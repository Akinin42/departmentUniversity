package org.university.controller.rest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.university.dto.GroupDto;
import org.university.dto.UserDto;
import org.university.entity.DayTimetable;
import org.university.service.DayTimetableService;
import org.university.utils.CSVDataGenerator;

import com.opencsv.CSVWriter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/csv")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Slf4j
public class CSVControllerRest {

    private static final String CSV = "text/csv";

    DayTimetableService timetableService;
    CSVDataGenerator csvGenerator;

    @PostMapping("/weekgroup")
    @ResponseStatus(HttpStatus.OK)
    public void createWeekGroupTimetableCSV(@RequestBody GroupDto group, HttpServletResponse response) {
        List<DayTimetable> timetables = timetableService.createWeekGroupTimetable(LocalDate.now(), group.getName());
        createGroupTimetable(timetables, response, group.getName());
    }

    @PostMapping("/monthgroup")
    @ResponseStatus(HttpStatus.OK)
    public void createMonthGroupTimetableCSV(@RequestBody GroupDto group, HttpServletResponse response) {
        List<DayTimetable> timetables = timetableService.createMonthGroupTimetable(LocalDate.now(), group.getName());
        createGroupTimetable(timetables, response, group.getName());
    }

    @PostMapping("/weekteacher")
    @ResponseStatus(HttpStatus.OK)
    public void createWeekTeacherTimetableCSV(@RequestBody UserDto teacher,
            HttpServletResponse response) {
        List<DayTimetable> timetables = timetableService.createWeekTeacherTimetable(LocalDate.now(),
                teacher.getEmail());
        createTeacherTimetable(timetables, response, teacher.getName());
    }

    @PostMapping("/monthteacher")
    @ResponseStatus(HttpStatus.OK)
    public void createMonthTeacherTimetableCSV(@RequestBody UserDto teacher,
            HttpServletResponse response) {
        List<DayTimetable> timetables = timetableService.createMonthTeacherTimetable(LocalDate.now(),
                teacher.getEmail());
        createTeacherTimetable(timetables, response, teacher.getName());
    }

    private void createGroupTimetable(List<DayTimetable> timetables, HttpServletResponse response, String groupName) {
        response.setContentType(CSV);
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=%s timetable.csv", groupName);
        response.setHeader(headerKey, headerValue);
        try (CSVWriter writer = new CSVWriter(response.getWriter())) {
            writer.writeAll(csvGenerator.generateGroupsData(timetables));
        } catch (IOException e) {
            log.error("File creation failed!");
        }
    }

    private void createTeacherTimetable(List<DayTimetable> timetables, HttpServletResponse response,
            String teacherName) {
        response.setContentType(CSV);
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=%s timetable.csv", teacherName);
        response.setHeader(headerKey, headerValue);
        try (CSVWriter writer = new CSVWriter(response.getWriter())) {
            writer.writeAll(csvGenerator.generateTeachersData(timetables));
        } catch (IOException e) {
            log.error("File creation failed!");
        }
    }
}
