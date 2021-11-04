package org.university.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;
import org.university.service.validator.constraints.FieldMatch;
import org.university.service.validator.constraints.Phone;
import org.university.utils.Sex;

import lombok.Data;

@Data
@FieldMatch(field = "password", verifyField = "confirmPassword", message = "passwordmatch")
public class UserDto {
    private Integer id;
    private Sex sex;

    @NotBlank
    @Size(min = 1)
    @Pattern(regexp = "[a-zA-Z0-9\\_\\- ]+")
    private String name;

    @Email
    private String email;

    @NotBlank
    @Phone
    private String phone;

    @NotBlank
    private String password;

    @NotBlank
    private String confirmPassword;

    private MultipartFile photo;
    private String photoName;
    private String desiredRole;
    private String scientificDegree;
    private String desiredDegree;
    Boolean confirm;    
    String confirmDescription;
}
