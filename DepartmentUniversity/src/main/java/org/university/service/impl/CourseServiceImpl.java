package org.university.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.university.dao.CourseDao;
import org.university.dto.CourseDto;
import org.university.entity.Course;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.service.CourseService;
import org.university.service.validator.Validator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Transactional
public class CourseServiceImpl implements CourseService {

    CourseDao courseDao;
    Validator<Course> validator;

    @Override
    @Transactional
    public Course createCourse(String courseName) {
        if (!courseDao.findByName(courseName).isPresent()) {
            throw new EntityNotExistException();
        }
        return courseDao.findByName(courseName).get();
    }

    @Override
    @Transactional
    public void addCourse(@NonNull CourseDto courseDto) {
        Course course = mapDtoToEntity(courseDto);        
        if (existCourse(course)) {
            throw new EntityAlreadyExistException("courseexist");
        }
        validator.validate(course);
        courseDao.save(course);
        log.info("Course with name {} added succesfull!", course.getName());
    }

    @Override
    @Transactional
    public List<Course> findAllCourses() {
        return courseDao.findAll();
    }

    @Override
    @Transactional
    public void delete(@NonNull CourseDto courseDto) {
        Course course = mapDtoToEntity(courseDto);
        if (existCourse(course)) {
            courseDao.deleteById(course.getId());
            log.info("Course with name {} deleted!", course.getName());
        }
    }
    
    @Override
    @Transactional
    public void edit(@NonNull CourseDto courseDto) {
        Course course = mapDtoToEntity(courseDto);
        if (!courseDao.findById(course.getId()).get().getName().equals(course.getName())&&existCourse(course)) {
            throw new EntityAlreadyExistException("courseexist");
        }
        validator.validate(course);
        courseDao.update(course);
        log.info("Course with name {} edited succesfull!", course.getName());
    }

    private boolean existCourse(Course course) {
        return !courseDao.findByName(course.getName()).equals(Optional.empty());
    }

    private Course mapDtoToEntity(CourseDto course) {
        return Course.builder()
                .withId(course.getId())
                .withName(course.getName())
                .withDescription(course.getDescription())
                .build();
    }    
}
