package org.university.service;

import java.util.List;

import org.university.entity.DayTimetable;

public interface DayTimetableService {
    
    DayTimetable createTeacherTimetable(String date, String teacherEmail);
    
    DayTimetable createGroupTimetable(String date, String groupName);
    
    List<DayTimetable> createMonthTeacherTimetable(String date, String teacherEmail);
    
    List<DayTimetable> createMonthGroupTimetable(String date, String groupName);
    
    DayTimetable createDayTimetable(String date);

}
