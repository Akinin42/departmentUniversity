package org.university.service.validator;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.university.entity.Lesson;
import org.university.exceptions.InvalidClassroomCapacityException;
import org.university.exceptions.InvalidLessonTimeException;
import org.university.exceptions.InvalidLinkException;

@Component
public class LessonValidator implements Validator<Lesson> {

    private static final int LESSONS_START_TIME = 9;
    private static final int LESSONS_FINISH_TIME = 18;
    private static final Pattern LESSON_LINK = Pattern.compile(".{5,100}");

    @Override
    public void validate(Lesson lesson) {
        if (lesson.getGroup().getStudents().size() > lesson.getClassroom().getCapacity()) {
            throw new InvalidClassroomCapacityException("lessoncapacity");
        }
        LocalDateTime inputLessonStart = lesson.getStartLesson();
        LocalDateTime inputLessonEnd = lesson.getEndLesson();
        if (inputLessonStart.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            throw new InvalidLessonTimeException("sunday");
        }
        if (inputLessonStart.getHour() < LESSONS_START_TIME || inputLessonStart.getHour() > LESSONS_FINISH_TIME) {
            throw new InvalidLessonTimeException("notworktime");
        }
        if (inputLessonEnd.isBefore(inputLessonStart) || inputLessonEnd.isEqual(inputLessonStart)) {
            throw new InvalidLessonTimeException("endearlierstart");
        }
        if (Boolean.TRUE.equals(lesson.getOnlineLesson())
                && (lesson.getLessonLink() == null || !LESSON_LINK.matcher(lesson.getLessonLink()).matches())) {
            throw new InvalidLinkException("onlinelink");
        }
    }
}
