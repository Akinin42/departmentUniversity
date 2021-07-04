package org.university.exceptions;

public class InvalidPhoneException extends RuntimeException {
    
    public InvalidPhoneException(String messages) {
        super(messages);
    }
}
