package org.university.service.validator;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import org.university.entity.Lesson;
import org.university.exceptions.InvalidClassroomCapacityException;
import org.university.exceptions.InvalidLessonTimeException;

@Component
public class LessonValidator implements Validator<Lesson> {

    private static final int LESSONS_START_TIME = 9;
    private static final int LESSONS_FINISH_TIME = 18;

    @Override
    public void validate(Lesson lesson) {
        if (lesson.getGroup().getStudents().size() > lesson.getClassroom().getCapacity()) {
            throw new InvalidClassroomCapacityException("Number student in group more than classroom capacity!");
        }
        LocalDateTime inputLessonStart = lesson.getStartLesson();
        LocalDateTime inputLessonEnd = lesson.getEndLesson();
        if (inputLessonStart.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            throw new InvalidLessonTimeException();
        }
        if (inputLessonStart.getHour() < LESSONS_START_TIME || inputLessonStart.getHour() > LESSONS_FINISH_TIME) {
            throw new InvalidLessonTimeException();
        }
        if (inputLessonEnd.isBefore(inputLessonStart) || inputLessonEnd.isEqual(inputLessonStart)) {
            throw new InvalidLessonTimeException();
        }
    }
}
