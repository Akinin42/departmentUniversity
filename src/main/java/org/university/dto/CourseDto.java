package org.university.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class CourseDto {    
    private Integer id;
    
    @NotBlank
    @Pattern(regexp = "[A-Za-z ]{2,50}")
    private String name;
    
    @NotBlank
    @Size(min = 5)
    @Pattern(regexp = "[a-zA-Z0-9\\_\\- ]+")
    private String description;
}
