package org.university.service.validator;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import org.university.entity.User;
import org.university.exceptions.InvalidEmailException;
import org.university.exceptions.InvalidPhoneException;
import org.university.exceptions.InvalidUserNameException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserValidator<E> implements Validator<E> {
    
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s]{2,50}");
    private static final Pattern EMAIL_PATTERN = Pattern
            .compile("^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9.()-]{10,25}$");

    @Override
    public void validate(E user) throws InvalidEmailException {
        String name = ((User) user).getName();
        if (!NAME_PATTERN.matcher(name).matches()) {
            log.error("Input user has invalid name: " + name);
            throw new InvalidUserNameException("Input name isn't valid!");
        }
        String email = ((User) user).getEmail();
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            log.error("Input user has invalid email: " + email);
            throw new InvalidEmailException("Input email isn't valid!");
        }
        String phone = ((User) user).getPhone();
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            log.error("Input user has invalid phone: " + phone);
            throw new InvalidPhoneException("Input phone isn't valid!");
        }
    }
}
