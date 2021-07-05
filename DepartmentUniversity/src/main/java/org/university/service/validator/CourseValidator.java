package org.university.service.validator;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import org.university.entity.Course;
import org.university.exceptions.InvalidCourseNameException;
import org.university.exceptions.InvalidDescriptionException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CourseValidator implements Validator<Course> {

    private static final Pattern NAME_PATTERN = Pattern.compile("[A-Za-z]{2,50}");
    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile(".{5,}");

    @Override
    public void validate(Course course) {
        if (!NAME_PATTERN.matcher(course.getName()).matches()) {
            log.error("Input course has invalid name: " + course.getName());
            throw new InvalidCourseNameException("Input course name isn't valid!");
        }
        if (!DESCRIPTION_PATTERN.matcher(course.getDescription()).matches()) {
            log.error("Input course has invalid description: " + course.getDescription());
            throw new InvalidDescriptionException("Input course description isn't valid!");
        }        
    }
}
