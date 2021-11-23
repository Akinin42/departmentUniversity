package org.university.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class ClassroomDto {

    private Integer id;

    @Min(1)
    private Integer number;

    @Pattern(regexp = "[a-zA-Z0-9\\_\\- ]+")
    @NotBlank(message = "address can't be empty")
    @Size(min = 5, max = 100)
    private String address;

    @Min(1)
    private Integer capacity;
}
