package org.university.exceptions;

public class InvalidUserNameException extends RuntimeException {
    
    public InvalidUserNameException(String messages) {
        super(messages);
    }
    
    public InvalidUserNameException() {
    }
}
