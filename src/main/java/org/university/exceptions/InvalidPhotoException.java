package org.university.exceptions;

public class InvalidPhotoException extends RuntimeException {

    public InvalidPhotoException(String message) {
        super(message);        
    }
}
