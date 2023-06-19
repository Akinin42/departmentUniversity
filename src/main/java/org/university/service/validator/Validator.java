package org.university.service.validator;

public interface Validator<E> {
    void validate(E entity);
}
