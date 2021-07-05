package org.university.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.university.dto.DayTimetableDto;
import org.university.dto.LessonDto;
import org.university.entity.Classroom;
import org.university.entity.Course;
import org.university.entity.DayTimetable;
import org.university.entity.Group;
import org.university.entity.Lesson;
import org.university.entity.Teacher;
import org.university.exceptions.ClassroomBusyException;
import org.university.exceptions.InvalidClassroomCapacityException;
import org.university.exceptions.InvalidLessonTimeException;
import org.university.exceptions.InvalidLinkException;
import org.university.service.ClassroomService;
import org.university.service.CourseService;
import org.university.service.DayTimetableService;
import org.university.service.GroupService;
import org.university.service.LessonService;
import org.university.service.TeacherService;
import org.university.utils.CreatorTestEntities;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class TimetableControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GroupService groupServiceMock;

    @Mock
    private CourseService courseServiceMock;

    @Mock
    private ClassroomService classroomServiceMock;

    @Mock
    private TeacherService teacherServiceMock;

    @Mock
    private LessonService lessonServiceMock;

    @Mock
    private DayTimetableService timetableServiceMock;

    private TimetableController timetableController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        timetableController = new TimetableController(timetableServiceMock, groupServiceMock, teacherServiceMock,
                courseServiceMock, classroomServiceMock, lessonServiceMock);
        mockMvc = MockMvcBuilders.standaloneSetup(timetableController).build();
    }

    @Test
    void testGetTimetable() throws Exception {
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        DayTimetable timetable = new DayTimetable(LocalDate.now(), lessons);
        when(timetableServiceMock.createDayTimetable(LocalDate.now().toString())).thenReturn(timetable);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/timetables/");
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("lessons"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("lessons"))
                .andExpect(MockMvcResultMatchers.model().attribute("lessons", lessons));
    }

    @Test
    void testGetTimetableOnDay() throws Exception {
        DayTimetableDto timetableDto = new DayTimetableDto();
        timetableDto.setDay("2020-10-20");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        DayTimetable timetable = new DayTimetable(LocalDate.parse("2020-10-20"), lessons);
        when(timetableServiceMock.createDayTimetable("2020-10-20")).thenReturn(timetable);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/timetables/date/")
                .flashAttr("timetable", timetableDto);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("lessons"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("lessons"))
                .andExpect(MockMvcResultMatchers.model().attribute("lessons", lessons));
    }

    @Test
    void testCreateGroupTimetable() throws Exception {
        DayTimetableDto timetableDto = new DayTimetableDto();
        timetableDto.setDay("2020-10-20");
        timetableDto.setGroupName("Test");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        DayTimetable timetable = new DayTimetable(LocalDate.parse("2020-10-20"), lessons);
        when(timetableServiceMock.createGroupTimetable("2020-10-20", "Test")).thenReturn(timetable);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/timetables/group/")
                .flashAttr("timetable", timetableDto);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("lessons"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("lessons"))
                .andExpect(MockMvcResultMatchers.model().attribute("lessons", lessons));
    }

    @Test
    void testCreateTeacherTimetable() throws Exception {
        DayTimetableDto timetableDto = new DayTimetableDto();
        timetableDto.setDay("2020-10-20");
        timetableDto.setTeacherEmail("Test");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        DayTimetable timetable = new DayTimetable(LocalDate.parse("2020-10-20"), lessons);
        when(timetableServiceMock.createTeacherTimetable("2020-10-20", "Test")).thenReturn(timetable);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/timetables/teacher/")
                .flashAttr("timetable", timetableDto);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("lessons"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("lessons"))
                .andExpect(MockMvcResultMatchers.model().attribute("lessons", lessons));
    }

    @Test
    void testAddLesson() throws Exception {
        LessonDto lesson = new LessonDto();
        lesson.setStartLesson("2010-10-10T10:00");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        DayTimetable timetable = new DayTimetable(LocalDate.parse("2010-10-10"), lessons);
        when(timetableServiceMock.createDayTimetable("2010-10-10")).thenReturn(timetable);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/timetables/")
                .flashAttr("lesson", lesson);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("lessons"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("lessons"))
                .andExpect(MockMvcResultMatchers.model().attribute("lessons", lessons));
        verify(lessonServiceMock).addLesson(lesson);
    }
    
    @Test
    void testAddLessonWhenInputInvalidTime() throws Exception {
        LessonDto lesson = new LessonDto();        
        doThrow(new InvalidLessonTimeException("Message about invalid time!")).when(lessonServiceMock).addLesson(lesson);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/timetables/")
                .flashAttr("lesson", lesson);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/timetables/new"))
                .andExpect(MockMvcResultMatchers.model().attribute("message", "Message about invalid time!"));
    }
    
    @Test
    void testAddLessonWhenInputGroupMoreThanInputClassroomCapacity() throws Exception {
        LessonDto lesson = new LessonDto();        
        doThrow(new InvalidClassroomCapacityException("Message about invalid classroom capacity!")).when(lessonServiceMock).addLesson(lesson);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/timetables/")
                .flashAttr("lesson", lesson);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/timetables/new"))
        .andExpect(MockMvcResultMatchers.model().attribute("message", "Message about invalid classroom capacity!"));
    }
    
    @Test
    void testAddLessonWhenInputClassroomBusy() throws Exception {
        LessonDto lesson = new LessonDto();        
        doThrow(new ClassroomBusyException("Message about classroom is busy!")).when(lessonServiceMock).addLesson(lesson);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/timetables/")
                .flashAttr("lesson", lesson);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/timetables/new"))
        .andExpect(MockMvcResultMatchers.model().attribute("message", "Message about classroom is busy!"));
    }
    
    @Test
    void testAddLessonWhenInputInvalidLink() throws Exception {
        LessonDto lesson = new LessonDto();        
        doThrow(new InvalidLinkException("Message about invalid link!")).when(lessonServiceMock).addLesson(lesson);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/timetables/")
                .flashAttr("lesson", lesson);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/timetables/new"))
        .andExpect(MockMvcResultMatchers.model().attribute("message", "Message about invalid link!"));
    }

    @Test
    void testDeleteLesson() throws Exception {
        LessonDto lesson = new LessonDto();
        lesson.setStartLesson("2010-10-10T10:00");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        DayTimetable timetable = new DayTimetable(LocalDate.parse("2010-10-10"), lessons);
        when(timetableServiceMock.createDayTimetable("2010-10-10")).thenReturn(timetable);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/timetables/")
                .flashAttr("lesson", lesson);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("lessons"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("lessons"))
                .andExpect(MockMvcResultMatchers.model().attribute("lessons", lessons));
        verify(lessonServiceMock).delete(lesson);
    }

    @Test
    void testNewLesson() throws Exception {
        List<Group> groups = CreatorTestEntities.createGroups();
        LessonDto lesson = new LessonDto();
        List<Teacher> teachers = CreatorTestEntities.createTeachers();
        List<Course> courses = CreatorTestEntities.createCourses();
        List<Classroom> classrooms = CreatorTestEntities.createClassrooms();
        when(groupServiceMock.findAllGroups()).thenReturn(groups);
        when(teacherServiceMock.findAll()).thenReturn(teachers);
        when(courseServiceMock.findAllCourses()).thenReturn(courses);
        when(classroomServiceMock.findAllClassrooms()).thenReturn(classrooms);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/timetables/new/");
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("lessonform"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("groups"))
                .andExpect(MockMvcResultMatchers.model().attribute("groups", groups))
                .andExpect(MockMvcResultMatchers.model().attributeExists("lesson"))
                .andExpect(MockMvcResultMatchers.model().attribute("lesson", lesson))
                .andExpect(MockMvcResultMatchers.model().attributeExists("teachers"))
                .andExpect(MockMvcResultMatchers.model().attribute("teachers", teachers))
                .andExpect(MockMvcResultMatchers.model().attributeExists("courses"))
                .andExpect(MockMvcResultMatchers.model().attribute("courses", courses))
                .andExpect(MockMvcResultMatchers.model().attributeExists("classrooms"))
                .andExpect(MockMvcResultMatchers.model().attribute("classrooms", classrooms));
    }

}
