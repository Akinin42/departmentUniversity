package org.university.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.university.entity.DayTimetable;
import org.university.entity.Lesson;

@Component()
public class CSVDataGenerator {

    public List<String[]> generateGroupsData(List<DayTimetable> timetables) {
        List<String[]> groupsData = new ArrayList<>();
        String[] csvHeader = { "Date", "Start lesson", "End lesson", "Teacher", "Course", "Classroom", "Lesson link" };
        groupsData.add(csvHeader);
        for (DayTimetable timetable : timetables) {
            List<Lesson> lessons = timetable.getLessons();
            String date = timetable.getDay().toString();
            for (int i = 0; i < lessons.size(); i++) {
                if (i >= 1) {
                    date = "";
                }
                String startLesson = lessons.get(i).getStartLesson().toLocalTime().toString();
                String endLesson = lessons.get(i).getEndLesson().toLocalTime().toString();
                String teacherName = lessons.get(i).getTeacher().getName();
                String courseName = lessons.get(i).getCourse().getName();
                int classroomNumber = lessons.get(i).getClassroom().getNumber();
                String lessonLink = lessons.get(i).getLessonLink();
                String[] row = { date, startLesson, endLesson, teacherName, courseName,
                        Integer.toString(classroomNumber), lessonLink };
                groupsData.add(row);
            }
        }
        return groupsData;
    }

    public List<String[]> generateTeachersData(List<DayTimetable> timetables) {
        List<String[]> teachersData = new ArrayList<>();
        String[] csvHeader = { "Date", "Start lesson", "End lesson", "Group", "Course", "Classroom", "Lesson link" };
        teachersData.add(csvHeader);
        for (DayTimetable timetable : timetables) {
            List<Lesson> lessons = timetable.getLessons();
            String date = timetable.getDay().toString();
            for (int i = 0; i < lessons.size(); i++) {
                if (i >= 1) {
                    date = "";
                }
                String startLesson = lessons.get(i).getStartLesson().toLocalTime().toString();
                String endLesson = lessons.get(i).getEndLesson().toLocalTime().toString();
                String groupName = lessons.get(i).getGroup().getName();
                String courseName = lessons.get(i).getCourse().getName();
                int classroomNumber = lessons.get(i).getClassroom().getNumber();
                String lessonLink = lessons.get(i).getLessonLink();
                String[] row = { date, startLesson, endLesson, groupName, courseName, Integer.toString(classroomNumber),
                        lessonLink };
                teachersData.add(row);
            }
        }
        return teachersData;
    }

}
