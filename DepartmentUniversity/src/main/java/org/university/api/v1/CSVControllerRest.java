package org.university.api.v1;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

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

    @GetMapping("/weekgroup")    
    public void createWeekGroupTimetableCSV(@RequestBody GroupDto group, HttpServletResponse response) {
        List<DayTimetable> timetables = timetableService.createWeekGroupTimetable(LocalDate.now(), group.getName());
        createGroupTimetable(timetables, response, group.getName());        
    }

    @GetMapping("/monthgroup")    
    public void createMonthGroupTimetableCSV(@RequestBody GroupDto group, HttpServletResponse response) {
        List<DayTimetable> timetables = timetableService.createMonthGroupTimetable(LocalDate.now(), group.getName());
        createGroupTimetable(timetables, response, group.getName());
    }

    @GetMapping("/weekteacher")    
    public void createWeekTeacherTimetableCSV(@RequestBody UserDto teacher,
            HttpServletResponse response) {
        List<DayTimetable> timetables = timetableService.createWeekTeacherTimetable(LocalDate.now(),
                teacher.getEmail());
        createTeacherTimetable(timetables, response, teacher.getName());
    }

    @GetMapping("/monthteacher")    
    public void createMonthTeacherTimetableCSV(@RequestBody UserDto teacher,
            HttpServletResponse response) {
        List<DayTimetable> timetables = timetableService.createMonthTeacherTimetable(LocalDate.now(),
                teacher.getEmail());
        createTeacherTimetable(timetables, response, teacher.getName());
    }

    private void createGroupTimetable(List<DayTimetable> timetables, HttpServletResponse response, String groupName){
        response.setContentType(CSV);
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=%s timetable.csv", groupName);
        response.setHeader(headerKey, headerValue);
        response.setStatus(500);
        try (CSVWriter writer = new CSVWriter(response.getWriter())) {
            writer.writeAll(csvGenerator.generateGroupsData(timetables));
            response.setStatus(200);
        } catch (IOException e) {
            log.error("File creation failed!");            
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File creation failed!");
        }
    }

    private void createTeacherTimetable(List<DayTimetable> timetables, HttpServletResponse response,
            String teacherName) {
        response.setContentType(CSV);
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=%s timetable.csv", teacherName);
        response.setHeader(headerKey, headerValue);
        response.setStatus(500);
        try (CSVWriter writer = new CSVWriter(response.getWriter())) {            
            writer.writeAll(csvGenerator.generateTeachersData(timetables));
            response.setStatus(200);
        } catch (IOException e) {
            log.error("File creation failed!");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File creation failed!");
        }
    }
}
