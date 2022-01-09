package com.ice.net.services;

import com.eventsystem.components.EventBus;
import com.eventsystem.events.Event;
import com.eventsystem.subscriber.Subscriber;
import com.eventsystem.utils.RunnableService;
import com.ice.events.services.NetServiceStartEvent;
import com.ice.events.services.ServiceFailedEvent;
import com.ice.events.services.ServiceStartEvent;
import com.ice.events.services.ServiceTerminateEvent;
import com.ice.models.DataAccess;
import com.ice.net.exceptions.ServiceUnexpectedlyTerminatedException;
import com.ice.net.exceptions.UnableToStartServiceException;
import com.ice.net.utils.ServiceDebugLogger;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractRunnableService extends RunnableService implements Subscriber {

    protected final EventBus eventBus;

    protected final DataAccess dataAccess;

    public AbstractRunnableService(@NotNull EventBus eventBus, @NotNull DataAccess dataAccess) {
        this.eventBus = eventBus;
        this.dataAccess = dataAccess;
    }

    protected void setup(NetServiceStartEvent event) throws Exception {}

    protected void teardown() {}

    protected void initiateUnexpectedTermination(Throwable throwable) {
        ServiceDebugLogger.log(this.getClass(), "Problem occurred causing termination of the service!");
        terminated = true;

        eventBus.publish(
                new ServiceFailedEvent(new ServiceUnexpectedlyTerminatedException(throwable)),
                ConnectionController.CC_TOPIC);
        teardown();
    }

    @Override
    public void handle(Event event) {
        if (event instanceof ServiceStartEvent) {
            if (thread != null && thread.isAlive()) return;

            try {
                setup((NetServiceStartEvent) event);

                if (!start())
                    teardown();
            } catch (Exception e) {
                e.printStackTrace();
                eventBus.publish(new ServiceFailedEvent(new UnableToStartServiceException(e)), ConnectionController.CC_TOPIC);
            }
        } else if (event instanceof ServiceTerminateEvent) {
            if (terminate(true)) teardown();
        }
    }

}
