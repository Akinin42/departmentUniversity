package org.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.university.dto.DayTimetableDto;
import org.university.service.DayTimetableService;
import org.university.service.GroupService;
import org.university.service.TeacherService;

@Controller
@RequestMapping("/timetables")
public class TimetableController {

    @Autowired
    private DayTimetableService timetableService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private TeacherService teacherService;

    @GetMapping()
    public String getTimetable(Model model) {
        model.addAttribute("timetable", new DayTimetableDto());
        model.addAttribute("groups", groupService.findAllGroups());
        model.addAttribute("teachers", teacherService.findAll());
        return "timetables";
    }

    @PostMapping("/getForGroup")
    public String createGroupTimetable(@ModelAttribute("timetable") DayTimetableDto timetable, Model model) {
        System.out.println();
        model.addAttribute("lessons",
                timetableService.createGroupTimetable(timetable.getDay(), timetable.getGroupName()).getLessons());
        return "lessons";
    }

}
