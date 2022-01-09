package com.rchat.ui.exceptions;

public class TooLongException extends RuntimeException {

    public TooLongException(String text) {
        super(text + " is too long for a single lined bubbled message!");
    }
}
