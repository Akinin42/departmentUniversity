package org.university.dao;

import java.util.List;
import java.util.Optional;
import org.university.entity.Course;

public interface CourseDao extends CrudDao<Course, Integer> {
    
    Optional<Course> findByName(String name);
    
    List<Course> findAllByStudent(int studentId);
}
