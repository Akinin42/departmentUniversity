package org.university.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class StudentDto extends UserDto {
    String courseName;
    String groupName;
}
