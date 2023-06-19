package org.university.service.validator;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.university.service.validator.constraints.Phone;

public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9.()-]{10,25}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return PHONE_PATTERN.matcher(value).matches();
    }
}
