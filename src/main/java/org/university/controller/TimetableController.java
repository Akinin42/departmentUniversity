package org.university.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.university.dto.DayTimetableDto;
import org.university.dto.GroupDto;
import org.university.dto.LessonDto;
import org.university.dto.UserDto;
import org.university.exceptions.ClassroomBusyException;
import org.university.exceptions.InvalidClassroomCapacityException;
import org.university.exceptions.InvalidLessonTimeException;
import org.university.exceptions.InvalidLinkException;
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

    private static final String LESSON_FORM = "lessonform";
    DayTimetableService timetableService;
    GroupService groupService;
    TeacherService teacherService;
    CourseService courseService;
    ClassroomService classroomService;
    LessonService lessonService;

    @GetMapping()
    public String getTimetable(Model model) {
        model.addAttribute("lessons", timetableService.createDayTimetable(LocalDate.now()).getLessons());
        return "lessons";
    }

    @PostMapping("/date")
    public String getTimetableOnDay(@ModelAttribute("timetable") DayTimetableDto timetable, Model model) {
        if (timetable.getDay().equals("")) {
            return "redirect:/timetables";
        }
        model.addAttribute("lessons",
                timetableService.createDayTimetable(LocalDate.parse(timetable.getDay())).getLessons());
        return "lessons";
    }

    @PostMapping("/group")
    public String createGroupTimetable(@ModelAttribute("timetable") DayTimetableDto timetable, Model model) {
        if (timetable.getDay().equals("")) {
            return "redirect:/groups";
        }
        model.addAttribute("lessons", timetableService
                .createGroupTimetable(LocalDate.parse(timetable.getDay()), timetable.getGroupName()).getLessons());
        return "lessons";
    }

    @PostMapping("/weekgroup")
    public String createWeekGroupTimetable(@ModelAttribute("group") GroupDto group, Model model) {
        model.addAttribute("timetables", timetableService.createWeekGroupTimetable(LocalDate.now(), group.getName()));
        model.addAttribute("group", group);
        return "grouptimetable";
    }

    @PostMapping("/monthgroup")
    public String createMonthGroupTimetable(@ModelAttribute("group") GroupDto group, Model model) {
        model.addAttribute("timetables", timetableService.createMonthGroupTimetable(LocalDate.now(), group.getName()));
        model.addAttribute("group", group);
        return "grouptimetable";
    }

    @PostMapping("/teacher")
    public String createTeacherTimetable(@ModelAttribute("timetable") DayTimetableDto timetable, Model model) {
        if (timetable.getDay().equals("")) {
            return "redirect:/teachers";
        }
        model.addAttribute("lessons", timetableService
                .createTeacherTimetable(LocalDate.parse(timetable.getDay()), timetable.getTeacherEmail()).getLessons());
        return "lessons";
    }

    @PostMapping("/weekteacher")
    public String createWeekTeacherTimetable(@ModelAttribute("teacher") UserDto teacher, Model model) {
        model.addAttribute("timetables",
                timetableService.createWeekTeacherTimetable(LocalDate.now(), teacher.getEmail()));
        model.addAttribute("teacher", teacher);
        return "teachertimetable";
    }

    @PostMapping("/monthteacher")
    public String createMonthTeacherTimetable(@ModelAttribute("teacher") UserDto teacher, Model model) {
        model.addAttribute("timetables",
                timetableService.createMonthTeacherTimetable(LocalDate.now(), teacher.getEmail()));
        model.addAttribute("teacher", teacher);
        return "teachertimetable";
    }

    @GetMapping("/new")
    public String newLesson(@ModelAttribute("message") String message, Model model) {
        model.addAttribute("groups", groupService.findAllGroups());
        model.addAttribute("teachers", teacherService.findAll());
        model.addAttribute("lesson", new LessonDto());
        model.addAttribute("courses", courseService.findAllCourses());
        model.addAttribute("classrooms", classroomService.findAllClassrooms());
        return LESSON_FORM;
    }

    @PostMapping()
    public String addLesson(@ModelAttribute("lesson") @Valid LessonDto lesson, BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "invalid date");
            return "redirect:/timetables/new";
        }
        try {
            lessonService.addLesson(lesson);
            LocalDate date = LocalDateTime.parse(lesson.getStartLesson()).toLocalDate();
            model.addAttribute("lessons", timetableService.createDayTimetable(date).getLessons());
            return "lessons";
        } catch (InvalidLessonTimeException | InvalidClassroomCapacityException | ClassroomBusyException
                | InvalidLinkException e) {
            model.addAttribute("message", e.getMessage());
            return "redirect:/timetables/new";
        }
    }

    @DeleteMapping()
    public String deleteLesson(@ModelAttribute("lesson") LessonDto lesson, Model model) {
        lessonService.delete(lesson);
        LocalDate date = LocalDateTime.parse(lesson.getStartLesson()).toLocalDate();
        model.addAttribute("lessons", timetableService.createDayTimetable(date).getLessons());
        return "lessons";
    }

    @GetMapping("/edit")
    public String getEditForm(@ModelAttribute("lesson") LessonDto lesson, @ModelAttribute("message") String message,
            Model model) {
        model.addAttribute("groups", groupService.findAllGroups());
        model.addAttribute("teachers", teacherService.findAll());
        model.addAttribute("courses", courseService.findAllCourses());
        model.addAttribute("classrooms", classroomService.findAllClassrooms());
        model.addAttribute("lesson", lesson);
        return "updateforms/lesson";
    }

    @PatchMapping()
    public String edit(@ModelAttribute("lesson") @Valid LessonDto lesson, BindingResult bindingResult,
            @ModelAttribute("message") String message, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "invalid date");
            return "redirect:/timetables/edit";
        }
        try {
            lessonService.edit(lesson);
            LocalDate date = LocalDateTime.parse(lesson.getStartLesson()).toLocalDate();
            model.addAttribute("lessons", timetableService.createDayTimetable(date).getLessons());
            return "lessons";
        } catch (InvalidLessonTimeException | InvalidClassroomCapacityException | ClassroomBusyException
                | InvalidLinkException e) {
            model.addAttribute("lesson", lesson);
            model.addAttribute("message", e.getMessage());
            return "redirect:/timetables/edit";
        }
    }
}
