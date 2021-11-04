package org.university.service;

import java.util.List;

import org.university.dto.UserDto;

public interface UserService <E>{
    
    void register(UserDto user);
    
    void delete(UserDto user);    
    
    List<E> findNumberOfUsers(int quantity, int number);
    
    void edit(UserDto user);
    
    E getByEmail(String email);
}
