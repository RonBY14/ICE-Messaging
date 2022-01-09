package com.ice.events.net;

import com.eventsystem.events.Event;
import com.ice.net.utils.AuthenticationResult;

public class AuthenticationEvent extends Event {

    private final AuthenticationResult authenticationResult;

    public AuthenticationEvent(AuthenticationResult authenticationResult) {
        this.authenticationResult = authenticationResult;
    }

    public AuthenticationResult getAuthenticationResult() {
        return authenticationResult;
    }
}
