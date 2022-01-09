package com.rchat.ui.components;

import com.formdev.flatlaf.FlatLightLaf;
import com.rchat.ui.exceptions.TooLongException;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class TextBubble extends JPanel {

    public static final int MAX_SINGLE_LINE_CHARACTERS = 40;
    public static final int COLUMNS = 30;

    private static final Color BACKGROUND_COLOR_DARK = new Color(46, 44, 44);
    private static final Color FOREGROUND_COLOR_DARK = new Color(168, 168, 168);

    private static final Color BACKGROUND_COLOR_LIGHT = new Color(215, 215, 215);
    private static final Color FOREGROUND_COLOR_LIGHT = new Color(0, 0, 0);

    private Font font;
    private String sender;
    private String message;

    private JButton senderInfoButton;
    private JTextField singleLineMessage;
    private JTextArea multilineMessage;

    private JTextComponent textComponent;

    public TextBubble(String sender, String message, boolean singleLineCentered) {
        setLayout(new BorderLayout());

        font = new Font("Helvetica", Font.PLAIN, 14);
        this.sender = sender;
        this.message = message;

        initComponents(singleLineCentered);
    }

    private void initComponents(boolean singleLineCentered) {
        if (singleLineCentered) {
            if (message.length() >= MAX_SINGLE_LINE_CHARACTERS)
                throw new TooLongException(message);
            singleLineMessage = new JTextField(message);
            singleLineMessage.setFont(font);
            singleLineMessage.setEditable(false);
            singleLineMessage.setHorizontalAlignment(JTextField.CENTER);
            textComponent = singleLineMessage;
        } else {
            multilineMessage = new JTextArea();
            multilineMessage.setFont(font);
            multilineMessage.setColumns(COLUMNS);
            multilineMessage.setEditable(false);
            multilineMessage.setLineWrap(true);
            multilineMessage.setWrapStyleWord(true);
            textComponent = multilineMessage;
        }
        textComponent.setText(message);
        textComponent.setMargin(new Insets(0, 3, 0, 0));
        add(textComponent, BorderLayout.CENTER);

        if (sender != null) {
            senderInfoButton = new JButton(sender);
            senderInfoButton.setBorderPainted(false);
            senderInfoButton.setMargin(new Insets(0, 2, 0, 0));
            senderInfoButton.setHorizontalAlignment(JButton.LEFT);
            senderInfoButton.setFont(font.deriveFont(Font.BOLD));

            add(senderInfoButton, BorderLayout.NORTH);
        }
        pickUI();
    }

    private void pickUI() {
        if (textComponent != null) {
            if (UIManager.getLookAndFeel() instanceof FlatLightLaf) {
                setBackground(BACKGROUND_COLOR_LIGHT);

                if (senderInfoButton != null) {
                    senderInfoButton.setBackground(BACKGROUND_COLOR_LIGHT);
                    senderInfoButton.setForeground(FOREGROUND_COLOR_LIGHT);
                }
                textComponent.setBackground(BACKGROUND_COLOR_LIGHT);
                textComponent.setForeground(FOREGROUND_COLOR_LIGHT);
            } else {
                setBackground(BACKGROUND_COLOR_DARK);

                if (senderInfoButton != null) {
                    senderInfoButton.setBackground(BACKGROUND_COLOR_DARK);
                    senderInfoButton.setForeground(FOREGROUND_COLOR_DARK);
                }
                textComponent.setBackground(BACKGROUND_COLOR_DARK);
                textComponent.setForeground(FOREGROUND_COLOR_DARK);
            }
        }
    }

    @Override
    public void updateUI() {
        pickUI();
        super.updateUI();
    }

}
