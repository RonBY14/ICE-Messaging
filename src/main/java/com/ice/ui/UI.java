package com.ice.ui;

import com.eventsystem.components.EventBus;
import com.eventsystem.subscriber.Subscriber;
import com.ice.models.DataAccess;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class UI extends JPanel implements Subscriber {

    protected EventBus eventBus;
    protected DataAccess dataAccess;

    protected GUIManager guiManager;

    public UI(@NotNull EventBus eventBus, @NotNull DataAccess dataAccess, @NotNull GUIManager guiManager) {
        this.eventBus = eventBus;
        this.dataAccess = dataAccess;

        this.guiManager = guiManager;
    }

    public abstract void setup();

    public abstract void teardown();

}
