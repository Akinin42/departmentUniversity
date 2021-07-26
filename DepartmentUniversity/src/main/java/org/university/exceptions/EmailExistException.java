package org.university.exceptions;

public class EmailExistException extends RuntimeException {
    
    public EmailExistException(String messages) {
        super(messages);
    }
}
