package org.university.service.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.university.service.validator.constraints.FieldMatch;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(final FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.field();
        secondFieldName = constraintAnnotation.verifyField();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {

        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);
        final Object firstObj = beanWrapper.getPropertyValue(firstFieldName);
        final Object secondObj = beanWrapper.getPropertyValue(secondFieldName);
        boolean isValid = firstObj != null && firstObj.equals(secondObj);
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(firstFieldName).addConstraintViolation();
        }
        return isValid;
    }
}
