package org.university.dto;

import java.util.List;
import org.university.entity.Student;
import lombok.Data;

@Data
public class GroupDto {    
    Integer id;
    String name;
    List<Student> students;    
}
