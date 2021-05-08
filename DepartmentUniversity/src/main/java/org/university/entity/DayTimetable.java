package org.university.entity;

import java.time.LocalDate;
import java.util.List;

public class DayTimetable {

    private final LocalDate day;
    private final List<Lesson> lessons;

    public DayTimetable(LocalDate day, List<Lesson> lessons) {
        this.day = day;
        this.lessons = lessons;
    }

    public LocalDate getDay() {
        return day;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }
}
