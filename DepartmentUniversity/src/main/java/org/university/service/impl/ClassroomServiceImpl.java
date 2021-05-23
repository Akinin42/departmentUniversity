package org.university.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.university.dao.ClassroomDao;
import org.university.entity.Classroom;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.exceptions.InvalidClassroomCapacityException;
import org.university.service.ClassroomService;

@Component
public class ClassroomServiceImpl implements ClassroomService {

    private final ClassroomDao classroomDao;

    public ClassroomServiceImpl(ClassroomDao classroomDao) {
        this.classroomDao = classroomDao;
    }

    @Override
    public Classroom createClassroom(int classroomNumber) {
        if (!classroomDao.findByNumber(classroomNumber).isPresent()) {
            throw new EntityNotExistException();
        }
        return classroomDao.findByNumber(classroomNumber).get();
    }

    @Override
    public void addClassroom(Classroom classroom) {
        if (existClassroom(classroom)) {
            throw new EntityAlreadyExistException();
        }
        if(classroom.getCapacity()<=0){
            throw new InvalidClassroomCapacityException();
        }
        classroomDao.save(classroom);
    }

    @Override
    public List<Classroom> findAllClassrooms() {
        return classroomDao.findAll();
    }

    @Override
    public void delete(Classroom classroom) {
        if (existClassroom(classroom)) {
            classroomDao.deleteById(classroom.getId());
        }
    }

    private boolean existClassroom(Classroom classroom) {
        if (classroom == null) {
            throw new IllegalArgumentException();
        }
        return !classroomDao.findById(classroom.getId()).equals(Optional.empty());
    }
}
