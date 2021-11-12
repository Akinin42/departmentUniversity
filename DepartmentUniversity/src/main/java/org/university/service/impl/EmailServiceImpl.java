package org.university.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import org.university.email.AbstractEmailContext;
import org.university.email.AccountVerificationEmailContext;
import org.university.entity.SecureToken;
import org.university.service.EmailService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmailServiceImpl<T> implements EmailService<T> {

    private JavaMailSender emailSender;   
    private ITemplateEngine templateEngine;    

    @Override
    public void sendMail(AbstractEmailContext<T> email) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(email.getContext());
        String emailContent = templateEngine.process(email.getTemplateLocation(), context);
        mimeMessageHelper.setTo(email.getTo());
        mimeMessageHelper.setSubject(email.getSubject());
        mimeMessageHelper.setFrom(email.getFrom());
        mimeMessageHelper.setText(emailContent, true);
        emailSender.send(message);
    }

    @Override
    public AbstractEmailContext<T> createEmailContext(T context, Locale locale, SecureToken token) {
        AccountVerificationEmailContext<T> emailContext = new AccountVerificationEmailContext<>();
        emailContext.setEmailLanguage(locale);
        emailContext.init(context);
        emailContext.setToken(token.getToken());
        emailContext.buildVerificationUrl("http://localhost:8080", token.getToken());
        return emailContext;
    }
}
