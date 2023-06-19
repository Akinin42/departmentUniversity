package org.university.email;

import java.util.ResourceBundle;

import org.springframework.web.util.UriComponentsBuilder;
import org.university.entity.TemporaryUser;

public class AccountVerificationEmailContext<T> extends AbstractEmailContext<T> {
    
    @Override
    public void init(T context) {
        TemporaryUser user = (TemporaryUser) context;
        put("name", user.getName());
        setTemplateLocation("emails/email-verification");
        setSubject(ResourceBundle.getBundle("/i18n/messages", getEmailLanguage()).getString("complete"));
        setFrom("noreply@gmail.com");
        setTo(user.getEmail());
    }

    public void setToken(String token) {
        put("token", token);
    }

    public void buildVerificationUrl(final String baseURL, final String token) {
        final String url = UriComponentsBuilder.fromHttpUrl(baseURL).path("DepartmentUniversity/register")
                .queryParam("token", token).toUriString();
        put("verificationURL", url);
    }
}
