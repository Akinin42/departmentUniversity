package org.university.dao;

import java.util.Optional;
import org.university.entity.Classroom;

public interface ClassroomDao extends CrudDao<Classroom, Integer> {

    Optional<Classroom> findByNumber(int number);
}
