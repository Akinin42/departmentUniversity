package org.university.api.v1;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.university.dto.GroupDto;
import org.university.dto.StudentDto;
import org.university.entity.Group;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.service.GroupService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/v1/groups")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class GroupControllerRest {

    GroupService groupService;

    @GetMapping()
    public List<Group> getAll() {
        return groupService.findAllGroups();
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@Valid @RequestBody GroupDto group) {
        try {
            groupService.addGroup(group);
        } catch (EntityAlreadyExistException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/student")    
    public void addStudent(@RequestBody StudentDto student) {
        groupService.addStudentToGroup(student);
    }

    @DeleteMapping("/student")    
    public void deleteStudent(@RequestBody StudentDto student) {
        groupService.deleteStudentFromGroup(student);
    }

    @DeleteMapping()    
    public void delete(@RequestBody GroupDto group) {
        groupService.delete(group);
    }

    @PatchMapping()    
    public void edit(@Valid @RequestBody GroupDto group) {
        try {
            groupService.edit(group);
        } catch (EntityAlreadyExistException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
