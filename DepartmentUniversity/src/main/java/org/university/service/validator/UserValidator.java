package org.university.service.validator;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.university.dao.StudentDao;
import org.university.dao.TeacherDao;
import org.university.dao.TemporaryUserDao;
import org.university.entity.Student;
import org.university.entity.Teacher;
import org.university.entity.TemporaryUser;
import org.university.entity.User;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EmailExistException;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UserValidator implements Validator<User> {

    private StudentDao studentDao;
    private TeacherDao teacherDao;
    private TemporaryUserDao temporaryUserDao;
    private PasswordEncoder encoder;

    @Override
    public void validate(User user) {
        String email = user.getEmail();
        Integer userId = user.getId();
        if (studentPasswordNotCorrect(user, userId) || teacherPasswordNotCorrect(user, userId)
                || userPasswordNotCorrect(user, userId)) {
            throw new AuthorisationFailException("authorisationfail");
        }
        if (userId == null && checkEmailExistsToDatabase(email)) {
            throw new EmailExistException("useremailexist");
        }
        if (userId != null && checkEmailExistsToDatabase(email) && checkUserEditHisEmail(userId, email)) {
            throw new EmailExistException("useremailexist");
        }
    }

    private boolean checkEmailExistsToDatabase(String email) {
        return !studentDao.findByEmail(email).equals(Optional.empty())
                || !teacherDao.findByEmail(email).equals(Optional.empty())
                || !temporaryUserDao.findByEmail(email).equals(Optional.empty());
    }

    private boolean checkUserEditHisEmail(Integer userId, String email) {
        return !studentDao.findByEmail(email).equals(studentDao.findById(userId))
                || !teacherDao.findByEmail(email).equals(teacherDao.findById(userId))
                || !temporaryUserDao.findByEmail(email).equals(temporaryUserDao.findById(userId));
    }

    private boolean studentPasswordNotCorrect(User user, Integer userId) {
        return userId != null && user.getClass() == Student.class
                && !encoder.matches(user.getPassword(), studentDao.findById(userId).get().getPassword());
    }

    private boolean teacherPasswordNotCorrect(User user, Integer userId) {
        return userId != null && user.getClass() == Teacher.class
                && !encoder.matches(user.getPassword(), teacherDao.findById(userId).get().getPassword());
    }

    private boolean userPasswordNotCorrect(User user, Integer userId) {
        return userId != null && user.getClass() == TemporaryUser.class
                && !encoder.matches(user.getPassword(), temporaryUserDao.findById(userId).get().getPassword());
    }
}
