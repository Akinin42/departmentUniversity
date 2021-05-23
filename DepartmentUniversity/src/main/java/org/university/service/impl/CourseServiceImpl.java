package org.university.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.university.dao.CourseDao;
import org.university.entity.Course;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.service.CourseService;

@Component
public class CourseServiceImpl implements CourseService {

    private final CourseDao courseDao;

    public CourseServiceImpl(CourseDao courseDao) {
        this.courseDao = courseDao;
    }

    @Override
    public Course createCourse(String courseName) {
        if (!courseDao.findByName(courseName).isPresent()) {
            throw new EntityNotExistException();
        }
        return courseDao.findByName(courseName).get();
    }

    @Override
    public void addCourse(Course course) {
        if (existCourse(course)) {
            throw new EntityAlreadyExistException();
        }
        courseDao.save(course);
    }

    @Override
    public List<Course> findAllCourses() {
        return courseDao.findAll();
    }

    @Override
    public void delete(Course course) {
        if(existCourse(course)) {
            courseDao.deleteById(course.getId());
        }        
    }

    private boolean existCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException();
        }
        return !courseDao.findById(course.getId()).equals(Optional.empty());
    }
}
