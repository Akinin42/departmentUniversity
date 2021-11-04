package org.university.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

@Controller
@RequestMapping("/csv")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Slf4j
public class CSVController {

    private static final String CSV = "text/csv";

    DayTimetableService timetableService;
    CSVDataGenerator csvGenerator;

    @PostMapping("/weekgroup")
    public void createWeekGroupTimetableCSV(@ModelAttribute("group") GroupDto group, HttpServletResponse response) {
        List<DayTimetable> timetables = timetableService.createWeekGroupTimetable(LocalDate.now(), group.getName());
        createGroupTimetable(timetables, response, group.getName());
    }

    @PostMapping("/monthgroup")
    public void createMonthGroupTimetableCSV(@ModelAttribute("group") GroupDto group, HttpServletResponse response) {
        List<DayTimetable> timetables = timetableService.createMonthGroupTimetable(LocalDate.now(), group.getName());
        createGroupTimetable(timetables, response, group.getName());
    }

    @PostMapping("/weekteacher")
    public void createWeekTeacherTimetableCSV(@ModelAttribute("teacher") UserDto teacher,
            HttpServletResponse response) {
        List<DayTimetable> timetables = timetableService.createWeekTeacherTimetable(LocalDate.now(),
                teacher.getEmail());
        createTeacherTimetable(timetables, response, teacher.getName());
    }

    @PostMapping("/monthteacher")
    public void createMonthTeacherTimetableCSV(@ModelAttribute("teacher") UserDto teacher,
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
