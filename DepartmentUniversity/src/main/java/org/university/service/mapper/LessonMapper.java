package org.university.service.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.university.dao.ClassroomDao;
import org.university.dao.CourseDao;
import org.university.dao.GroupDao;
import org.university.dao.TeacherDao;
import org.university.dto.LessonDto;
import org.university.entity.Lesson;

@Component
public class LessonMapper {    
    
    @Autowired
    private CourseDao courseDao;
    
    @Autowired
    private GroupDao groupDao;
    
    @Autowired
    private TeacherDao teacherDao;
    
    @Autowired
    private ClassroomDao classroomDao;    
    
    public Lesson mapDtoToEntity(LessonDto lesson) {
        return Lesson.builder()
                .withCourse(courseDao.findByName(lesson.getCourseName()).get())
                .withGroup(groupDao.findByName(lesson.getGroupName()).get())
                .withTeacher(teacherDao.findByEmail(lesson.getTeacherEmail()).get())
                .withClassroom(classroomDao.findByNumber(lesson.getClassroomNumber()).get())
                .withStartLesson(lesson.getStartLesson())
                .withEndLesson(lesson.getEndLesson())
                .withOnlineLesson(lesson.getOnlineLesson())
                .withLessonLink(lesson.getLessonLink())
                .build();
    }
}
