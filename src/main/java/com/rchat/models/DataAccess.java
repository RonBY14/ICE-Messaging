package com.rchat.models;

public class DataAccess {

    private User user;

    private final SessionData sessionData;

    public DataAccess() {
        sessionData = new SessionData();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SessionData getSessionData() {
        return sessionData;
    }
}
