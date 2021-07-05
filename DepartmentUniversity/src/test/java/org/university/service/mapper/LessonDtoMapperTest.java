package org.university.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.university.dao.ClassroomDao;
import org.university.dao.CourseDao;
import org.university.dao.TeacherDao;
import org.university.dto.LessonDto;
import org.university.entity.Group;
import org.university.entity.Lesson;
import org.university.entity.Student;
import org.university.service.GroupService;
import org.university.utils.CreatorTestEntities;

@ExtendWith(MockitoExtension.class)
class LessonDtoMapperTest {
    
    @Mock
    private CourseDao courseDaoMock;
    
    @Mock
    private GroupService groupServiceMock;
    
    @Mock
    private TeacherDao teacherDaoMock;
    
    @Mock
    private ClassroomDao classroomDaoMock;
    
    @InjectMocks
    private LessonDtoMapper lessonMapper;    

    @Test
    void mapDtoToEntityShouldReturnExpectedLessonWhenInputValidLessonDto() {        
        when(courseDaoMock.findByName("Law")).thenReturn(Optional.of(CreatorTestEntities.createCourses().get(0)));
        when(groupServiceMock.createGroup("AB-22")).thenReturn(createGroupWithStudents());
        when(teacherDaoMock.findByEmail("Bob@mail.ru")).thenReturn(Optional.of(CreatorTestEntities.createTeachers().get(0)));
        when(classroomDaoMock.findByNumber(1)).thenReturn(Optional.of(CreatorTestEntities.createClassrooms().get(0)));
        LessonDto lessonDto = new LessonDto();
        lessonDto.setCourseName("Law");
        lessonDto.setGroupName("AB-22");
        lessonDto.setTeacherEmail("Bob@mail.ru");
        lessonDto.setClassroomNumber(1);
        lessonDto.setStartLesson("2021-10-19T10:00");
        lessonDto.setEndLesson("2021-10-19T12:00");
        lessonDto.setOnlineLesson(false);
        lessonDto.setLessonLink(null);        
        assertThat(lessonMapper.mapDtoToEntity(lessonDto)).isEqualTo(createExceptLesson());       
    }
    
    private Group createGroupWithStudents() {        
        List<Student> students = new ArrayList<>();
        students.add(CreatorTestEntities.createStudents().get(0));
        students.add(CreatorTestEntities.createStudents().get(1));
        students.add(CreatorTestEntities.createStudents().get(2));
        students.add(CreatorTestEntities.createStudents().get(3));
        return Group.builder()
                .withId(1)
                .withName("AB-22")
                .withStudents(students)
                .build();        
    }
    
    private Lesson createExceptLesson() {
        return Lesson.builder()
                .withStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00))
                .withEndLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 12, 00, 00))
                .withOnlineLesson(false)
                .withLessonLink(null)
                .withClassroom(CreatorTestEntities.createClassrooms().get(0))
                .withCourse(CreatorTestEntities.createCourses().get(0))
                .withTeacher(CreatorTestEntities.createTeachers().get(0))
                .withGroup(createGroupWithStudents())
                .build();
    }
}
