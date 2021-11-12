package org.university.service;

import java.util.List;
import java.util.Locale;

import org.university.dto.UserDto;
import org.university.exceptions.InvalidTokenException;

public interface UserService <E>{
    
    void register(UserDto user);
    
    void delete(UserDto user);    
    
    List<E> findNumberOfUsers(int quantity, int number);
    
    void edit(UserDto user);
    
    E getByEmail(String email);
    
    void sendRegistrationConfirmationEmail(E user, Locale locale);

    boolean verifyUser(String token) throws InvalidTokenException;
}
