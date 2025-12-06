package com.flavioteixeira1.remaster.core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class KeyCaptureDialog extends JDialog {
    private int capturedKey = -1;
    ConfigDialog configDialog;

    private KeyCaptureDialog(ConfigDialog configDialog, String label) {
        super(configDialog, "Pressione uma tecla...", true);
        setLayout(new BorderLayout());
        JLabel instrucao = new JLabel("Pressione a tecla para " + label + " (ESC para cancelar)", JLabel.CENTER);
        add(instrucao, BorderLayout.CENTER);
        setSize(300, 130);
        setLocationRelativeTo(configDialog);

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    capturedKey = -1;
                    dispose();
                } else {
                    capturedKey = e.getKeyCode();
                    dispose();
                }
            }
        });
        setFocusable(true);
        requestFocusInWindow();
    }

    public static int capture(ConfigDialog configDialog, String label) {
        KeyCaptureDialog dlg = new KeyCaptureDialog(configDialog, label);
        dlg.setVisible(true);
        return dlg.capturedKey;
    }
}