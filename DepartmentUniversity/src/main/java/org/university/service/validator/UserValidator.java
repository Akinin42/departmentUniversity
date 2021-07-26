package org.university.service.validator;

import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import org.university.dao.StudentDao;
import org.university.dao.TeacherDao;
import org.university.entity.Student;
import org.university.entity.Teacher;
import org.university.entity.User;
import org.university.exceptions.EmailExistException;
import org.university.exceptions.InvalidEmailException;
import org.university.exceptions.InvalidPhoneException;
import org.university.exceptions.InvalidUserNameException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AllArgsConstructor
public class UserValidator<E> implements Validator<E> {

    private StudentDao studentDao;
    private TeacherDao teacherDao;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s]{2,50}");
    private static final Pattern EMAIL_PATTERN = Pattern
            .compile("^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9.()-]{10,25}$");

    @Override
    public void validate(E user) throws InvalidEmailException {
        String name = ((User) user).getName();
        if (!NAME_PATTERN.matcher(name).matches()) {
            log.error("Input user has invalid name: " + name);
            throw new InvalidUserNameException("invalidusername");
        }
        String email = ((User) user).getEmail();
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            log.error("Input user has invalid email: " + email);
            throw new InvalidEmailException("invalidemail");
        }
        String phone = ((User) user).getPhone();
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            log.error("Input user has invalid phone: " + phone);
            throw new InvalidPhoneException("invalidphone");
        }
        if (user.getClass() == Student.class && !studentDao.findByEmail(email).equals(Optional.empty())) {
            if (studentDao.findById(((User) user).getId()).get().getEmail().equals(email)) {
            } else {
                throw new EmailExistException("studentemailexist");
            }
        }
        if (user.getClass() == Teacher.class && !teacherDao.findByEmail(email).equals(Optional.empty())) {
            if (teacherDao.findById(((User) user).getId()).get().getEmail().equals(email)) {
            } else {
                throw new EmailExistException("teacheremailexist");
            }
        }
    }
}
