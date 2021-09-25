package org.university.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.university.dao.ClassroomDao;
import org.university.dto.ClassroomDto;
import org.university.entity.Classroom;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.service.ClassroomService;
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
public class ClassroomServiceImpl implements ClassroomService {

    ClassroomDao classroomDao;
    Validator<Classroom> validator;

    @Override
    @Transactional
    public Classroom createClassroom(int classroomNumber) {
        if (!classroomDao.findByNumber(classroomNumber).isPresent()) {
            throw new EntityNotExistException();
        }
        return classroomDao.findByNumber(classroomNumber).get();
    }

    @Override
    @Transactional
    public void addClassroom(@NonNull ClassroomDto classroomDto) {
        Classroom classroom = mapDtoToEntity(classroomDto);
        if (classroom.getId() != null && existClassroom(classroom)) {
            throw new EntityAlreadyExistException("classroomexist");
        }
        validator.validate(classroom);
        classroomDao.save(classroom);
        log.info("Classroom with number {} added succesfull!", classroom.getNumber());
    }

    @Override
    @Transactional
    public List<Classroom> findAllClassrooms() {
        return classroomDao.findAll();
    }

    @Override
    @Transactional
    public void delete(@NonNull ClassroomDto classroomDto) {
        Classroom classroom = mapDtoToEntity(classroomDto);
        if (existClassroom(classroom)) {
            classroomDao.deleteById(classroom.getId());
            log.info("Classroom with number {} deleted!", classroom.getNumber());
        }
    }
    
    @Override
    @Transactional
    public void edit(@NonNull ClassroomDto classroomDto) {
        Classroom classroom = mapDtoToEntity(classroomDto);
        validator.validate(classroom);
        classroomDao.update(classroom);
        log.info("Classroom with number {} edited succesfull!", classroom.getNumber());        
    }

    private boolean existClassroom(Classroom classroom) {        
        return !classroomDao.findById(classroom.getId()).equals(Optional.empty());
    }
    
    private Classroom mapDtoToEntity(ClassroomDto classroomDto) {
        return Classroom.builder()
                .withId(classroomDto.getId())
                .withNumber(classroomDto.getNumber())
                .withAddress(classroomDto.getAddress())
                .withCapacity(classroomDto.getCapacity())
                .build();
    }    
}
