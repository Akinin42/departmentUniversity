package org.university.dao;

import java.util.List;

import org.university.entity.Lesson;

public interface LessonDao extends CrudDao<Lesson, Integer> {
    
    List<Lesson> findAllByDateAndTeacher(String date, int teacherId);
    
    List<Lesson> findAllByDateAndGroup(String date, int groupId);
}
