package org.university.exceptions;

public class InvalidAddressException extends RuntimeException {

    public InvalidAddressException(String message) {
        super(message);        
    }
}
