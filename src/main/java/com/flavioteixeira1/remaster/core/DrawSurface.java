package com.flavioteixeira1.remaster.core;

import java.awt.*;
import java.awt.event.*;

public final class DrawSurface extends Panel {
	Remaster parent;
	
	DrawSurface(Remaster p) {
		super();
		
		this.parent = p;
		
		 // painel focável para receber eventos de teclado
	    setFocusable(true);
	    requestFocusInWindow();
	    
		 addKeyListener(new KeyAdapter() {
	        public void keyPressed(KeyEvent e) {
	        	handleKeyEvent(e, false);
	        }
	      
	        public void keyReleased(KeyEvent e) {
	        	handleKeyEvent(e, true);
	        }
	        
	        private void handleKeyEvent(KeyEvent e, boolean released) {
	        	switch(e.getKeyCode()) {
	        		case 37:   // Key LEFT
	        			if(released) parent.joy.byte1 |= Joystick.JOY1_LEFT;
	        			else parent.joy.byte1 &= ~Joystick.JOY1_LEFT;
	        			break;
	        		case 39:   // Key RIGHT
	        			if(released) parent.joy.byte1 |= Joystick.JOY1_RIGHT;
	        			else parent.joy.byte1 &= ~Joystick.JOY1_RIGHT;
	        			break;
	        		case 38:   // Key UP
	        			if(released) parent.joy.byte1 |= Joystick.JOY1_UP;
	        			else parent.joy.byte1 &= ~Joystick.JOY1_UP;
	        			break;
	        		case 40:   // Key DOWN
	        			if(released) parent.joy.byte1 |= Joystick.JOY1_DOWN;
	        			else parent.joy.byte1 &= ~Joystick.JOY1_DOWN;
	        			break;
	        		case 90:   // Key Z
	        			if(released) parent.joy.byte1 |= Joystick.JOY1_FIREB;
	        			else parent.joy.byte1 &= ~Joystick.JOY1_FIREB;
	        			break;
	        		case 88:   // Key X
	        			if(released) parent.joy.byte1 |= Joystick.JOY1_FIREA;
	        			else parent.joy.byte1 &= ~Joystick.JOY1_FIREA;
	        			break;
	        		case 27:   // ESC: Soft reset
	        			if(released) parent.joy.byte2 |= Joystick.RESET;
	        			else parent.joy.byte2 &= ~Joystick.RESET;
	        			break;
	        		case 32:   // SPACE BAR: Hard pause
	        			if(!released) {
		        			if(parent.mainloop != null) {
		        				parent.mainloop.stopEmulation();
		        				parent.mainloop = null;
		        			}
		        			else {
		        				parent.mainloop = new MainThread(parent.screen, parent.cart, parent.memory, parent.vdp, parent.psg, parent.ports, parent.joy, parent.z80, parent.debugger, parent.vramviewer, parent.cramviewer);
		        			    parent.mainloop.start();
		        			}
	        			}
	        			break;
	        		case 107:  // Numerical PLUS key: Increase frame skip
	        			if(!released && parent.vdp != null) parent.vdp.frameskip++;
	        			break;
	        		case 109:  // Numerical MINUS key: Decrease frame skip
	        			if(!released && parent.vdp != null && parent.vdp.frameskip > 1) parent.vdp.frameskip--;
	        			break;
	        		case 119:  // F8 key - toggle crappy sound synchronization on/off
	        			if(!released) parent.psg.crappy_sync = !parent.psg.crappy_sync;
	        			break;
	        		default:
	        			// Ignorar outras teclas
	        	}
	        }
	    });
	}

	// Método para recuperar o foco quando necessário
	public void regainFocus() {
		requestFocusInWindow();
	}
	        
	
	public void paint(Graphics g) {
		parent.screen.drawScreen(0, 0);
	}

	public void paintAll(Graphics g) {}
	public void repaint() {}
	public void repaint(long tm, int x, int y, int width, int height){}
	public void repaint(int x, int y, int width, int height){}
	
}