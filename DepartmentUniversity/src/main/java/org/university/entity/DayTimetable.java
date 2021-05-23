package org.university.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

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

    @Override
    public int hashCode() {
        return Objects.hash(day, lessons);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof DayTimetable))
            return false;
        DayTimetable other = (DayTimetable) obj;
        return Objects.equals(day, other.day) && Objects.equals(lessons, other.lessons);
    }
}
