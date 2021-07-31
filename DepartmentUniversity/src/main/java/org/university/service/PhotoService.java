package org.university.service;

import org.university.dto.UserDto;

public interface PhotoService {
    
    String savePhoto(UserDto user);
    
    String createPhoto(String fileName);
}
