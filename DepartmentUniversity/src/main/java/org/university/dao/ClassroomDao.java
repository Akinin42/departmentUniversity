package org.university.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.university.entity.Classroom;

@Repository
public interface ClassroomDao extends CrudRepository<Classroom, Integer> {

    Optional<Classroom> findByNumber(Integer number);
}
