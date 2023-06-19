package org.university.api.v1;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.university.dto.UserDto;
import org.university.entity.Teacher;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EmailExistException;
import org.university.exceptions.InvalidPhotoException;
import org.university.service.PhotoService;
import org.university.service.TeacherService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/v1/teachers")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class TeacherControllerRest {

    TeacherService teacherService;
    PhotoService photoService;

    @GetMapping(params = { "page", "size" })
    public List<Teacher> getTeachers(@RequestParam("page") int page, @RequestParam("size") int size) {
        int lastPage = getLastPage(size);
        if (page < 0) {
            page = 0;
        }
        if (teacherService.findNumberOfUsers(size, page).isEmpty()) {
            page = lastPage;
        }
        return teacherService.findNumberOfUsers(size, page);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") int id) {
        UserDto teacher = new UserDto();
        teacher.setId(id);
        teacherService.delete(teacher);
    }

    @GetMapping("/{email}")
    public Teacher findTeacherByEmail(@PathVariable("email") String email) {
        return teacherService.getByEmail(email);
    }

    @PostMapping(consumes = { "multipart/form-data" })
    public void edit(@Valid @RequestPart("teacher") UserDto teacher, @RequestPart("photo") MultipartFile photo) {
        try {
            teacher.setPhoto(photo);
            String photoName = photoService.savePhoto(teacher);
            teacher.setPhotoName(photoName);
            teacherService.edit(teacher);
        } catch (EmailExistException | InvalidPhotoException | AuthorisationFailException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private int getLastPage(int size) {
        if ((teacherService.findAll().size() % size) != 0) {
            return teacherService.findAll().size() / size;
        } else {
            return (teacherService.findAll().size() / size) - 1;
        }
    }
}
