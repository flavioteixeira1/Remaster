package com.flavioteixeira1.remaster.core.joyrobot;

/**
 * Utilitário para mapeamento de controles do Master System
 */
public class SMSInputMapper {
    
    // Mapeamento padrão para Master System
    public static final int SMS_BUTTON_1 = 0;  // Fire A (X no teclado)
    public static final int SMS_BUTTON_2 = 1;  // Fire B (Z no teclado)
    public static final int SMS_START    = 2;  // Start
    public static final int SMS_RESET    = 3;  // Reset
    
    // Teclas padrão do Master System
    public static int getDefaultKey(int playerId, int smsButton) {
        if (playerId == 0) {
            switch(smsButton) {
                case SMS_BUTTON_1: return 88;   // X
                case SMS_BUTTON_2: return 90;   // Z
                case SMS_START:    return 10;   // Enter
                case SMS_RESET:    return 27;   // ESC
                default: return -1;
            }
        } else {
            // Player 2 - usar teclado numérico
            switch(smsButton) {
                case SMS_BUTTON_1: return 103;  // Numpad 7
                case SMS_BUTTON_2: return 105;  // Numpad 9
                case SMS_START:    return 97;   // Numpad 1
                case SMS_RESET:    return 99;   // Numpad 3
                default: return -1;
            }
        }
    }
    
    // Direcionais
    public static int[] getDefaultDirectionalKeys(int playerId) {
        if (playerId == 0) {
            return new int[]{37, 39, 38, 40}; // LEFT, RIGHT, UP, DOWN
        } else {
            return new int[]{100, 102, 104, 98}; // Numpad 4, 6, 8, 2
        }
    }
}