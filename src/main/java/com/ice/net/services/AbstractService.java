package com.ice.net.services;

import com.eventsystem.components.EventBus;
import com.eventsystem.subscriber.Subscriber;
import com.ice.models.DataAccess;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractService implements Subscriber {

    protected final EventBus eventBus;
    protected final DataAccess dataAccess;

    public AbstractService(@NotNull EventBus eventBus, @NotNull DataAccess dataAccess) {
        this.eventBus = eventBus;
        this.dataAccess = dataAccess;
    }

}
