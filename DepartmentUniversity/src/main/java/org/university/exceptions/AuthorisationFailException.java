package org.university.exceptions;

public class AuthorisationFailException extends RuntimeException {

    public AuthorisationFailException(String messages) {
        super(messages);
    }

    public AuthorisationFailException() {        
    }
}
