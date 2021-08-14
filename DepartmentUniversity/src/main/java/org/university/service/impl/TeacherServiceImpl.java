package org.university.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.university.dao.TeacherDao;
import org.university.dto.TeacherDto;
import org.university.dto.UserDto;
import org.university.entity.Teacher;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EntityNotExistException;
import org.university.service.TeacherService;
import org.university.service.validator.Validator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Transactional
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
                .withId(user.getId())
                .withSex(user.getSex())
                .withName(user.getName())
                .withEmail(user.getEmail())
                .withPhone(user.getPhone())
                .withPassword(encoder.encode(user.getPassword()))
                .withScientificDegree(user.getScientificDegree())
                .withPhoto(user.getPhoto())
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
    
    @Override
    public List<Teacher> findAll() {        
        return teacherDao.findAll();
    }
    
    protected Teacher mapDtoToEntity(UserDto user) {
        TeacherDto teacher = (TeacherDto) user;
        return Teacher.builder()
                .withId(teacher.getId())
                .withSex(teacher.getSex())
                .withName(teacher.getName())
                .withEmail(teacher.getEmail())
                .withPhone(teacher.getPhone())
                .withPassword(teacher.getPassword())
                .withScientificDegree(teacher.getScientificDegree())
                .withPhoto(teacher.getPhotoName())
                .build();
    }
}
