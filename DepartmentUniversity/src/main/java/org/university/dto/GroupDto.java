package org.university.dto;

import java.util.Set;

import org.university.entity.Student;

import lombok.Data;

@Data
public class GroupDto {    
    Integer id;
    String name;
    Set<Student> students;    
}
