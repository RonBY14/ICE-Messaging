package com.rchat.net.exceptions;

import com.rchat.net.utils.AuthenticationResult;
import org.jetbrains.annotations.NotNull;

public class AuthenticationException extends RuntimeException {

    private final AuthenticationResult authenticationResult;

    public AuthenticationException(@NotNull AuthenticationResult authenticationResult) {
        super(authenticationResult.name());

        this.authenticationResult = authenticationResult;
    }

    public AuthenticationResult getAuthenticationResult() {
        return authenticationResult;
    }
}
