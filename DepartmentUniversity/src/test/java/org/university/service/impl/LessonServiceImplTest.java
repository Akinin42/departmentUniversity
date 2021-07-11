package org.university.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.dao.impl.LessonDaoImpl;
import org.university.dto.LessonDto;
import org.university.entity.Classroom;
import org.university.entity.Group;
import org.university.entity.Lesson;
import org.university.entity.Teacher;
import org.university.exceptions.ClassroomBusyException;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.exceptions.InvalidClassroomCapacityException;
import org.university.exceptions.InvalidLessonTimeException;
import org.university.exceptions.InvalidLinkException;
import org.university.service.mapper.LessonDtoMapper;
import org.university.service.validator.LessonValidator;
import org.university.utils.CreatorTestEntities;

class LessonServiceImplTest {

    private static LessonServiceImpl lessonService;
    private static LessonDaoImpl lessonDaoMock;
    private static Lesson lessonMock;
    private static LessonDtoMapper mapperMock;

    @BeforeAll
    static void init() {
        lessonDaoMock = createLessonDaoMock();
        mapperMock = mock(LessonDtoMapper.class);
        lessonService = new LessonServiceImpl(lessonDaoMock, new LessonValidator(), mapperMock);
        lessonMock = createLessonMock();
    }

    @Test
    void createLessonShouldReturnExpectedLessonWhenItExistsInDatabase() {
        Lesson lesson = CreatorTestEntities.createLessons().get(0);
        assertThat(lessonService.createLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00), "Bob@mail.ru", "AB-22")).isEqualTo(lesson);
    }

    @Test
    void createLessonShouldThrowEntityNotExistExceptionWhenLessonWithInputDataNotExist() {
        assertThatThrownBy(() -> lessonService.createLesson(LocalDateTime.of(2021, Month.OCTOBER, 30, 10, 00, 00), "notexistemail", "notexistgroup"))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void addLessonShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> lessonService.addLesson(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addLessonShouldThrowInvalidClassroomCapacityExceptionWhenInputLessonHasInvalidClassroomCapacityForGroup() {
        Lesson lessonMock = mock(Lesson.class);
        Group groupMock = mock(Group.class);
        Classroom classroomMock = mock(Classroom.class);
        List students = mock(List.class);
        when(lessonMock.getGroup()).thenReturn(groupMock);
        when(groupMock.getStudents()).thenReturn(students);
        when(students.size()).thenReturn(10);
        when(lessonMock.getClassroom()).thenReturn(classroomMock);
        when(classroomMock.getCapacity()).thenReturn(5);
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.addLesson(lessonDto))
                .isInstanceOf(InvalidClassroomCapacityException.class);
    }

    @Test
    void addLessonShouldThrowInvalidLessonTimeExceptionWhenInputLessonStartTimeIsSunday() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.MAY, 30, 10, 00, 00));
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.addLesson(lessonDto)).isInstanceOf(InvalidLessonTimeException.class);
    }

    @Test
    void addLessonShouldThrowInvalidLessonTimeExceptionWhenInputLessonStartTimeBeforeNineAM() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.MAY, 28, 8, 00, 00));
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.addLesson(lessonDto)).isInstanceOf(InvalidLessonTimeException.class);
    }

    @Test
    void addLessonShouldThrowInvalidLessonTimeExceptionWhenInputLessonStartTimeAfterSixPM() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.MAY, 28, 20, 00, 00));
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.addLesson(lessonDto)).isInstanceOf(InvalidLessonTimeException.class);
    }

    @Test
    void addLessonShouldThrowInvalidLessonTimeExceptionWhenInputLessonStartLaterLessonEnd() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.MAY, 28, 15, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.MAY, 28, 13, 00, 00));
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.addLesson(lessonDto)).isInstanceOf(InvalidLessonTimeException.class);
    }

    @Test
    void addLessonShouldThrowInvalidLessonTimeExceptionWhenInputLessonStartEqualLessonEnd() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.MAY, 28, 15, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.MAY, 28, 15, 00, 00));
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.addLesson(lessonDto)).isInstanceOf(InvalidLessonTimeException.class);
    }

    @Test
    void addLessonShouldThrowEntityAlreadyExistExceptionWhenInputLessonExist() {
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getId()).thenReturn(1);
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.addLesson(lessonDto)).isInstanceOf(EntityAlreadyExistException.class);
    }

    @Test
    void addLessonShouldThrowInvalidLessonTimeExceptionWhenTeacherBusyThisTime() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 15, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 16, 00, 00));
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.addLesson(lessonDto)).isInstanceOf(InvalidLessonTimeException.class);
    }

    @Test
    void addLessonShouldThrowInvalidLessonTimeExceptionWhenGroupBusyThisTime() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 12, 00, 00));
        when(lessonMock.getOnlineLesson()).thenReturn(false);
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        lessons.remove(2);
        lessons.remove(1);
        when(lessonDaoMock.findAllByDateAndGroup(LocalDate.of(2021, Month.OCTOBER, 19), 2)).thenReturn(lessons);
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.addLesson(lessonDto)).isInstanceOf(InvalidLessonTimeException.class);
    }
    
    @Test
    void addLessonShouldThrowInvalidLinkExceptionWhenInputLinkNull() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 18, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 19, 00, 00));
        when(lessonMock.getOnlineLesson()).thenReturn(true);
        when(lessonMock.getLessonLink()).thenReturn(null);
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.addLesson(lessonDto)).isInstanceOf(InvalidLinkException.class);
    }
    
    @Test
    void addLessonShouldThrowInvalidLinkExceptionWhenInputLinkInvalid() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 18, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 19, 00, 00));
        when(lessonMock.getOnlineLesson()).thenReturn(true);
        when(lessonMock.getLessonLink()).thenReturn("ddd");
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.addLesson(lessonDto)).isInstanceOf(InvalidLinkException.class);
    }

    @Test
    void addLessonShouldSaveLessonWhenLessonValidOrTeqcherAndGroupFree() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 18, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 19, 00, 00));
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        lessonService.addLesson(lessonDto);
        verify(lessonDaoMock).save(lessonMock);
    }

    @Test
    void addLessonShouldThrowInvalidLessonTimeExceptionWhenLessonAfterLastButNotBeforeNext() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 18, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 21, 00, 00));
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.addLesson(lessonDto)).isInstanceOf(InvalidLessonTimeException.class);
    }

    @Test
    void addLessonShouldSaveLessonWhenLessonValidAndBeforeOutherLessonOnDay() {
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 12, 00, 00));
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        lessonService.addLesson(lessonDto);
        verify(lessonDaoMock).save(lessonMock);
    }

    @Test
    void addLessonShouldSaveLessonWhenLessonValidAndAfterOutherLessonOnDay() {
        Lesson lessonMock = createLessonMock();
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        lessons.remove(2);
        lessons.remove(1);
        when(lessonDaoMock.findAllByDateAndTeacher(LocalDate.of(2021, Month.OCTOBER, 19), 2)).thenReturn(lessons);
        when(lessonDaoMock.findAllByDateAndGroup(LocalDate.of(2021, Month.OCTOBER, 19), 2)).thenReturn(lessons);
        when(lessonMock.getClassroom()).thenReturn(CreatorTestEntities.createClassrooms().get(0));
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        lessonService.addLesson(lessonDto);
        verify(lessonDaoMock).save(lessonMock);
    }

    @Test
    void addLessonShouldThrowClassroomBusyExceptionWhenForTeacherClassroomBusy() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 12, 00, 00));
        when(lessonMock.getClassroom()).thenReturn(CreatorTestEntities.createClassrooms().get(0));
        when(lessonMock.getOnlineLesson()).thenReturn(false);
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        lessons.remove(0);
        when(lessonDaoMock.findAllByDateAndTeacher(LocalDate.of(2021, Month.OCTOBER, 19), 2)).thenReturn(lessons);
        when(lessonDaoMock.findAllByDateAndGroup(LocalDate.of(2021, Month.OCTOBER, 19), 2)).thenReturn(lessons);
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.addLesson(lessonDto)).isInstanceOf(ClassroomBusyException.class);
    }

    @Test
    void deleteShouldDeleteLessonWhenLessonExists() {
        LessonDto lessonDtoMock = mock(LessonDto.class);
        when(lessonDtoMock.getId()).thenReturn(1);                
        lessonService.delete(lessonDtoMock);
        verify(lessonDaoMock).deleteById(1);
    }
    
    @Test 
    void deleteShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> lessonService.delete(null)).isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void editShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> lessonService.edit(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void editShouldThrowInvalidClassroomCapacityExceptionWhenInputLessonHasInvalidClassroomCapacityForGroup() {
        Lesson lessonMock = mock(Lesson.class);
        Group groupMock = mock(Group.class);
        Classroom classroomMock = mock(Classroom.class);
        List students = mock(List.class);
        when(lessonMock.getGroup()).thenReturn(groupMock);
        when(groupMock.getStudents()).thenReturn(students);
        when(students.size()).thenReturn(10);
        when(lessonMock.getClassroom()).thenReturn(classroomMock);
        when(classroomMock.getCapacity()).thenReturn(5);
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.edit(lessonDto))
                .isInstanceOf(InvalidClassroomCapacityException.class);
    }

    @Test
    void editShouldThrowInvalidLessonTimeExceptionWhenInputLessonStartTimeIsSunday() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.MAY, 30, 10, 00, 00));
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.edit(lessonDto)).isInstanceOf(InvalidLessonTimeException.class);
    }

    @Test
    void editShouldThrowInvalidLessonTimeExceptionWhenInputLessonStartTimeBeforeNineAM() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.MAY, 28, 8, 00, 00));
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.edit(lessonDto)).isInstanceOf(InvalidLessonTimeException.class);
    }

    @Test
    void editLessonShouldThrowInvalidLessonTimeExceptionWhenInputLessonStartTimeAfterSixPM() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.MAY, 28, 20, 00, 00));
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.edit(lessonDto)).isInstanceOf(InvalidLessonTimeException.class);
    }

    @Test
    void editShouldThrowInvalidLessonTimeExceptionWhenInputLessonStartLaterLessonEnd() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.MAY, 28, 15, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.MAY, 28, 13, 00, 00));
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.edit(lessonDto)).isInstanceOf(InvalidLessonTimeException.class);
    }

    @Test
    void editShouldThrowInvalidLessonTimeExceptionWhenInputLessonStartEqualLessonEnd() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.MAY, 28, 15, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.MAY, 28, 15, 00, 00));
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.edit(lessonDto)).isInstanceOf(InvalidLessonTimeException.class);
    }

    @Test
    void editShouldThrowInvalidLessonTimeExceptionWhenTeacherBusyThisTime() {
        LessonDto lessonDto = new LessonDto();
        lessonDto.setId(1);
        lessonDto.setStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00).toString());
        lessonDto.setEndLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 12, 00, 00).toString());
        lessonDto.setGroupName("FR-33");
        lessonDto.setTeacherEmail("Bob@mail.ru");
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 12, 00, 00));
        Group group = Group.builder()
                .withId(2)
                .withName("FR-33")
                .withStudents(CreatorTestEntities.createStudents())
                .build();
        when(lessonMock.getGroup()).thenReturn(group);
        when(lessonMock.getTeacher()).thenReturn(CreatorTestEntities.createTeachers().get(1)); 
        LessonDaoImpl lessonDaoMock = createLessonDaoMock();    
        when(lessonDaoMock.findAllByDateAndTeacher(LocalDate.of(2021, Month.OCTOBER, 19), 2)).thenReturn(CreatorTestEntities.createLessons());
        when(lessonDaoMock.findAllByDateAndGroup(LocalDate.of(2021, Month.OCTOBER, 19), 2)).thenReturn(new ArrayList<>());        
        when(lessonDaoMock.findById(1)).thenReturn(Optional.ofNullable(lessonMock));        
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        LessonServiceImpl lessonService = new LessonServiceImpl(lessonDaoMock, new LessonValidator(), mapperMock);
        assertThatThrownBy(() -> lessonService.edit(lessonDto)).isInstanceOf(InvalidLessonTimeException.class);
    }
    
    @Test
    void editShouldThrowInvalidLessonTimeExceptionWhenGroupBusyThisTime() {
        LessonDto lessonDto = new LessonDto();
        lessonDto.setId(1);
        lessonDto.setStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00).toString());
        lessonDto.setEndLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 12, 00, 00).toString());
        lessonDto.setGroupName("AB-22");
        lessonDto.setTeacherEmail("Ann@mail.ru");
        LessonDaoImpl lessonDaoMock = createLessonDaoMock(); 
        List<Lesson> lessons = CreatorTestEntities.createLessons();        
        when(lessonDaoMock.findAllByDateAndTeacher(LocalDate.of(2021, Month.OCTOBER, 19), 2)).thenReturn(new ArrayList<>());
        when(lessonDaoMock.findAllByDateAndGroup(LocalDate.of(2021, Month.OCTOBER, 19), 2)).thenReturn(lessons);             
        Group group = Group.builder()
                .withId(2)
                .withName("FR-33")
                .withStudents(CreatorTestEntities.createStudents())
                .build();
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getId()).thenReturn(1);
        when(lessonMock.getGroup()).thenReturn(group);
        when(lessonMock.getTeacher()).thenReturn(CreatorTestEntities.createTeachers().get(1));
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 12, 00, 00));
        when(lessonDaoMock.findById(1)).thenReturn(Optional.ofNullable(lessonMock));
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        LessonServiceImpl lessonService = new LessonServiceImpl(lessonDaoMock, new LessonValidator(), mapperMock);
        assertThatThrownBy(() -> lessonService.edit(lessonDto)).isInstanceOf(InvalidLessonTimeException.class);
    }
    
    @Test
    void editShouldEditLessonWhenNotChangeTimesTeacherGroupAndClassroom() {
        LessonDto lessonDto = new LessonDto();
        lessonDto.setId(2);
        lessonDto.setStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 13, 00, 00).toString());
        lessonDto.setEndLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 14, 00, 00).toString());
        lessonDto.setGroupName("FR-33");
        lessonDto.setTeacherEmail("Ann@mail.ru");
        LessonDaoImpl lessonDaoMock = createLessonDaoMock();
        Group group = Group.builder()
                .withId(2)
                .withName("FR-33")
                .withStudents(CreatorTestEntities.createStudents())
                .build();
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getGroup()).thenReturn(group);
        when(lessonMock.getTeacher()).thenReturn(CreatorTestEntities.createTeachers().get(1));
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 13, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 14, 00, 00));
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        List<Lesson> lessons = CreatorTestEntities.createLessons();        
        when(lessonDaoMock.findAllByDateAndTeacher(LocalDate.of(2021, Month.OCTOBER, 19), 2)).thenReturn(lessons);
        when(lessonDaoMock.findAllByDateAndGroup(LocalDate.of(2021, Month.OCTOBER, 19), 2)).thenReturn(lessons);        
        when(lessonDaoMock.findById(2)).thenReturn(Optional.ofNullable(lessonMock));
        LessonServiceImpl lessonService = new LessonServiceImpl(lessonDaoMock, new LessonValidator(), mapperMock);
        lessonService.edit(lessonDto);
        verify(lessonDaoMock).update(lessonMock);
    }
    
    @Test
    void editShouldThrowClassroomBusyExceptionWhenNotChangeTimesTeacherGroupButClassroomChange() {
        LessonDto lessonDto = new LessonDto();
        lessonDto.setId(2);
        lessonDto.setStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 13, 00, 00).toString());
        lessonDto.setEndLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 14, 00, 00).toString());
        lessonDto.setGroupName("FR-33");
        lessonDto.setTeacherEmail("Ann@mail.ru");
        lessonDto.setClassroomNumber(1);
        LessonDaoImpl lessonDaoMock = createLessonDaoMock();
        Group group = Group.builder()
                .withId(2)
                .withName("FR-33")
                .withStudents(CreatorTestEntities.createStudents())
                .build();
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getGroup()).thenReturn(group);
        when(lessonMock.getTeacher()).thenReturn(CreatorTestEntities.createTeachers().get(1));
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 13, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 14, 00, 00));
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        List<Lesson> lessons = CreatorTestEntities.createLessons();        
        when(lessonDaoMock.findAllByDateAndTeacher(LocalDate.of(2021, Month.OCTOBER, 19), 2)).thenReturn(lessons);
        when(lessonDaoMock.findAllByDateAndGroup(LocalDate.of(2021, Month.OCTOBER, 19), 2)).thenReturn(lessons);        
        when(lessonDaoMock.findById(2)).thenReturn(Optional.ofNullable(lessonMock));
        List<Lesson> classroomLessons = new ArrayList<>();
        classroomLessons.add(lessonMock);
        when(lessonDaoMock.findAllByDate(LocalDate.of(2021, Month.OCTOBER, 19))).thenReturn(classroomLessons);
        LessonServiceImpl lessonService = new LessonServiceImpl(lessonDaoMock, new LessonValidator(), mapperMock);
        assertThatThrownBy(() -> lessonService.edit(lessonDto)).isInstanceOf(ClassroomBusyException.class);
    }

    @Test
    void editShouldThrowInvalidLessonTimeExceptionWhenTimeChangesAndTeacherBusyThisTime(){
        LessonDto lessonDto = new LessonDto();
        lessonDto.setId(1);
        lessonDto.setStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 15, 00, 00).toString());
        lessonDto.setEndLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 17, 00, 00).toString());
        lessonDto.setGroupName("FR-33");
        lessonDto.setTeacherEmail("Bob@mail.ru");
        lessonDto.setClassroomNumber(1);
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 15, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 18, 00, 00));
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        LessonDaoImpl lessonDaoMock = createLessonDaoMock();
        when(lessonDaoMock.findAllByDateAndTeacher(LocalDate.of(2021, Month.OCTOBER, 19), 2)).thenReturn(lessons);
        when(lessonDaoMock.findAllByDateAndGroup(LocalDate.of(2021, Month.OCTOBER, 19), 2)).thenReturn(lessons);
        LessonServiceImpl lessonService = new LessonServiceImpl(lessonDaoMock, new LessonValidator(), mapperMock);
        assertThatThrownBy(() -> lessonService.edit(lessonDto)).isInstanceOf(InvalidLessonTimeException.class);
    }

    private static Lesson createLessonMock() {
        Lesson lessonMock = mock(Lesson.class);
        Group groupMock = mock(Group.class);
        Teacher teacherMock = mock(Teacher.class);
        List students = mock(List.class);
        when(lessonMock.getGroup()).thenReturn(groupMock);
        when(lessonMock.getTeacher()).thenReturn(teacherMock);
        when(lessonMock.getClassroom()).thenReturn(CreatorTestEntities.createClassrooms().get(1));
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 13, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 14, 00, 00));
        when(groupMock.getStudents()).thenReturn(students);
        when(groupMock.getId()).thenReturn(2);
        when(teacherMock.getId()).thenReturn(2);
        when(students.size()).thenReturn(2);
        return lessonMock;
    }

    private static LessonDaoImpl createLessonDaoMock() {
        LessonDaoImpl lessonDaoMock = mock(LessonDaoImpl.class);
        when(lessonDaoMock.findByTimeAndTeacherAndGroup(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00), "Bob@mail.ru", "AB-22"))
                .thenReturn(Optional.ofNullable(CreatorTestEntities.createLessons().get(0)));
        when(lessonDaoMock.findById(1)).thenReturn(Optional.ofNullable(CreatorTestEntities.createLessons().get(0)));
        when(lessonDaoMock.findById(10)).thenReturn(Optional.empty());
        when(lessonDaoMock.findAllByDate(LocalDate.of(2021, Month.OCTOBER, 19))).thenReturn(CreatorTestEntities.createLessons());
        return lessonDaoMock;
    }
}
