package org.university.exceptions;

public class InvalidEmailException extends RuntimeException {
    
    public InvalidEmailException(String messages) {
        super(messages);
    }
}
