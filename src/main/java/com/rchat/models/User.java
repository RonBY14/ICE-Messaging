package com.rchat.models;

import com.rchat.net.exceptions.AuthenticationException;
import com.rchat.net.utils.AuthenticationResult;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

public class User {

    private String nickname;

    private BufferedImage background;

    public User(@NotNull String nickname) throws AuthenticationException {
        setNickname(nickname);

        background = null;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) throws AuthenticationException {
        String invalidCharacters = ":;@?/\\`!(){}^%$#+-*.,'\"[]";

        if (nickname.isBlank() || nickname.isEmpty()) {
            throw new AuthenticationException(AuthenticationResult.BLANK_OR_EMPTY);
        } else if (nickname.length() < 2) {
            throw new AuthenticationException(AuthenticationResult.TOO_SHORT);
        } else if (nickname.length() > 12) {
            throw new AuthenticationException(AuthenticationResult.TOO_LONG);
        } else if (StringUtils.containsAny(nickname, invalidCharacters)) {
            throw new AuthenticationException(AuthenticationResult.INVALID);
        } else if (nickname.equalsIgnoreCase("Me")) {
            throw new AuthenticationException(AuthenticationResult.UNAVAILABLE);
        }
        this.nickname = nickname;
    }

    public BufferedImage getBackground() {
        return background;
    }

    public void setBackground(BufferedImage background) {
        this.background = background;
    }
}
