package org.university.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.university.dao.StudentDao;
import org.university.dao.TeacherDao;
import org.university.dao.TemporaryUserDao;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    private StudentDao studentDao;
    private TeacherDao teacherDao;
    private TemporaryUserDao temporaryUserDao;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (temporaryUserDao.findByEmail(email).isPresent()) {
            return new MyUserDetails(temporaryUserDao.findByEmail(email).get());
        }
        if (studentDao.findByEmail(email).isPresent()) {
            return new MyUserDetails(studentDao.findByEmail(email).get());
        }
        if (teacherDao.findByEmail(email).isPresent()) {
            return new MyUserDetails(teacherDao.findByEmail(email).get());
        } else {
            throw new UsernameNotFoundException("Could not find user");
        }
    }
}
