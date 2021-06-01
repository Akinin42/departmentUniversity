package org.university.service.validator;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import org.university.entity.User;
import org.university.exceptions.InvalidEmailException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserValidator<E> implements Validator<E> {

    private static final Pattern EMAIL_PATTERN = Pattern
            .compile("^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$");

    @Override
    public void validate(E user) throws InvalidEmailException {
        String email = ((User) user).getEmail();
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            log.error("Input user has invalid email: " + email);
            throw new InvalidEmailException();
        }
    }
}
