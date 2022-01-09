package com.ice.net.exceptions;

public class MessageOversizeException extends RuntimeException {

    public MessageOversizeException(int size, int maximum) {
        super(size + " is oversize! Maximum=" + maximum);
    }
}
