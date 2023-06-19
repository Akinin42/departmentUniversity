package org.university.api.v1;

import java.time.LocalDate;
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
import org.springframework.web.server.ResponseStatusException;
import org.university.dto.DayTimetableDto;
import org.university.dto.LessonDto;
import org.university.entity.DayTimetable;
import org.university.exceptions.ClassroomBusyException;
import org.university.exceptions.InvalidClassroomCapacityException;
import org.university.exceptions.InvalidLessonTimeException;
import org.university.exceptions.InvalidLinkException;
import org.university.service.DayTimetableService;
import org.university.service.LessonService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/v1/timetables")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class TimetableControllerRest {

    DayTimetableService timetableService;
    LessonService lessonService;

    @GetMapping()
    public DayTimetable getTimetable() {
        return timetableService.createDayTimetable(LocalDate.now());
    }

    @GetMapping("/date")
    public DayTimetable getTimetableOnDay(@RequestBody DayTimetableDto timetable) {
        if (timetable.getDay().equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date can not be empty!");
        }
        return timetableService.createDayTimetable(LocalDate.parse(timetable.getDay()));
    }

    @GetMapping("/group")
    public DayTimetable createGroupTimetable(@RequestBody DayTimetableDto timetable) {
        if (timetable.getDay().equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date can not be empty!");
        }
        return timetableService.createGroupTimetable(LocalDate.parse(timetable.getDay()), timetable.getGroupName());
    }

    @GetMapping("/weekgroup/{groupname}")
    public List<DayTimetable> createWeekGroupTimetable(@PathVariable("groupname") String groupName) {
        return timetableService.createWeekGroupTimetable(LocalDate.now(), groupName);
    }

    @GetMapping("/monthgroup/{groupname}")
    public List<DayTimetable> createMonthGroupTimetable(@PathVariable("groupname") String groupName) {
        return timetableService.createMonthGroupTimetable(LocalDate.now(), groupName);
    }

    @GetMapping("/teacher")
    public DayTimetable createTeacherTimetable(@RequestBody DayTimetableDto timetable) {
        if (timetable.getDay().equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date can not be empty!");
        }
        return timetableService.createTeacherTimetable(LocalDate.parse(timetable.getDay()),
                timetable.getTeacherEmail());
    }

    @GetMapping("/weekteacher/{teacheremail}")
    public List<DayTimetable> createWeekTeacherTimetable(@PathVariable("teacheremail") String teacherEmail) {
        return timetableService.createWeekTeacherTimetable(LocalDate.now(), teacherEmail);
    }

    @GetMapping("/monthteacher/{teacheremail}")
    public List<DayTimetable> createMonthTeacherTimetable(@PathVariable("teacheremail") String teacherEmail) {
        return timetableService.createMonthTeacherTimetable(LocalDate.now(), teacherEmail);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void addLesson(@Valid @RequestBody LessonDto lesson) {
        try {
            lessonService.addLesson(lesson);
        } catch (InvalidLessonTimeException | InvalidClassroomCapacityException | ClassroomBusyException
                | InvalidLinkException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void deleteLesson(@PathVariable("id") int id) {
        LessonDto lesson = new LessonDto();
        lesson.setId(id);
        lessonService.delete(lesson);
    }

    @PatchMapping()
    public void edit(@Valid @RequestBody LessonDto lesson) {
        try {
            lessonService.edit(lesson);
        } catch (InvalidLessonTimeException | InvalidClassroomCapacityException | ClassroomBusyException
                | InvalidLinkException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
