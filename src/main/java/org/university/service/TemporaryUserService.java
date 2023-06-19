package org.university.service;

import java.util.List;

import org.university.dto.UserDto;
import org.university.entity.TemporaryUser;

public interface TemporaryUserService extends UserService<TemporaryUser>{
    
    List<TemporaryUser> findAllConfirmUser();
    
    UserDto mapEntityToDto(TemporaryUser user);
    
    void addConfirmDescription(UserDto user);

}
