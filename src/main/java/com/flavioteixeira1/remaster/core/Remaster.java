package com.flavioteixeira1.remaster.core;

import java.awt.*;
import java.awt.event.*;
import java.util.prefs.Preferences;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;


public final class Remaster extends Frame {
  Remaster remaster;
  MainThread mainloop;
  Cartridge cart;
  Joystick joy;
  MemoryManager memory;
  VDP vdp;
  PSG psg;
  Ports ports;
  EZ80 z80;
  Screen screen;
  Debugger debugger;
  DrawSurface drawsurface;

  VRAMViewer vramviewer;
  CRAMViewer cramviewer;
  
  MenuBar menubar;
  Menu file;
  Menu emulator;
  Menu display;
  Menu sound;
  Menu help;
  Menu joystick; 
  HelpDialog helpDialog;
  JoystickConfigWindow joystickConfigWindow;
  
  // Recent files
  private Menu recentMenu;
  private final int MAX_RECENTS = 8;
  private final Preferences prefs = Preferences.userNodeForPackage(Remaster.class);
  private final String PREF_KEY_PREFIX = "recent.";

  static String APPNAME = "Remaster KEys: Z, X, Enter, Ctrl";
  
  public Remaster() {
    super(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
    this.remaster = this;
    
    System.out.println(Remaster.APPNAME);

    setLayout(new BorderLayout());
    setTitle(Remaster.APPNAME);
    setLocation(100, 50);
    setSize(264,238);


    // create drawing surface panel and add it to the main frame
    drawsurface = new DrawSurface(this);
    screen = new Screen(drawsurface, 256, 192);
    screen.clearBuffer();
    add(drawsurface, BorderLayout.CENTER);
    
    // Set up emulation classes 
    joy = new Joystick();
    cart = new Cartridge();
    memory = new MemoryManager(cart);
    vdp = new VDP(screen);
    psg = new PSG();
    ports = new Ports(vdp, psg, joy);
    debugger = new Debugger();
    z80 = new EZ80(memory, ports, vdp, debugger);
    vramviewer = new VRAMViewer(vdp);
    cramviewer = new CRAMViewer(vdp);

    //create JoystickConfigWindow (using Jkeyboard classes)
    joystickConfigWindow = new JoystickConfigWindow();

    // create Help Dialog (moved out to its own class)
    helpDialog = new HelpDialog(this);

    // create and set up MenuBar
    menubar = setup_MenuBar();
    setMenuBar(menubar);
    setVisible(true);
    
    // Load recents from preferences (after UI created)
    rebuildRecentMenuItems();
    
/*    // --------- Debug - instruction trace output stream -------
    java.io.PrintStream trace = null;
    try {
      trace = new java.io.PrintStream(new java.io.FileOutputStream("trace.log", false));
    }
    catch(java.io.FileNotFoundException e) {}
    System.setOut(trace);
    // --------- end of temporary code block -----------------
*/    
   // Setup action listeners
    addWindowListener(new WindowAdapter() { 
  public void windowClosing(WindowEvent e) { 
        exit(); 
      } 
    });
  }

  
  // Adds a path to recents (moves to top). Persists to prefs.
  private void addToRecents(String fullPath) {
    if (fullPath == null) return;
    List<String> list = loadRecents();
    list.remove(fullPath); // deduplicate
    list.add(0, fullPath);
    while (list.size() > MAX_RECENTS) list.remove(list.size() - 1);
    saveRecents(list);
    rebuildRecentMenuItems();
  }

  private List<String> loadRecents() {
    List<String> list = new ArrayList<>();
    for (int i = 0; i < MAX_RECENTS; i++) {
      String v = prefs.get(PREF_KEY_PREFIX + i, null);
      if (v != null && v.trim().length() > 0) list.add(v);
    }
    return list;
  }

  private void saveRecents(List<String> list) {
    // clear first
    for (int i = 0; i < MAX_RECENTS; i++) prefs.remove(PREF_KEY_PREFIX + i);
    // store
    for (int i = 0; i < list.size() && i < MAX_RECENTS; i++) {
      prefs.put(PREF_KEY_PREFIX + i, list.get(i));
    }
    try {
      prefs.flush();
    } catch (Exception e) {}
  }

  // Rebuild the Recent submenu UI
  private void rebuildRecentMenuItems() {
    if (file == null) return; // menu not yet created
    if (recentMenu == null) {
      recentMenu = new Menu("Recentes");
      // Insert recentMenu after "Abrir ROM" (we added openRom first in setup_MenuBar)
      // We'll remove any existing recentMenu before adding to avoid duplicates.
    } else {
      // remove existing recentMenu from file if present
      for (int i = 0; i < file.getItemCount(); i++) {
        MenuItem mi = file.getItem(i);
        if (mi instanceof Menu && ((Menu)mi).getLabel().equals("Recentes")) {
          file.remove(i);
          break;
        }
      }
      recentMenu.removeAll();
    }

    List<String> recents = loadRecents();
    if (recents.isEmpty()) {
      MenuItem none = new MenuItem("(nenhum)");
      none.setEnabled(false);
      recentMenu.add(none);
    } else {
      for (String path : recents) {
        final String p = path;
        String name = new java.io.File(path).getName();
        MenuItem recentItem = new MenuItem(name);
        recentItem.setEnabled(true);
        recentItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            loadRomFromPath(p);
          }
        });
        recentMenu.add(recentItem);
      }
      recentMenu.addSeparator();
      MenuItem clear = new MenuItem("Limpar lista de recentes");
      clear.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          saveRecents(new ArrayList<String>());
          rebuildRecentMenuItems();
        }
      });
      recentMenu.add(clear);
    }

    // Insert recentMenu after the first item (Abrir ROM)
    // find position of "Abrir ROM"
    int insertPos = -1;
    for (int i = 0; i < file.getItemCount(); i++) {
      MenuItem mi = file.getItem(i);
      if (mi.getLabel() != null && mi.getLabel().equals("Abrir ROM")) {
        insertPos = i + 1;
        break;
      }
    }
    if (insertPos >= 0 && insertPos <= file.getItemCount()) {
      file.insert(recentMenu, insertPos);
    } else {
      file.add(recentMenu);
    }
  }

  // Attempts to load a ROM file from a full path and start emulation (used by Recent menu)
  private void loadRomFromPath(String fullPath) {
    if (fullPath == null) return;
    // unload any currently loaded cart
    if (z80 != null) z80.msg.setVisible(false);
    cart.unload();
    boolean ok = cart.loadFromFile(fullPath);
    if (!ok) {
      JOptionPane.showMessageDialog(this, "Não foi possível carregar o arquivo:\n" + fullPath, "Erro", JOptionPane.ERROR_MESSAGE);
      return;
    }
    // ensure recent list updated
    addToRecents(fullPath);
    // start emulation
    startEmulation();
    drawsurface.requestFocus();
  }
  
  public void startEmulation() {
    setTitle(Remaster.APPNAME + " - " + cart.getFileName());
    
    // Enable menu items
    for(int i=0; i < file.getItemCount(); i++)     file.getItem(i).enable();
    for(int i=0; i < emulator.getItemCount(); i++) emulator.getItem(i).enable();
    for(int i=0; i < joystick.getItemCount(); i++) joystick.getItem(i).enable();    
    if(mainloop != null) { mainloop.stopEmulation(); mainloop = null; }

    mainloop = new MainThread(screen, cart, memory, vdp, psg, ports, joy, z80, debugger, vramviewer, cramviewer);
    mainloop.SMS_reset();
    
    mainloop.start();   // Start emulation
  }
  
  public void exit() {
    JoystickManager.globalCleanup();

  	if(mainloop != null) {
  		mainloop.stopEmulation();
  		//memory.dumpMemory();
  		//vdp.dumpMemory();
  	}
  	System.exit(0);
  }
  
  public void paint(Graphics g) {
  	if(mainloop != null) screen.drawScreen(0, 0);
  	else {
  		drawsurface.getGraphics().setColor(new Color(0));
  		drawsurface.getGraphics().fillRect(0, 0, drawsurface.getWidth(), drawsurface.getHeight());
  		drawsurface.repaint();
  	}
  }
  
  public MenuBar setup_MenuBar() {
  	MenuBar menubar = new MenuBar();
  	
    file = new Menu("Arquivo"); // Set up File menu
    MenuItem openRom  = new MenuItem("Abrir ROM");
    openRom.setShortcut(new MenuShortcut(KeyEvent.VK_O));
    MenuItem closeRom = new MenuItem("Fechar ROM");
    closeRom.setShortcut(new MenuShortcut(KeyEvent.VK_C));
    closeRom.disable();
    MenuItem exit =     new MenuItem("Sair");
    exit.setShortcut(new MenuShortcut(KeyEvent.VK_X));
    file.add(openRom);
    // recentMenu will be injected by rebuildRecentMenuItems()
    file.add(closeRom);
    file.addSeparator();
    file.add(exit);
    menubar.add(file);
    
    emulator = new Menu("Emulação"); // Set up Emulator menu
    CheckboxMenuItem pauseresume = new CheckboxMenuItem("Pause");
    pauseresume.disable();
    CheckboxMenuItem vramview =    new CheckboxMenuItem("Habilitar visualizador de tiles (VRAM)");
    vramview.disable();
    CheckboxMenuItem cramview =    new CheckboxMenuItem("Habilitar visualizador da paleta de cores (CRAM)");
    cramview.disable();
    CheckboxMenuItem debug =       new CheckboxMenuItem("Habilitar Debugger");
    debug.disable();
    emulator.add(pauseresume);
    
    // New menu item: Pause via NMI (mapped to key 'P')
    MenuItem pauseNMI = new MenuItem("Pause (NMI) - tecla P");
    pauseNMI.setShortcut(new MenuShortcut(KeyEvent.VK_P));
    pauseNMI.setEnabled(false);
    emulator.add(pauseNMI);
    
    emulator.addSeparator();
    emulator.add(vramview);
    emulator.add(cramview);
    emulator.addSeparator();
    emulator.add(debug);
    menubar.add(emulator);
    
    display = new Menu("Tela");
    MenuItem fullscreen = new MenuItem("Tela cheia");
    fullscreen.setShortcut(new MenuShortcut(KeyEvent.VK_ENTER));
    MenuItem originalsize = new MenuItem("Tamanho original");
    originalsize.setShortcut(new MenuShortcut(KeyEvent.VK_1));
    MenuItem doublesize = new MenuItem("Tamanho dobrado");
    doublesize.setShortcut(new MenuShortcut(KeyEvent.VK_2));
    display.add(fullscreen);
    display.addSeparator();
    display.add(originalsize);
    display.add(doublesize);
    menubar.add(display);
    
    sound = new Menu("Som");
    CheckboxMenuItem enablesound = new CheckboxMenuItem("Ligar/desligar som");
    enablesound.setShortcut(new MenuShortcut(KeyEvent.VK_S));
    enablesound.setState(psg.enabled);
    CheckboxMenuItem crappysync = new CheckboxMenuItem("Sincronizar som (experimental)");
    crappysync.setState(psg.crappy_sync);
    CheckboxMenuItem soundchan0 = new CheckboxMenuItem("Ligar/Desligar canal 0");
    soundchan0.setState(psg.chan0);
    CheckboxMenuItem soundchan1 = new CheckboxMenuItem("Ligar/Desligar canal 0");
    soundchan1.setState(psg.chan1);
    CheckboxMenuItem soundchan2 = new CheckboxMenuItem("Ligar/Desligar canal 0");
    soundchan2.setState(psg.chan2);
    sound.add(enablesound);
    sound.add(crappysync);
    sound.addSeparator();
    sound.add(soundchan0);
    sound.add(soundchan1);
    sound.add(soundchan2);  
    menubar.add(sound);
    
    help = new Menu("Ajuda"); // Set up help menu
    MenuItem about = new MenuItem("Sobre");
    help.add(about);
    menubar.add(help);
    
    // File Menu actions
    openRom.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
    	if(z80 != null) z80.msg.setVisible(false);
    	cart.unload();
    	cart.showOpenDialog(remaster);
        if(cart.isLoaded()) { 
            // remember recent
            addToRecents(cart.getFullPath());
            emulator.getItem(0).setLabel("Pause emulation"); 
            startEmulation(); 
        } 
        drawsurface.requestFocus(); } } );
    closeRom.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
    	cart.unload();
    	screen.clearBuffer();
    	if(mainloop != null) { mainloop.stopEmulation(); mainloop = null; }
    	setTitle(Remaster.APPNAME);
    	drawsurface.requestFocus(); } } );
    exit.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { exit(); } } );
    // Emulation menu
    pauseresume.addItemListener(new ItemListener() { public void itemStateChanged(ItemEvent e) {
		if(mainloop != null) { mainloop.stopEmulation(); mainloop = null; drawsurface.requestFocus(); }
		else { mainloop = new MainThread(screen, cart, memory, vdp, psg, ports, joy, z80, debugger, vramviewer, cramviewer); mainloop.start(); emulator.getItem(0).setLabel("Pausar emulação"); drawsurface.requestFocus(); } } } );
    // Action for Pause (NMI) menu item: request NMI in a thread-safe way
    pauseNMI.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (mainloop != null) {
                mainloop.requestNMI();
            } else if (z80 != null) {
                synchronized (z80) { z80.nmi(); }
            }
            // Ensure GUI focus returns so keyboard mappings continue to work
            drawsurface.requestFocus();
        }
    });
    vramview.addItemListener(new ItemListener() { public void itemStateChanged(ItemEvent e) {
    	if(vramviewer.enabled) { vramviewer.toggleEnabled(); drawsurface.requestFocus(); }
    	else { vramviewer.toggleEnabled(); drawsurface.requestFocus(); } } } );
    cramview.addItemListener(new ItemListener() { public void itemStateChanged(ItemEvent e) {
    	if(cramviewer.enabled) { cramviewer.toggleEnabled(); drawsurface.requestFocus(); }
    	else { cramviewer.toggleEnabled(); drawsurface.requestFocus(); } } } );
    debug.addItemListener(new ItemListener() { public void itemStateChanged(ItemEvent e) {
    	if(debugger.enabled) { debugger.toggleEnabled(); drawsurface.requestFocus(); }
    	else { debugger.toggleEnabled(); drawsurface.requestFocus(); } } } );
    // Display menu
    fullscreen.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { screen.toggleFullScreen(remaster);	} } );
    originalsize.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { setSize(264, 238); validate(); } } );
    doublesize.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { setSize(520, 430); validate(); } } );
    // Sound menu
    enablesound.addItemListener(new ItemListener() { public void itemStateChanged(ItemEvent e) { psg.enabled = !psg.enabled; } } );
    crappysync.addItemListener(new ItemListener() { public void itemStateChanged(ItemEvent e) { psg.crappy_sync = !psg.crappy_sync; } } );
    soundchan0.addItemListener(new ItemListener() { public void itemStateChanged(ItemEvent e) { psg.chan0 = !psg.chan0; } } );    
    soundchan1.addItemListener(new ItemListener() { public void itemStateChanged(ItemEvent e) { psg.chan1 = !psg.chan1; } } );    
    soundchan2.addItemListener(new ItemListener() { public void itemStateChanged(ItemEvent e) { psg.chan2 = !psg.chan2; } } );
    
    //Configuração de Joystick
    joystick = new Menu("Joystick");
    
    MenuItem joystickConfig = new MenuItem("Joystick Config");    

    joystick.addSeparator();
    joystick.add(joystickConfig);
    menubar.add(joystick);
    
    // Help menu
    about.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { helpDialog.setVisible(true); } } );
    
    //Joystick Config Menu
   joystickConfig.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { joystickConfigWindow.setVisible(true); } } );
        
	
  
    return menubar;
  }

   
  

  
  public static void main(String args[])
  {
    new Remaster();
  }
}