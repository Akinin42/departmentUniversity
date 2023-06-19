package org.university.exceptions;

public class InvalidLessonTimeException extends RuntimeException {

    public InvalidLessonTimeException(String message) {
        super(message);        
    }    
}
