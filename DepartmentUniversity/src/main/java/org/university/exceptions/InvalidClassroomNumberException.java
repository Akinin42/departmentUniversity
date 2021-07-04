package org.university.exceptions;

public class InvalidClassroomNumberException extends RuntimeException {
    
    public InvalidClassroomNumberException(String message) {
        super(message);        
    }
}
