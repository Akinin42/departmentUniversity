package org.university.exceptions;

public class InvalidCourseNameException extends RuntimeException {

    public InvalidCourseNameException(String message) {
        super(message);        
    }
}
