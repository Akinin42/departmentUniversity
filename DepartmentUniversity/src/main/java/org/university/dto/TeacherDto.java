package org.university.dto;

import lombok.Data;

@Data
public class TeacherDto {    
    Integer id;
    String sex;
    String name;
    String email;
    String phone;
    String password;
    String confirmPassword;
    String scientificDegree;
}
