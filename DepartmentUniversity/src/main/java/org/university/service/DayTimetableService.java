package org.university.service;

import java.time.LocalDate;
import java.util.List;

import org.university.entity.DayTimetable;

public interface DayTimetableService {
    
    DayTimetable createTeacherTimetable(LocalDate date, String teacherEmail);
    
    DayTimetable createGroupTimetable(LocalDate date, String groupName);
    
    List<DayTimetable> createMonthTeacherTimetable(LocalDate date, String teacherEmail);
    
    List<DayTimetable> createMonthGroupTimetable(LocalDate date, String groupName);
    
    DayTimetable createDayTimetable(LocalDate date);
    
    List<DayTimetable> createWeekTeacherTimetable(LocalDate date, String teacherEmail);
    
    List<DayTimetable> createWeekGroupTimetable(LocalDate date, String groupName);
}
