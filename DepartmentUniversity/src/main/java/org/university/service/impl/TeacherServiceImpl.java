package org.university.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.university.dao.TeacherDao;
import org.university.entity.Teacher;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EntityNotExistException;
import org.university.service.TeacherService;
import org.university.service.validator.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class TeacherServiceImpl extends AbstractUserServiceImpl<Teacher> implements TeacherService {

    TeacherDao teacherDao;
    PasswordEncoder encoder;

    public TeacherServiceImpl(TeacherDao teacherDao, Validator<Teacher> validator, PasswordEncoder encoder) {
        super(teacherDao, validator);
        this.teacherDao = teacherDao;
        this.encoder = encoder;
    }

    @Override
    protected Teacher mapUserWithPassword(Teacher user) {
        return Teacher.builder()
                .withSex(user.getSex())
                .withName(user.getName())
                .withEmail(user.getEmail())
                .withPhone(user.getPhone())
                .withPassword(encoder.encode(user.getPassword()))
                .withScientificDegree(user.getScientificDegree())
                .build();
    }

    @Override
    public Teacher login(String email, String password) {
        if (!teacherDao.findByEmail(email).isPresent()) {
            throw new EntityNotExistException();
        }
        Teacher teacher = teacherDao.findByEmail(email).get();
        if (!encoder.matches(password, teacher.getPassword())) {
            throw new AuthorisationFailException();
        }
        log.info("Authorisation for teacher with id {} succesfull!", teacher.getId());
        return teacher;
    }
}
