package org.university.dto;

import lombok.Data;

@Data
public class UserDto {
    Integer id;
    String sex;
    String name;
    String email;
    String phone;
    String password;
    String confirmPassword;
}
