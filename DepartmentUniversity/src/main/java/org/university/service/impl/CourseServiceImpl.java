package org.university.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.university.dao.CourseDao;
import org.university.dto.CourseDto;
import org.university.entity.Course;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.service.CourseService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class CourseServiceImpl implements CourseService {

    CourseDao courseDao;

    @Override
    public Course createCourse(String courseName) {
        if (!courseDao.findByName(courseName).isPresent()) {
            throw new EntityNotExistException();
        }
        return courseDao.findByName(courseName).get();
    }

    @Override
    public void addCourse(@NonNull CourseDto courseDto) {
        Course course = mapDtoToEntity(courseDto);
        if (course.getId() != null && existCourse(course)) {
            throw new EntityAlreadyExistException();
        }
        courseDao.save(course);
        log.info("Course with name {} added succesfull!", course.getName());
    }

    @Override
    public List<Course> findAllCourses() {
        return courseDao.findAll();
    }

    @Override
    public void delete(@NonNull Course course) {
        if (existCourse(course)) {
            courseDao.deleteById(course.getId());
            log.info("Course with name {} deleted!", course.getName());
        }
    }

    private boolean existCourse(Course course) {
        return !courseDao.findById(course.getId()).equals(Optional.empty());
    }

    private Course mapDtoToEntity(CourseDto course) {
        return Course.builder()
                .withId(course.getId())
                .withName(course.getName())
                .withDescription(course.getDescription())
                .build();
    }
}
