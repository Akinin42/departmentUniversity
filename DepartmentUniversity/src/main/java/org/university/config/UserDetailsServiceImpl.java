package org.university.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.university.dao.StudentDao;
import org.university.dao.TeacherDao;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class UserDetailsServiceImpl implements UserDetailsService {    

    private StudentDao studentDao;
    private TeacherDao teacherDao;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
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
