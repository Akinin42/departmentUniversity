package org.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.university.dto.GroupDto;
import org.university.service.DayTimetableService;
import org.university.service.LessonService;

@Controller
@RequestMapping("/lessons")
public class LessonsController {
    
    @Autowired
    private LessonService lessonService;
    
    @Autowired
    private DayTimetableService timetableService;
    
//    @GetMapping
//    public String showTimetable(Model model) {
//        
//    }
    
//    @PostMapping("/groupTimetable")
//    public String createGroupTimetable(@ModelAttribute("group")GroupDto group) {
//        model.addAttribute("lessons", timetableService.createGroupTimetable(date, group.getName()));
//        return "redirect:/lessons";
//    }

}
