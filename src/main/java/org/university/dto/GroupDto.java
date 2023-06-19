package org.university.dto;

import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.university.entity.Student;

import lombok.Data;

@Data
public class GroupDto {    
    private Integer id;
    
    @NotBlank
    @Pattern(regexp = "[A-Z]{2}-\\d{2}")
    private String name;
    
    private Set<Student> students;    
}
