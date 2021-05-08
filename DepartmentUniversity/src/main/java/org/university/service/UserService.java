package org.university.service;

import java.util.List;

public interface UserService <E>{
    
    void register(E user);
    
    void delete(E user);    
    
    List<E> findNumberOfUsers(int quantity, int number);

}
