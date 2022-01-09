package com.ice.ui.panels;

import com.eventsystem.components.EventBus;
import com.eventsystem.events.Event;
import com.ice.events.net.AuthenticationEvent;
import com.ice.events.net.ConnectEvent;
import com.ice.events.net.UnableToConenctEvent;
import com.ice.models.DataAccess;
import com.ice.models.User;
import com.ice.net.exceptions.AuthenticationException;
import com.ice.net.services.ConnectionController;
import com.ice.net.utils.AuthenticationResult;
import com.ice.ui.GUIManager;
import com.ice.ui.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BasePanel extends UI {

    private ActionHandler actionHandler;

    private JLabel connectionStatusLabel;

    private JLabel hostLabel;
    private JTextField hostField;
    private JLabel nicknameLabel;
    private JTextField nicknameField;

    private JButton connectButton;

    public BasePanel(EventBus eventBus, DataAccess dataAccess, GUIManager guiManager) {
        super(eventBus, dataAccess, guiManager);
    }

    @Override
    public void setup() {
        actionHandler = new ActionHandler();

        setLayout(new FlowLayout(FlowLayout.RIGHT));
        setPreferredSize(new Dimension(395, 120));

        hostLabel = new JLabel("Host:          ");
        hostLabel.setFont(new Font("Helvetica", Font.BOLD, 14));
        add(hostLabel);

        hostField = new JTextField("127.0.0.1");
        hostField.setHorizontalAlignment(JTextField.CENTER);
        hostField.setFont(new Font("Helvetica", Font.BOLD, 15));
        hostField.setPreferredSize(new Dimension(300, 25));
        add(hostField);

        nicknameLabel = new JLabel("Nickname:");
        nicknameLabel.setFont(new Font("Helvetica ", Font.BOLD, 15));
        add(nicknameLabel);

        nicknameField = new JTextField();
        nicknameField.setHorizontalAlignment(JTextField.CENTER);
        nicknameField.setFont(new Font("Helvetica ", Font.BOLD, 15));
        nicknameField.setPreferredSize(new Dimension(300, 25));
        add(nicknameField);

        connectButton = new JButton("Connect");
        connectButton.setFont(new Font("Helvetica ", Font.BOLD, 15));
        connectButton.setPreferredSize(new Dimension(300, 25));
        connectButton.addActionListener(actionHandler);
        add(connectButton);

        connectionStatusLabel = new JLabel("Not Connected!");
        connectionStatusLabel.setForeground(Color.BLUE);
        connectionStatusLabel.setFont(new Font("Helvetica ", Font.BOLD, 15));
        add(connectionStatusLabel);
    }

    @Override
    public void teardown() {}

    private void connect() {
        connectionStatusLabel.setForeground(Color.BLUE);
        connectionStatusLabel.setText("Trying To Connect...");

        User user;

        try {
            user = new User(nicknameField.getText());
        } catch (AuthenticationException e) {
            e.printStackTrace();
            handleAuthenticationEvent(new AuthenticationEvent(e.getAuthenticationResult()));
            return;
        }
        dataAccess.setUser(user);

        eventBus.publish(new ConnectEvent(hostField.getText()), ConnectionController.CC_TOPIC);
    }

    public void handleUnableToConnectEvent(UnableToConenctEvent event) {
        connectionStatusLabel.setForeground(Color.RED);
        connectionStatusLabel.setText(
                "[" + event.getThrowable().getClass().getSimpleName() + "] " +
                        event.getThrowable().getLocalizedMessage());
    }

    private void handleAuthenticationEvent(AuthenticationEvent event) {
        AuthenticationResult result = event.getAuthenticationResult();

        switch (result) {
            case SUCCESS:
                connectionStatusLabel.setForeground(Color.GREEN);
                connectionStatusLabel.setText("Session with " + hostField.getText() + " Started!");

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                guiManager.setCurrentPanel(new ConversationPanel(eventBus, dataAccess, guiManager));
                break;
            case BLANK_OR_EMPTY:
                connectionStatusLabel.setText("Name can't be BLANK or EMPTY!");
                break;
            case TOO_SHORT:
                connectionStatusLabel.setText("Nickname is too short *minimum-2*!");
                break;
            case TOO_LONG:
                connectionStatusLabel.setText("Nickname is too long *maximum-12*!");
                break;
            case INVALID:
                connectionStatusLabel.setText("Invalid characters are present in nickname!");
                break;
            case UNAVAILABLE:
                connectionStatusLabel.setText("This nickname is unavailable!");
        }
        connectionStatusLabel.setForeground(Color.RED);
    }

    @Override
    public void handle(Event event) {
        if (event instanceof UnableToConenctEvent) {
            handleUnableToConnectEvent((UnableToConenctEvent) event);
        } else if (event instanceof AuthenticationEvent) {
            handleAuthenticationEvent((AuthenticationEvent) event);
        }
    }

    private class ActionHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(connectButton)) {
                connect();
            }
        }
    }
}
