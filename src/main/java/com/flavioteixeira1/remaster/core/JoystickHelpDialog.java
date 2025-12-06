package com.flavioteixeira1.remaster.core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Help / About dialog moved to its own class (modal)
 */
public class JoystickHelpDialog extends JDialog {
    private JTextArea text;
    private JButton bt_ok;
    JoystickConfigWindow joystickConfigwindow;

    public JoystickHelpDialog(Frame owner) {
        super(owner, "Jkeyboard - Sobre o programa...", true);
        initialize();
    }

    private void initialize() {
        bt_ok = new JButton("Ok");
        bt_ok.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { setVisible(false); } });

        JScrollPane scrollpanel = new JScrollPane();
        text = new JTextArea();
        text.setEditable(false);
        scrollpanel.setViewportView(text);

        text.append(" - Jkeyboard Mapear eventos de Joystick para teclado similar ao QJoypad  feito em Java.");
        text.append("\n--------------------------------------------------------------------------------------------");
        text.append("\n Desenvolvido por Flávio Augusto Teixeira - flavioteixeira1@gmail.com");
        text.append("\n repositório github:  https://github.com/flavioteixeira1/Jkeyboard ");
        text.append("\n--------------------------------------------------------------------------------------------");
        text.append("\n");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.add(bt_ok);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollpanel, BorderLayout.CENTER);
        getContentPane().add(bottom, BorderLayout.SOUTH);

        setSize(450, 350);
        setResizable(false);
        setLocationRelativeTo(getOwner());

        addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { setVisible(false); } } );
    }
}