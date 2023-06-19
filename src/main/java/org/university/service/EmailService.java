package org.university.service;

import java.util.Locale;

import javax.mail.MessagingException;

import org.university.email.AbstractEmailContext;
import org.university.entity.SecureToken;

public interface EmailService<T> {

    void sendMail(AbstractEmailContext<T> email) throws MessagingException;
    
    AbstractEmailContext<T> createEmailContext(T context, Locale locale, SecureToken token);
}
