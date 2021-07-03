package org.university.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.university.dto.DayTimetableDto;
import org.university.dto.LessonDto;
import org.university.service.ClassroomService;
import org.university.service.CourseService;
import org.university.service.DayTimetableService;
import org.university.service.GroupService;
import org.university.service.LessonService;
import org.university.service.TeacherService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RequestMapping("/timetables")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class TimetableController {
    
    DayTimetableService timetableService;
    GroupService groupService;
    TeacherService teacherService;
    CourseService courseService;
    ClassroomService classroomService;
    LessonService lessonService;

    @GetMapping()
    public String getTimetable(Model model) {
        model.addAttribute("lessons", timetableService.createDayTimetable(LocalDate.now().toString()).getLessons());
        return "lessons";
    }

    @PostMapping("/date")
    public String getTimetableOnDay(@ModelAttribute("timetable") DayTimetableDto timetable, Model model) {
        model.addAttribute("lessons", timetableService.createDayTimetable(timetable.getDay()).getLessons());
        return "lessons";
    }

    @PostMapping("/group")
    public String createGroupTimetable(@ModelAttribute("timetable") DayTimetableDto timetable, Model model) {
        model.addAttribute("lessons",
                timetableService.createGroupTimetable(timetable.getDay(), timetable.getGroupName()).getLessons());
        return "lessons";
    }

    @PostMapping("/teacher")
    public String createTeacherTimetable(@ModelAttribute("timetable") DayTimetableDto timetable, Model model) {
        model.addAttribute("lessons",
                timetableService.createTeacherTimetable(timetable.getDay(), timetable.getTeacherEmail()).getLessons());
        return "lessons";
    }

    @GetMapping("/new")
    public String newLesson(Model model) {
        model.addAttribute("groups", groupService.findAllGroups());
        model.addAttribute("teachers", teacherService.findAll());
        model.addAttribute("lesson", new LessonDto());
        model.addAttribute("courses", courseService.findAllCourses());
        model.addAttribute("classrooms", classroomService.findAllClassrooms());
        return "lessonform";
    }

    @PostMapping()
    public String addLesson(@ModelAttribute("lesson") LessonDto lesson, Model model) {
        lessonService.addLesson(lesson);
        String date = LocalDateTime.parse(lesson.getStartLesson()).toLocalDate().toString();
        model.addAttribute("lessons", timetableService.createDayTimetable(date).getLessons());
        return "lessons";
    }
    
    @DeleteMapping()
    public String deleteLesson(@ModelAttribute("lesson") LessonDto lesson, Model model) {
        lessonService.delete(lesson);
        String date = LocalDateTime.parse(lesson.getStartLesson()).toLocalDate().toString();
        model.addAttribute("lessons", timetableService.createDayTimetable(date).getLessons());
        return "lessons";
    }
}
