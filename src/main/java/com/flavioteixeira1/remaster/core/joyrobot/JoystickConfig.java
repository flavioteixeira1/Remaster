package com.flavioteixeira1.remaster.core.joyrobot;

//Classe proveniente do vNes-Desktop

import java.util.prefs.Preferences;
import java.awt.event.KeyEvent;

public class JoystickConfig {
    private static final String PREFS_NODE = "com/remaster/joystick"; //isso deve mudar
    private Preferences prefs;
    
    public JoystickConfig() {
        prefs = Preferences.userRoot().node(PREFS_NODE);
    }
    
    public void saveButtonMapping(int buttonIndex, int keyCode) {
        prefs.putInt("button_" + buttonIndex, keyCode);
    }
    
    public int loadButtonMapping(int buttonIndex, int defaultKey) {
        return prefs.getInt("button_" + buttonIndex, defaultKey);
    }
    
    public void loadConfig(JoystickManager joystickManager) {
        // Carregar mapeamentos salvos (agora com o botão 4 reservado para Pause/NMI)
	//isso deve mudar
        joystickManager.setButtonMapping(0, loadButtonMapping(0, KeyEvent.VK_Z));
        joystickManager.setButtonMapping(1, loadButtonMapping(1, KeyEvent.VK_X));
        joystickManager.setButtonMapping(2, loadButtonMapping(2, KeyEvent.VK_ENTER));
        joystickManager.setButtonMapping(3, loadButtonMapping(3, KeyEvent.VK_CONTROL));
        // Botão extra: Pause via NMI (tecla P por padrão)
        joystickManager.setButtonMapping(4, loadButtonMapping(4, KeyEvent.VK_P));
    }
}