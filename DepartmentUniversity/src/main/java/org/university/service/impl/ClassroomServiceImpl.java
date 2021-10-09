package org.university.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.university.dao.ClassroomDao;
import org.university.dto.ClassroomDto;
import org.university.entity.Classroom;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.service.ClassroomService;

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
public class ClassroomServiceImpl implements ClassroomService {

    ClassroomDao classroomDao;

    @Override
    public Classroom createClassroom(int classroomNumber) {
        if (!classroomDao.findByNumber(classroomNumber).isPresent()) {
            throw new EntityNotExistException();
        }
        return classroomDao.findByNumber(classroomNumber).get();
    }

    @Override
    public void addClassroom(@NonNull ClassroomDto classroomDto) {
        Classroom classroom = mapDtoToEntity(classroomDto);
        if (existClassroom(classroom)) {
            throw new EntityAlreadyExistException("classroomexist");
        } else {
            classroomDao.save(classroom);
            log.info("Classroom with number {} added succesfull!", classroom.getNumber());
        }      
    }

    @Override
    public List<Classroom> findAllClassrooms() {
        return (List<Classroom>) classroomDao.findAll();
    }

    @Override
    public void delete(@NonNull ClassroomDto classroomDto) {
        Classroom classroom = mapDtoToEntity(classroomDto);
        if (classroomDao.existsById(classroom.getId())) {
            classroomDao.deleteById(classroom.getId());
            log.info("Classroom with number {} deleted!", classroom.getNumber());
        }
    }
    
    @Override
    public void edit(@NonNull ClassroomDto classroomDto) {
        Classroom classroom = mapDtoToEntity(classroomDto);
        classroomDao.save(classroom);
        log.info("Classroom with number {} edited succesfull!", classroom.getNumber());        
    }

    private boolean existClassroom(Classroom classroom) {
        return classroomDao.findByNumber(classroom.getNumber()).isPresent();
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
