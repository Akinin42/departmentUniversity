package org.university.api.v1;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.university.dto.ClassroomDto;
import org.university.entity.Classroom;
import org.university.service.ClassroomService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/v1/classrooms")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ClassroomControllerRest {

    ClassroomService classroomService;

    @GetMapping()
    public List<Classroom> getAll() {
        return classroomService.findAllClassrooms();
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@Valid @RequestBody ClassroomDto classroom) {
        classroomService.addClassroom(classroom);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") int id) {
        ClassroomDto classroom = new ClassroomDto();
        classroom.setId(id);
        classroomService.delete(classroom);
    }

    @PatchMapping()
    public void edit(@Valid @RequestBody ClassroomDto classroom) {
        classroomService.edit(classroom);
    }
}
