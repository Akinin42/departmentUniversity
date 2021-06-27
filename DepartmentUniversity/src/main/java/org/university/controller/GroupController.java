package org.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.university.dto.DayTimetableDto;
import org.university.dto.GroupDto;
import org.university.dto.StudentDto;
import org.university.entity.Group;
import org.university.service.GroupService;
import org.university.service.StudentService;

@Controller
@RequestMapping("/groups")
public class GroupController {

    private static final String REDIRECT = "redirect:/groups";
    
    @Autowired
    private GroupService groupService;

    @Autowired
    private StudentService studentService;

    @GetMapping()
    public String getAll(Model model) {
        model.addAttribute("group", new GroupDto());
        model.addAttribute("student", new StudentDto());
        model.addAttribute("groups", groupService.findAllGroups());
        model.addAttribute("students", studentService.findAll());
        model.addAttribute("timetable", new DayTimetableDto());
        return "groups";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("group") GroupDto group) {
        groupService.addGroup(group);
        return REDIRECT;
    }

    @PostMapping("/addStudent")
    public String addStudent(@ModelAttribute("student") StudentDto student) {
        Group group = groupService.createGroup(student.getGroupName());
        studentService.addStudentToGroup(student, group);
        return REDIRECT;
    }

    @PostMapping("/deleteStudent")
    public String deleteStudent(@ModelAttribute("student") StudentDto student) {
        Group group = groupService.createGroup(student.getGroupName());
        studentService.deleteStudentFromGroup(student, group);
        return REDIRECT;
    }

    @PostMapping("/delete")
    public String delete(@ModelAttribute("group") GroupDto group) {
        groupService.delete(group);
        return REDIRECT;
    }    
}
