package com.ice.ui;

import com.eventsystem.components.EventBus;
import com.eventsystem.events.Event;
import com.eventsystem.subscriber.Subscriber;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.ice.net.services.MessageReader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Manages the GUI states and other GUI properties.
 */
public class GUIManager implements Subscriber {

	public static final String GUI_TOPIC = "GUI";

	private final EventBus eventBus;

	private final JFrame window;

	private final ActionHandler actionHandler;

	private final JMenuBar menuBar;

	private final JMenu viewMenu;
	private final JRadioButtonMenuItem changeThemeItem;

	// The panel that the user is currently see
	private UI currentPanel;

	public GUIManager(EventBus eventBus, @NotNull JFrame window) {
		this.eventBus = eventBus;
		eventBus.createTopic(GUI_TOPIC);
		eventBus.subscribe(this, GUI_TOPIC);
		eventBus.subscribe(this, MessageReader.RESPONSES_TOPIC);

		this.window = window;

		actionHandler = new ActionHandler();

		FlatLightLaf.setup();
		setFlatLAF(new FlatLightLaf());

		menuBar = new JMenuBar();

		viewMenu = new JMenu("View");
		changeThemeItem = new JRadioButtonMenuItem("Dark Theme");
		changeThemeItem.addActionListener(actionHandler);
		viewMenu.add(changeThemeItem);
		menuBar.add(viewMenu);

		window.setJMenuBar(menuBar);
	}

	public JFrame getWindow(){
		return window;
	}

	public JMenuBar getMenuBar() {
		return menuBar;
	}

	public JMenu getViewMenu() {
		return viewMenu;
	}

	public UI getCurrentPanel() {
		return currentPanel;
	}

	public void setFlatLAF(FlatLaf flatLaf) {
		try {
			UIManager.setLookAndFeel(flatLaf);
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		updateUI();
	}

	public void setCurrentPanel(@NotNull UI currentPanel) {
		if (this.currentPanel != null)
			this.currentPanel.teardown();

		currentPanel.setup();
		this.currentPanel = currentPanel;
		window.setContentPane(currentPanel);

		window.pack();
		window.setLocationRelativeTo(null);
	}

	public void updateUI() {
		SwingUtilities.updateComponentTreeUI(window);
	}

	@Override
	public void handle(Event event) {
		currentPanel.handle(event);
	}

	private class ActionHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(changeThemeItem)) {
				if (changeThemeItem.isSelected())
					setFlatLAF(new FlatDarculaLaf());
				else
					setFlatLAF(new FlatLightLaf());
			}
		}
	}

}
