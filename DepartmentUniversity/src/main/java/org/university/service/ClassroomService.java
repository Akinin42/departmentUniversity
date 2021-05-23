package org.university.service;

import java.util.List;
import org.university.entity.Classroom;

public interface ClassroomService {
    
    Classroom createClassroom(int classroomNumber);

    void addClassroom(Classroom classroom);
    
    List<Classroom> findAllClassrooms();
    
    void delete(Classroom classroom);

}
