package org.university.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.university.dto.DayTimetableDto;
import org.university.dto.GroupDto;
import org.university.dto.StudentDto;
import org.university.entity.Group;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.InvalidGroupNameException;
import org.university.service.GroupService;
import org.university.service.StudentService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RequestMapping("/groups")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class GroupController {

    private static final String REDIRECT = "redirect:/groups";
    GroupService groupService;
    StudentService studentService;

    @GetMapping()
    public String getAll(@ModelAttribute("message") String message, Model model) {
        model.addAttribute("group", new GroupDto());
        model.addAttribute("student", new StudentDto());
        model.addAttribute("groups", groupService.findAllGroups());
        model.addAttribute("students", studentService.findAll());
        model.addAttribute("timetable", new DayTimetableDto());
        return "groups";
    }

    @PostMapping()
    public String add(@ModelAttribute("group") GroupDto group, Model model) {
        try {
            groupService.addGroup(group);
            return REDIRECT;
        } catch (InvalidGroupNameException | EntityAlreadyExistException e) {
            model.addAttribute("message", e.getMessage());
            return REDIRECT;
        }
    }

    @PostMapping("/student")
    public String addStudent(@ModelAttribute("student") StudentDto student) {
        Group group = groupService.createGroup(student.getGroupName());
        studentService.addStudentToGroup(student, group);
        return REDIRECT;
    }

    @DeleteMapping("/student")
    public String deleteStudent(@ModelAttribute("student") StudentDto student) {
        Group group = groupService.createGroup(student.getGroupName());
        studentService.deleteStudentFromGroup(student, group);
        return REDIRECT;
    }

    @DeleteMapping()
    public String delete(@ModelAttribute("group") GroupDto group) {
        groupService.delete(group);
        return REDIRECT;
    }

    @PostMapping("/edit")
    public String getEditForm(@ModelAttribute("group") GroupDto group, @ModelAttribute("message") String message,
            Model model) {
        model.addAttribute("group", group);
        return "updateforms/group";
    }

    @PatchMapping()
    public String edit(@ModelAttribute("group") GroupDto group, Model model) {
        try {
            groupService.edit(group);
            return REDIRECT;
        } catch (InvalidGroupNameException | EntityAlreadyExistException e) {
            model.addAttribute("message", e.getMessage());
            return "updateforms/group";
        }
    }
}
