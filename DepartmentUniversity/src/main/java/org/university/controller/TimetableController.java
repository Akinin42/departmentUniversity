package org.university.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

@Controller
@RequestMapping("/timetables")
public class TimetableController {

    @Autowired
    private DayTimetableService timetableService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private LessonService lessonService;

    @GetMapping()
    public String getTimetable(Model model) {
        model.addAttribute("lessons", timetableService.createDayTimetable(LocalDate.now().toString()).getLessons());
        return "lessons";
    }

    @PostMapping("/getOnDay")
    public String getTimetableOnDay(@ModelAttribute("timetable") DayTimetableDto timetable, Model model) {
        model.addAttribute("lessons", timetableService.createDayTimetable(timetable.getDay()).getLessons());
        return "lessons";
    }

    @PostMapping("/getForGroup")
    public String createGroupTimetable(@ModelAttribute("timetable") DayTimetableDto timetable, Model model) {
        model.addAttribute("lessons",
                timetableService.createGroupTimetable(timetable.getDay(), timetable.getGroupName()).getLessons());
        return "lessons";
    }

    @PostMapping("/getForTeacher")
    public String createTeacherTimetable(@ModelAttribute("timetable") DayTimetableDto timetable, Model model) {
        model.addAttribute("lessons",
                timetableService.createTeacherTimetable(timetable.getDay(), timetable.getTeacherEmail()).getLessons());
        return "lessons";
    }

    @GetMapping("/newLesson")
    public String newLesson(Model model) {
        model.addAttribute("groups", groupService.findAllGroups());
        model.addAttribute("teachers", teacherService.findAll());
        model.addAttribute("lesson", new LessonDto());
        model.addAttribute("courses", courseService.findAllCourses());
        model.addAttribute("classrooms", classroomService.findAllClassrooms());
        return "lessonform";
    }

    @PostMapping("/addLesson")
    public String addLesson(@ModelAttribute("lesson") LessonDto lesson, Model model) {
        lessonService.addLesson(lesson);
        String date = LocalDateTime.parse(lesson.getStartLesson()).toLocalDate().toString();
        model.addAttribute("lessons", timetableService.createDayTimetable(date).getLessons());
        return "lessons";
    }
    
    @PostMapping("/deleteLesson")
    public String deleteLesson(@ModelAttribute("lesson") LessonDto lesson, Model model) {
        lessonService.delete(lesson);
        String date = LocalDateTime.parse(lesson.getStartLesson()).toLocalDate().toString();
        model.addAttribute("lessons", timetableService.createDayTimetable(date).getLessons());
        return "lessons";
    }
}
