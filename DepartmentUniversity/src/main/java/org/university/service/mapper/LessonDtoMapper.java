package org.university.service.mapper;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.university.dao.ClassroomDao;
import org.university.dao.CourseDao;
import org.university.dao.TeacherDao;
import org.university.dto.LessonDto;
import org.university.entity.Lesson;
import org.university.service.GroupService;

@Component
public class LessonDtoMapper {    
    
    @Autowired
    private CourseDao courseDao;
    
    @Autowired
    private GroupService groupService;
    
    @Autowired
    private TeacherDao teacherDao;
    
    @Autowired
    private ClassroomDao classroomDao;    
    
    public Lesson mapDtoToEntity(LessonDto lesson) {
        return Lesson.builder()
                .withCourse(courseDao.findByName(lesson.getCourseName()).get())
                .withGroup(groupService.createGroup(lesson.getGroupName()))
                .withTeacher(teacherDao.findByEmail(lesson.getTeacherEmail()).get())
                .withClassroom(classroomDao.findByNumber(lesson.getClassroomNumber()).get())
                .withStartLesson(LocalDateTime.parse(lesson.getStartLesson()))
                .withEndLesson(LocalDateTime.parse(lesson.getEndLesson()))
                .withOnlineLesson(lesson.getOnlineLesson())
                .withLessonLink(lesson.getLessonLink())
                .build();
    }
}
