package org.university.dto;

import java.util.Set;
import org.university.entity.Course;
import lombok.Data;

@Data
public class StudentDto {
    Integer id;
    String sex;
    String name;
    String email;
    String phone;
    String password;
    Set<Course> courses;
}
