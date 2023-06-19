package org.university.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.university.dao.RoleDao;
import org.university.dao.TeacherDao;
import org.university.dto.UserDto;
import org.university.entity.Teacher;
import org.university.entity.User;
import org.university.exceptions.EntityNotExistException;
import org.university.service.EmailService;
import org.university.service.SecureTokenService;
import org.university.service.TeacherService;
import org.university.service.validator.Validator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional
public class TeacherServiceImpl extends AbstractUserServiceImpl<Teacher> implements TeacherService {
    
    private static final String TEACHER = "TEACHER";
    
    TeacherDao teacherDao;
    RoleDao roleDao;
    PasswordEncoder encoder;    

    public TeacherServiceImpl(TeacherDao teacherDao, EmailService<User> emailService,
            SecureTokenService secureTokenService,
            Validator<User> validator, PasswordEncoder encoder, RoleDao roleDao) {
        super(teacherDao, validator, emailService, secureTokenService);
        this.encoder = encoder;
        this.teacherDao = teacherDao;
        this.roleDao = roleDao;
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
                .withRole(roleDao.findByName(TEACHER).get())
                .withEnabled(true)
                .build();
    }   
    
    @Override
    public List<Teacher> findAll() {        
        return teacherDao.findAll();
    }
    
    protected Teacher mapDtoToEntity(UserDto user) {        
        String scientificDegree;
        if(user.getScientificDegree()!=null) {
            scientificDegree = user.getScientificDegree();
        } else {
            scientificDegree = user.getDesiredDegree();
        }
        return Teacher.builder()
                .withId(user.getId())
                .withSex(user.getSex())
                .withName(user.getName())
                .withEmail(user.getEmail())
                .withPhone(user.getPhone())
                .withPassword(user.getPassword())
                .withScientificDegree(scientificDegree)
                .withPhoto(user.getPhotoName())                
                .withEnabled(true)
                .build();
    }

    @Override
    public Teacher getByEmail(String email) {
        return teacherDao.findByEmail(email).orElseThrow(() -> new EntityNotExistException("Teacher with " + email+ " not found"));
    }
}
