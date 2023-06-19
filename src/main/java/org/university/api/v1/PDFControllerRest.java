package org.university.api.v1;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.university.dto.GroupDto;
import org.university.dto.UserDto;
import org.university.entity.DayTimetable;
import org.university.service.DayTimetableService;
import org.university.utils.PDFDataGenerator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/pdf")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Slf4j
public class PDFControllerRest {

    private static final String PDF = "application/pdf";

    DayTimetableService timetableService;
    PDFDataGenerator pdfGenerator;

    @GetMapping("/weekgroup")    
    public void createWeekGroupTimetablePDF(@RequestBody GroupDto group, HttpServletResponse response) {
        List<DayTimetable> timetables = timetableService.createWeekGroupTimetable(LocalDate.now(), group.getName());
        createTimetable(timetables, response, group);
    }

    @GetMapping("/monthgroup")    
    public void createMonthGroupTimetablePDF(@RequestBody GroupDto group, HttpServletResponse response) {
        List<DayTimetable> timetables = timetableService.createMonthGroupTimetable(LocalDate.now(), group.getName());
        createTimetable(timetables, response, group);
    }

    @GetMapping("/weekteacher")    
    public void createWeekTeacherTimetablePDF(@RequestBody UserDto teacher, HttpServletResponse response) {
        List<DayTimetable> timetables = timetableService.createWeekTeacherTimetable(LocalDate.now(),
                teacher.getEmail());
        createTimetable(timetables, response, teacher);
    }

    @GetMapping("/monthteacher")    
    public void createMonthTeacherTimetablePDF(@RequestBody UserDto teacher, HttpServletResponse response) {
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
                pdfGenerator.generateTeacherTimetable(output, timetables, ((UserDto) entity).getName());
            }
        } catch (IOException e) {
            log.error("File creation failed!");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File creation failed!");
        }
    }
}
