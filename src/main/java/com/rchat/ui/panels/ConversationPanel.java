package com.rchat.ui.panels;

import com.eventsystem.components.EventBus;
import com.eventsystem.events.Event;
import com.rchat.core.Initializer;
import com.rchat.events.net.DisconnectEvent;
import com.rchat.events.net.DisconnectedEvent;
import com.rchat.events.net.ResponseEvent;
import com.rchat.events.net.SendRequestEvent;
import com.rchat.models.DataAccess;
import com.rchat.net.messages.requests.AlignRequest;
import com.rchat.net.messages.requests.DeliverRequest;
import com.rchat.net.messages.responses.AlignResponse;
import com.rchat.net.messages.responses.MessageResponse;
import com.rchat.net.messages.responses.Response;
import com.rchat.net.messages.responses.ResponseAcronym;
import com.rchat.net.services.ConnectionController;
import com.rchat.net.services.Sender;
import com.rchat.net.utils.RoutingScheme;
import com.rchat.net.utils.ServiceDebugLogger;
import com.rchat.ui.GUIManager;
import com.rchat.ui.UI;
import com.rchat.ui.components.TextBubble;
import com.rchat.utils.ImageLoader;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ConversationPanel extends UI {

    private JFileChooser fileChooser;

    private JMenuItem cleanChatItem;

    private JMenu networkMenu;
    private JMenuItem networkStatusItem;
    private JMenuItem disconnectItem;

    private JMenu appearanceMenu;
    private JMenuItem changeChatBackgroundItem;

    private JPanel profilePanel;
    private JLabel nicknameLabel;

    private ArrayList<TextBubble> messages;
    private JPanel messagesAreaPanel;
    private JScrollPane messagesAreaPanelJScrollPane;

    private JPanel inputAreaPanel;
    private JTextArea inputArea;
    private JScrollPane inputAreaScrollPane;
    private JButton sendButton;

    private DefaultListModel<String> participantsModel;
    private JList<String> participantsView;
    private JScrollPane participantsViewScrollPane;

    private ActionHandler actionListener;
    private KeyHandler keyHandler;

    public ConversationPanel(EventBus eventBus, DataAccess dataAccess, GUIManager guiManager){
        super(eventBus, dataAccess, guiManager);
    }

    @Override
    public void setup() {
        actionListener = new ActionHandler();
        keyHandler = new KeyHandler();

        setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
        setPreferredSize(new Dimension(800, 600));

        fileChooser = new JFileChooser();

        initMenus();
        initProfileArea();
        initConversationArea();
        initParticipantsListArea();
        initInputArea();

        eventBus.publish(new SendRequestEvent(new AlignRequest()), Sender.SENDER_TOPIC);
    }

    private void initMenus() {
        JMenuBar menuBar = guiManager.getMenuBar();
        JMenuItem viewMenu = guiManager.getViewMenu();

        cleanChatItem = new JMenuItem("Delete Conversation");
        cleanChatItem.addActionListener(actionListener);
        viewMenu.add(cleanChatItem);

        networkMenu = new JMenu("Network");
        networkStatusItem = new JMenuItem("Status");
        networkStatusItem.addActionListener(actionListener);
        networkMenu.add(networkStatusItem);
        menuBar.add(networkMenu);

        appearanceMenu = new JMenu("Appearance");

        changeChatBackgroundItem = new JMenuItem("Change Chat Background");
        changeChatBackgroundItem.addActionListener(actionListener);
        appearanceMenu.add(changeChatBackgroundItem);
        menuBar.add(appearanceMenu);

        disconnectItem = new JMenuItem("Disconnect...");
        disconnectItem.addActionListener(actionListener);
        menuBar.add(disconnectItem);
    }

    private void initProfileArea() {
        profilePanel = new JPanel();
        profilePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        profilePanel.setPreferredSize(new Dimension(getPreferredSize().width, 30));
        add(profilePanel);

        nicknameLabel = new JLabel("Your Nickname - " + dataAccess.getUser().getNickname());
        profilePanel.add(nicknameLabel);
    }

    private void initConversationArea() {
        messages = new ArrayList<>();

        Dimension dimension = new Dimension(getPreferredSize().width - 180, getPreferredSize().height - 72);

        messagesAreaPanel = new JPanel() {

            @Override
            public void paintComponent(Graphics g) {
                BufferedImage background = dataAccess.getUser().getBackground();
                int y = messagesAreaPanelJScrollPane.getVerticalScrollBar().getValue();

                g.drawImage(background, 0, y, dimension.width, dimension.height, null);

                super.paintComponent(g);
            }
        };
        messagesAreaPanel.setLayout(new MigLayout(
                "",
                "[:" + (dimension.width - 28) + ":][right][center][left][l]"));
        messagesAreaPanel.setOpaque(false);

        messagesAreaPanelJScrollPane = new JScrollPane(messagesAreaPanel);

        messagesAreaPanelJScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        messagesAreaPanelJScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        messagesAreaPanelJScrollPane.setPreferredSize(dimension);
        add(messagesAreaPanelJScrollPane);
    }

    private void initParticipantsListArea() {
        participantsModel = new DefaultListModel<>();

        participantsView = new JList<>(participantsModel);
        participantsView.setFont(new Font("Helvetica", Font.BOLD, 16));

        participantsViewScrollPane = new JScrollPane(participantsView);
        participantsViewScrollPane.setPreferredSize(new Dimension(getPreferredSize().width - messagesAreaPanelJScrollPane.getPreferredSize().width - 10,
                messagesAreaPanelJScrollPane.getPreferredSize().height));
        participantsViewScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        participantsViewScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(participantsViewScrollPane, BorderLayout.EAST);
    }

    private void initInputArea() {
        inputArea = new JTextArea();
        inputArea.setFont(new Font("Helvetica", Font.PLAIN, 14));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.addKeyListener(keyHandler);

        inputAreaScrollPane = new JScrollPane(inputArea);
        inputAreaScrollPane.setPreferredSize(new Dimension(messagesAreaPanelJScrollPane.getPreferredSize().width, 38));
        inputAreaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        inputAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(inputAreaScrollPane);

        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Helvetica", Font.PLAIN, 16));

        sendButton.setPreferredSize(new Dimension(participantsViewScrollPane.getPreferredSize().width, 38));
        sendButton.addActionListener(actionListener);
        add(sendButton);
    }

    @Override
    public void teardown() {
        JMenuBar menuBar = guiManager.getMenuBar();
        menuBar.remove(networkMenu);
        menuBar.remove(appearanceMenu);
        menuBar.remove(disconnectItem);

        JMenu view = guiManager.getViewMenu();
        view.remove(cleanChatItem);
    }

    public void insertMessage(String sender, String message) {
        TextBubble bubble;

        if (sender == null) {
            bubble = new TextBubble(null, message.strip(), true);
            messagesAreaPanel.add(bubble, "align center, wrap");
        } else if (sender.equals("Me")) {
            bubble = new TextBubble(sender, message.strip(), false);
            messagesAreaPanel.add(bubble, "align left, wrap");
        } else {
            bubble = new TextBubble(sender, message.strip(), false);
            messagesAreaPanel.add(bubble, "align right, wrap");
        }
        revalidate();
    }

    public void updateParticipantsModel(ArrayList<String> participants) {
        // Add participants to the list
        for (String participant : participants) {
            // If the participants model contains this participant it doesn't add him
            if (!participantsModel.contains(participant)) {
                participantsModel.addElement(participant);
                insertMessage(null, participant + " connected to the server!");
            }
        }

        // Remove disconnected participants
        for (int i = 0; i < participantsModel.size(); i++) {
            // If the participants list contains the participant retrieved from the model then continue
            if (!participants.contains(participantsModel.get(i))) {
                insertMessage(null, participantsModel.get(i) + " disconnected from the server...");
                participantsModel.remove(i);
            }
        }
    }

    public void disconnect() {
        Object[] options = new Object[] { "Yes", "No" };

        int result = JOptionPane.showOptionDialog(
                this,
                "Are you sure you want to end this session?",
                "Disconnection...",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[1]);

        if (result == 0)
            eventBus.publish(new DisconnectEvent(), ConnectionController.CC_TOPIC);
    }

    public void handleDisconnectedEvent(DisconnectedEvent event) {
        if (event.getReason().equals(DisconnectedEvent.DisconnectionReason.REQUESTED)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Session with " + dataAccess.getSessionData().getHostAddress() + " terminated!",
                    "Session Terminated!",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Connection Lost!",
                    "Network Problem Occurred!",
                    JOptionPane.ERROR_MESSAGE);

            JOptionPane.showMessageDialog(
                    this,
                    "Session with " + dataAccess.getSessionData().getHostAddress() + " terminated!",
                    "Session Terminated!",
                    JOptionPane.INFORMATION_MESSAGE);
            guiManager.setCurrentPanel(new BasePanel(eventBus, dataAccess, guiManager));
        }
        guiManager.setCurrentPanel((UI) Initializer.getBean("BasePanel"));
    }

    @Override
    public void handle(Event event) {
        if (event instanceof ResponseEvent) {
            Response response = ((ResponseEvent) event).getResponse();

            switch (response.getType()) {
                case ResponseAcronym.ALIGN_RESPONSE:
                    AlignResponse alignResponse = (AlignResponse) response;
                    updateParticipantsModel(new ArrayList<>(List.of(alignResponse.getParticipantsList())));
                    ServiceDebugLogger.log(this.getClass(), "Updated participants list!");
                    break;
                case ResponseAcronym.MESSAGE_RESPONSE:
                    MessageResponse messageResponse = (MessageResponse) response;
                    insertMessage(messageResponse.getFrom(), messageResponse.getMessage());
                    break;
            }
        } else if (event instanceof DisconnectedEvent) {
            handleDisconnectedEvent((DisconnectedEvent) event);
        }
    }

    public class ActionHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source.equals(cleanChatItem)) {
                Object[] options = new Object[] { "Yes", "No" };

                int result = JOptionPane.showOptionDialog(
                        ConversationPanel.this,
                        "This action can't be undone!\n Are you sure you want to delete the " +
                                "Conversation for you?",
                        "Warning! Delete Conversation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options,
                        options[1]);

                if (result == 0) {
                    messages.clear();
                    messagesAreaPanel.removeAll();
                    messagesAreaPanel.repaint();
                }
            } else if (source.equals(networkStatusItem)) {
                JOptionPane.showMessageDialog(
                        null,
                        dataAccess.getSessionData(),
                        "Network Information",
                        JOptionPane.INFORMATION_MESSAGE);
            } else if (source.equals(changeChatBackgroundItem)) {
                fileChooser.updateUI();
                int result = fileChooser.showDialog(null, "Choose");

                if (result == JFileChooser.APPROVE_OPTION) {
                    BufferedImage background = ImageLoader.loadFromFileSystem(fileChooser.getSelectedFile());

                    if (background != null) {
                        dataAccess.getUser().setBackground(background);
                        repaint();
                    }
                }
            } else if (source.equals(disconnectItem)) {
                disconnect();
            } else if (source.equals(sendButton)) {
                String text = inputArea.getText();

                if (text.isEmpty() || text.isBlank()) {
                    inputArea.setText("");
                    return;
                }
                inputArea.setText("");
                insertMessage("Me", text);

                eventBus.publish(new SendRequestEvent(new DeliverRequest(text, RoutingScheme.BROADCAST)), Sender.SENDER_TOPIC);
            }
        }
    }

    private class KeyHandler extends KeyAdapter {

        private boolean alt;

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_ALT) {
                alt = true;
                return;
            }

            if (key ==  KeyEvent.VK_ENTER && alt)
                inputArea.setText(inputArea.getText() + "\n");
            else if (key == KeyEvent.VK_ENTER) {
                e.consume();
                sendButton.doClick();
            }

        }

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_ALT)
                alt = false;
        }

        @Override
        public void keyTyped(KeyEvent e) {
            /*
            Code for hebrew support
            ========================

            int key = e.getKeyChar();

            String input = inputArea.getText();


            if (input.length() == 0 && inputArea.getComponentOrientation() != ComponentOrientation.LEFT_TO_RIGHT) {
                if (key >= 1488 && key <= 1514)
                    inputArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            } else if (input.length() == 0)
                inputArea.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            */
        }
    }

}
