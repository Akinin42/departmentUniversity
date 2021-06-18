package org.university.service;

import java.util.List;
import org.university.dto.ClassroomDto;
import org.university.entity.Classroom;

public interface ClassroomService {
    
    Classroom createClassroom(int classroomNumber);

    void addClassroom(ClassroomDto classroomDto);
    
    List<Classroom> findAllClassrooms();
    
    void delete(Classroom classroom);

}
