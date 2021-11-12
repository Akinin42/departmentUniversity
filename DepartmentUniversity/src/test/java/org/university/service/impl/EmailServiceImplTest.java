package org.university.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.university.email.AbstractEmailContext;
import org.university.email.AccountVerificationEmailContext;
import org.university.entity.SecureToken;
import org.university.entity.TemporaryUser;

class EmailServiceImplTest {
    
    private static ITemplateEngine templateEngineMock;
    private static JavaMailSender emailSenderMock;
    private static EmailServiceImpl<TemporaryUser> emailService;  

    @BeforeAll
    static void init() {
        emailSenderMock = mock(JavaMailSender.class);
        templateEngineMock = mock(ITemplateEngine.class);
        emailService = new EmailServiceImpl<TemporaryUser>(emailSenderMock, templateEngineMock);
    }

    @Test
    void createEmailContextShouldReturnExpectedEmailContext() {
        TemporaryUser user = TemporaryUser.builder()
                .withId(1)
                .build();
        Locale locale = new Locale("en");
        SecureToken token = new SecureToken();
        token.setToken("token");
        AccountVerificationEmailContext<TemporaryUser> expected = new AccountVerificationEmailContext<>();
        expected.setEmailLanguage(locale);
        expected.init(user);
        expected.setToken(token.getToken());
        expected.buildVerificationUrl("http://localhost:8080", token.getToken());
        assertThat(emailService.createEmailContext(user, locale, token)).isEqualTo(expected);
    }

    @Test
    @SuppressWarnings("unchecked")
    void sendMailShouldSendEmail() throws MessagingException {
        AbstractEmailContext<TemporaryUser> email = (AbstractEmailContext<TemporaryUser>) mock(
                AbstractEmailContext.class);
        MimeMessage message = mock(MimeMessage.class);
        when(emailSenderMock.createMimeMessage()).thenReturn(message);
        MimeMessageHelper messageHelper = new MimeMessageHelper(message,
        MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        messageHelper.setTo("user@email.com");
        messageHelper.setSubject("subject");
        messageHelper.setFrom("noreply@gmail.com");
        messageHelper.setText("email content", true);
        when(emailSenderMock.createMimeMessage()).thenReturn(message);
        Map<String, Object> inputContext = new HashMap<>();
        inputContext.put("name", "user name");
        inputContext.put("token", "user token");
        inputContext.put("verificationURL", "url for verify");
        Context context = new Context();
        context.setVariables(inputContext);
        when(email.getContext()).thenReturn(inputContext);
        when(email.getTo()).thenReturn("user@email.com");
        when(email.getSubject()).thenReturn("subject");
        when(email.getFrom()).thenReturn("noreply@gmail.com");
        when(email.getTemplateLocation()).thenReturn("template");              
        when(templateEngineMock.process(eq("template"), (IContext) any(IContext.class))).thenReturn("email content");        
        emailService.sendMail(email);
        verify(emailSenderMock).send(message);
    }
}
