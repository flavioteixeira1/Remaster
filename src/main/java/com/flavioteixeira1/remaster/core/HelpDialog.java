package com.flavioteixeira1.remaster.core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Help / About dialog moved to its own class (modal), similar to joystick config dialog.
 */
public class HelpDialog extends JDialog {
    private JTextArea text;
    private JButton bt_ok;

    public HelpDialog(Frame owner) {
        super(owner, "Remaster - Sobre o programa...", true);
        initialize();
    }

    private void initialize() {
        bt_ok = new JButton("Ok");
        bt_ok.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { setVisible(false); } });

        JScrollPane scrollpanel = new JScrollPane();
        text = new JTextArea();
        text.setEditable(false);
        scrollpanel.setViewportView(text);

        text.append(Remaster.APPNAME + " - Emulador de SEGA Master System feito em Java.");
        text.append("\n--------------------------------------------------------------------------------------------");
        text.append("\n Adaptado por Flávio Augusto Teixeira - flavioteixeira1@gmail.com");
        text.append("\n repositório github:  https://github.com/flavioteixeira1/Remaster ");
        text.append("\n v0.02 - suporte a joystick    ; Pausa com nmi do Z80 - original do master system ");
        text.append("\n--------------------------------------------------------------------------------------------");
        text.append("\nDesenvolvido em 2003-2004 por André Luiz Veltroni Sanches (alvs).");
        text.append("\nE-mail: andre.alvs@gmail.com");
        text.append("\nICQ: 69444232");
        text.append("\n");
        text.append("\n:: Colaboradores ::");
        text.append("\n--------------------------");
        text.append("\n  - Marcelo Abreu (skewer) - conceitos e técnicas.");
        text.append("\n  - gamer_boy - teste.");
        text.append("\n  - Todos do canal #emuroms na Brasnet (irc.brasnet.org)");
        text.append("\n  - Todos do forum de desenvolvimento SMSPower (www.smspower.org)");
        text.append("\n");
        text.append("\n:: Requisitos básicos ::");
        text.append("\n--------------------------------");
        text.append("\n  - Processador de 1 Ghz ou superior;");
        text.append("\n  - 128Mb de memória RAM;");
        text.append("\n  - Java Runtime Environment versão 1.3.2 ou superior;");
        text.append("\n");
        text.append("\n:: Como jogar ::");
        text.append("\n---------------------");
        text.append("\n  Os joysticks são emulados somente no teclado, na seguinte");
        text.append("\n  configuração:");
        text.append("\n  - Tecla Z: botão 2 do joystick 1;");
        text.append("\n  - Tecla X: botão 1 do joystick 1;");
        text.append("\n  - ESC: Reset;");
        text.append("\n  - Barra de espaço: Interrompe/continua execução;");
        text.append("\n  - Tecla P: envia NMI (Pause via NMI) — pode ser mapeada no joystick");
        text.append("\n");
        text.append("\n:: Características das versões ::");
        text.append("\n--------------------------------------------");
        text.append("\n*** v0.01:");
        text.append("\n  - Melhoras na criação do buffer gráfico, compativel com as configurações");
        text.append("\n    atuais de cores do desktop.");
        text.append("\n  - Melhora no esquema de sincronização do som com a jogabilidade.");
        text.append("\n  - Versão final para a entrega do Trabalho de Conclusão de Curso.");
        text.append("\n");
        text.append("\n*** v0.00:");
        text.append("\n  - Atualmente, o Remaster emula somente o master system na versão");
        text.append("\n    NTSC 224x190.");
        text.append("\n  - Emulaçãoo do canal gerador de ruido branco ainda nao implementado");
        text.append("\n  - Compatibilidade de aproximadamente 60% dos jogos.");		
        text.append("\n  - Primeiro lançamento privado, entregue somente para beta-testers.");
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