package org.university.dto;

import lombok.Data;

@Data
public class StudentDto {
    Integer id;
    String sex;
    String name;
    String email;
    String phone;
    String password;    
    String courseName;
    Integer groupId;
}
