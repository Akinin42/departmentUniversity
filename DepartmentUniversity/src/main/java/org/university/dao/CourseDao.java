package org.university.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.university.entity.Course;

@Repository
public interface CourseDao extends CrudRepository<Course, Integer> {
    
    Optional<Course> findByName(String name);
}
