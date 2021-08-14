package org.university.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
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
import org.university.dto.GroupDto;
import org.university.dto.LessonDto;
import org.university.dto.TeacherDto;
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
        when(timetableServiceMock.createDayTimetable(LocalDate.now())).thenReturn(timetable);
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
        when(timetableServiceMock.createDayTimetable(LocalDate.of(2020, Month.OCTOBER, 20))).thenReturn(timetable);
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
        when(timetableServiceMock.createGroupTimetable(LocalDate.of(2020, Month.OCTOBER, 20), "Test")).thenReturn(timetable);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/timetables/group/")
                .flashAttr("timetable", timetableDto);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("lessons"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("lessons"))
                .andExpect(MockMvcResultMatchers.model().attribute("lessons", lessons));
    }
    
    @Test
    void testCreateWeekGroupTimetable() throws Exception {
        GroupDto groupDto = new GroupDto();
        groupDto.setName("test");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        List<DayTimetable> timetables = new ArrayList<>();
        timetables.add(new DayTimetable(LocalDate.parse("2020-10-20"), lessons));
        when(timetableServiceMock.createWeekGroupTimetable(LocalDate.now(), "test")).thenReturn(timetables);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/timetables/weekgroup/")
                .flashAttr("group", groupDto);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("grouptimetable"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("timetables"))
                .andExpect(MockMvcResultMatchers.model().attribute("timetables", timetables))
                .andExpect(MockMvcResultMatchers.model().attributeExists("group"))
                .andExpect(MockMvcResultMatchers.model().attribute("group", groupDto));
    }
    
    @Test
    void testCreateMonthGroupTimetable() throws Exception {
        GroupDto groupDto = new GroupDto();
        groupDto.setName("test");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        List<DayTimetable> timetables = new ArrayList<>();
        timetables.add(new DayTimetable(LocalDate.parse("2020-10-20"), lessons));
        when(timetableServiceMock.createMonthGroupTimetable(LocalDate.now(), "test")).thenReturn(timetables);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/timetables/monthgroup/")
                .flashAttr("group", groupDto);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("grouptimetable"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("timetables"))
                .andExpect(MockMvcResultMatchers.model().attribute("timetables", timetables))
                .andExpect(MockMvcResultMatchers.model().attributeExists("group"))
                .andExpect(MockMvcResultMatchers.model().attribute("group", groupDto));
    }

    @Test
    void testCreateTeacherTimetable() throws Exception {
        DayTimetableDto timetableDto = new DayTimetableDto();
        timetableDto.setDay("2020-10-20");
        timetableDto.setTeacherEmail("Test");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        DayTimetable timetable = new DayTimetable(LocalDate.parse("2020-10-20"), lessons);
        when(timetableServiceMock.createTeacherTimetable(LocalDate.of(2020, Month.OCTOBER, 20), "Test")).thenReturn(timetable);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/timetables/teacher/")
                .flashAttr("timetable", timetableDto);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("lessons"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("lessons"))
                .andExpect(MockMvcResultMatchers.model().attribute("lessons", lessons));
    }
    
    @Test
    void testCreateWeekTeacherTimetable() throws Exception {
        TeacherDto teacher = new TeacherDto();
        teacher.setEmail("test");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        List<DayTimetable> timetables = new ArrayList<>();
        timetables.add(new DayTimetable(LocalDate.parse("2020-10-20"), lessons));
        when(timetableServiceMock.createWeekTeacherTimetable(LocalDate.now(), "test")).thenReturn(timetables);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/timetables/weekteacher/")
                .flashAttr("teacher", teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("teachertimetable"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("timetables"))
                .andExpect(MockMvcResultMatchers.model().attribute("timetables", timetables))
                .andExpect(MockMvcResultMatchers.model().attributeExists("teacher"))
                .andExpect(MockMvcResultMatchers.model().attribute("teacher", teacher));
    }
    
    @Test
    void testCreateMonthTeacherTimetable() throws Exception {
        TeacherDto teacher = new TeacherDto();
        teacher.setEmail("test");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        List<DayTimetable> timetables = new ArrayList<>();
        timetables.add(new DayTimetable(LocalDate.parse("2020-10-20"), lessons));
        when(timetableServiceMock.createMonthTeacherTimetable(LocalDate.now(), "test")).thenReturn(timetables);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/timetables/monthteacher/")
                .flashAttr("teacher", teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("teachertimetable"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("timetables"))
                .andExpect(MockMvcResultMatchers.model().attribute("timetables", timetables))
                .andExpect(MockMvcResultMatchers.model().attributeExists("teacher"))
                .andExpect(MockMvcResultMatchers.model().attribute("teacher", teacher));
    }

    @Test
    void testAddLesson() throws Exception {
        LessonDto lesson = new LessonDto();
        lesson.setStartLesson("2010-10-10T10:00");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        DayTimetable timetable = new DayTimetable(LocalDate.parse("2010-10-10"), lessons);
        when(timetableServiceMock.createDayTimetable(LocalDate.of(2010, Month.OCTOBER, 10))).thenReturn(timetable);
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
        when(timetableServiceMock.createDayTimetable(LocalDate.of(2010, Month.OCTOBER, 10))).thenReturn(timetable);
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
    
    @Test
    void testGetEditForm() throws Exception {
        List<Group> groups = CreatorTestEntities.createGroups();
        LessonDto lesson = new LessonDto();
        List<Teacher> teachers = CreatorTestEntities.createTeachers();
        List<Course> courses = CreatorTestEntities.createCourses();
        List<Classroom> classrooms = CreatorTestEntities.createClassrooms();
        when(groupServiceMock.findAllGroups()).thenReturn(groups);
        when(teacherServiceMock.findAll()).thenReturn(teachers);
        when(courseServiceMock.findAllCourses()).thenReturn(courses);
        when(classroomServiceMock.findAllClassrooms()).thenReturn(classrooms);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/timetables/edit/")
                .flashAttr("lesson", lesson);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/lesson"))
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
    
    @Test
    void testEdit() throws Exception {
        LessonDto lesson = new LessonDto();
        lesson.setStartLesson("2010-10-10T10:00");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        DayTimetable timetable = new DayTimetable(LocalDate.parse("2010-10-10"), lessons);
        when(timetableServiceMock.createDayTimetable(LocalDate.of(2010, Month.OCTOBER, 10))).thenReturn(timetable);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/timetables/")
                .flashAttr("lesson", lesson);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("lessons"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("lessons"))
                .andExpect(MockMvcResultMatchers.model().attribute("lessons", lessons));;
        verify(lessonServiceMock).edit(lesson);
    }
    
    @Test
    void testEditWhenInputInvalidTime() throws Exception {
        LessonDto lesson = new LessonDto();        
        doThrow(new InvalidLessonTimeException("Message about invalid time!")).when(lessonServiceMock).edit(lesson);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/timetables/")
                .flashAttr("lesson", lesson);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/timetables/edit"))
                .andExpect(MockMvcResultMatchers.model().attribute("message", "Message about invalid time!"));
    }
    
    @Test
    void testEditWhenInputGroupMoreThanInputClassroomCapacity() throws Exception {
        LessonDto lesson = new LessonDto();        
        doThrow(new InvalidClassroomCapacityException("Message about invalid classroom capacity!")).when(lessonServiceMock).edit(lesson);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/timetables/")
                .flashAttr("lesson", lesson);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/timetables/edit"))
        .andExpect(MockMvcResultMatchers.model().attribute("message", "Message about invalid classroom capacity!"));
    }
    
    @Test
    void testEditWhenInputClassroomBusy() throws Exception {
        LessonDto lesson = new LessonDto();        
        doThrow(new ClassroomBusyException("Message about classroom is busy!")).when(lessonServiceMock).edit(lesson);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/timetables/")
                .flashAttr("lesson", lesson);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/timetables/edit"))
        .andExpect(MockMvcResultMatchers.model().attribute("message", "Message about classroom is busy!"));
    }
    
    @Test
    void testEditWhenInputInvalidLink() throws Exception {
        LessonDto lesson = new LessonDto();        
        doThrow(new InvalidLinkException("Message about invalid link!")).when(lessonServiceMock).edit(lesson);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/timetables/")
                .flashAttr("lesson", lesson);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/timetables/edit"))
        .andExpect(MockMvcResultMatchers.model().attribute("message", "Message about invalid link!"));
    }
}
