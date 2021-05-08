package org.university.exceptions;

public class NoFileException extends RuntimeException {

    public NoFileException(String messages, Throwable cause) {
        super(messages, cause);
    }
}
