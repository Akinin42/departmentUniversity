package org.university.service.validator;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.university.dao.StudentDao;
import org.university.dao.TeacherDao;
import org.university.entity.Student;
import org.university.entity.Teacher;
import org.university.entity.User;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EmailExistException;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UserValidator implements Validator<User> {

    private StudentDao studentDao;
    private TeacherDao teacherDao;
    PasswordEncoder encoder;

    @Override
    public void validate(User user) {
        String email = user.getEmail();
        Integer userId = user.getId();
        if (userId != null && user.getClass() == Student.class
                && !encoder.matches(user.getPassword(), studentDao.findById(userId).get().getPassword())) {
            throw new AuthorisationFailException("authorisationfail");
        }
        if (userId != null && user.getClass() == Teacher.class
                && !encoder.matches(user.getPassword(), teacherDao.findById(userId).get().getPassword())) {
            throw new AuthorisationFailException("authorisationfail");
        }
        if ((!teacherDao.findByEmail(email).equals(Optional.empty())
                || !studentDao.findByEmail(email).equals(Optional.empty())) && userId == null) {
            throw new EmailExistException("useremailexist");
        }
        if (userId != null
                && (!studentDao.findByEmail(email).equals(Optional.empty())
                        && !studentDao.findByEmail(email).equals(studentDao.findById(userId)))
                || (!teacherDao.findByEmail(email).equals(Optional.empty())
                        && !teacherDao.findByEmail(email).equals(teacherDao.findById(userId)))) {
            throw new EmailExistException("useremailexist");
        }
    }
}
