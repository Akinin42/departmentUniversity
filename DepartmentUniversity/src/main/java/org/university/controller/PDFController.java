package org.university.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.university.dto.GroupDto;
import org.university.dto.TeacherDto;
import org.university.entity.DayTimetable;
import org.university.service.DayTimetableService;
import org.university.utils.PDFDataGenerator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/pdf")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Slf4j
public class PDFController {

    private static final String PDF = "application/pdf";

    DayTimetableService timetableService;
    PDFDataGenerator pdfGenerator;

    @PostMapping("/weekgroup")
    public void createWeekGroupTimetablePDF(@ModelAttribute("group") GroupDto group, HttpServletResponse response) {
        List<DayTimetable> timetables = timetableService.createWeekGroupTimetable(LocalDate.now(), group.getName());
        createTimetable(timetables, response, group);
    }

    @PostMapping("/monthgroup")
    public void createMonthGroupTimetablePDF(@ModelAttribute("group") GroupDto group, HttpServletResponse response) {
        List<DayTimetable> timetables = timetableService.createMonthGroupTimetable(LocalDate.now(), group.getName());
        createTimetable(timetables, response, group);
    }

    @PostMapping("/weekteacher")
    public void createWeekTeacherTimetablePDF(@ModelAttribute("teacher") TeacherDto teacher,
            HttpServletResponse response) {
        List<DayTimetable> timetables = timetableService.createWeekTeacherTimetable(LocalDate.now(),
                teacher.getEmail());
        createTimetable(timetables, response, teacher);
    }

    @PostMapping("/monthteacher")
    public void createMonthTeacherTimetablePDF(@ModelAttribute("teacher") TeacherDto teacher,
            HttpServletResponse response) {
        List<DayTimetable> timetables = timetableService.createMonthTeacherTimetable(LocalDate.now(),
                teacher.getEmail());
        createTimetable(timetables, response, teacher);
    }

    private void createTimetable(List<DayTimetable> timetables, HttpServletResponse response, Object entity) {
        try (ServletOutputStream output = response.getOutputStream()) {
            response.setHeader("X-Frame-Options", "");
            response.setContentType(PDF);
            if (entity.getClass().equals(GroupDto.class)) {
                pdfGenerator.generateGroupTimetable(output, timetables, ((GroupDto) entity).getName());
            } else {
                pdfGenerator.generateTeacherTimetable(output, timetables, ((TeacherDto) entity).getName());
            }
        } catch (IOException e) {
            log.error("File creation failed!");
        }
    }
}
