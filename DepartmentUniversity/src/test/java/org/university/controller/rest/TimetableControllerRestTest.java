package org.university.controller.rest;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.university.dto.DayTimetableDto;
import org.university.dto.LessonDto;
import org.university.entity.DayTimetable;
import org.university.entity.Lesson;
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

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class TimetableControllerRestTest {

    private MockMvc mockMvc;
    
    private ObjectMapper mapper;

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

    private TimetableControllerRest timetableController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        mapper = new ObjectMapper();
        timetableController = new TimetableControllerRest(timetableServiceMock, lessonServiceMock);
        mockMvc = MockMvcBuilders.standaloneSetup(timetableController).build();
    }

    @Test
    void testGetTimetable() throws Exception {        
        List<Lesson> lessons = CreatorTestEntities.createLessons();        
        DayTimetable timetable = new DayTimetable(LocalDate.now(), lessons);
        when(timetableServiceMock.createDayTimetable(LocalDate.now())).thenReturn(timetable);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/timetables")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.lessons[0].id", is(1)))
                .andExpect(jsonPath("$.lessons[1].id", is(2)))
                .andExpect(jsonPath("$.lessons[2].id", is(3)));
    }

    @Test
    void testGetTimetableOnDay() throws Exception {
        DayTimetableDto timetableDto = new DayTimetableDto();
        timetableDto.setDay("2020-10-20");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        DayTimetable timetable = new DayTimetable(LocalDate.parse("2020-10-20"), lessons);
        when(timetableServiceMock.createDayTimetable(LocalDate.of(2020, Month.OCTOBER, 20))).thenReturn(timetable);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/timetables/date")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(timetableDto));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.lessons[0].id", is(1)))
                .andExpect(jsonPath("$.lessons[1].id", is(2)))
                .andExpect(jsonPath("$.lessons[2].id", is(3)));
    }

    @Test
    void testGetTimetableOnDayWhenInputDayEmpty() throws Exception {
        DayTimetableDto timetableDto = new DayTimetableDto();
        timetableDto.setDay("");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/timetables/date")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(timetableDto));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(
                exception.getResolvedException().getMessage(), "400 BAD_REQUEST \"Date can not be empty!\""))                
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateGroupTimetable() throws Exception {
        DayTimetableDto timetableDto = new DayTimetableDto();
        timetableDto.setDay("2020-10-20");
        timetableDto.setGroupName("Test");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        DayTimetable timetable = new DayTimetable(LocalDate.parse("2020-10-20"), lessons);
        when(timetableServiceMock.createGroupTimetable(LocalDate.of(2020, Month.OCTOBER, 20), "Test"))
                .thenReturn(timetable);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/timetables/group")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(timetableDto));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.lessons[0].id", is(1)))
                .andExpect(jsonPath("$.lessons[1].id", is(2)))
                .andExpect(jsonPath("$.lessons[2].id", is(3)));
    }

    @Test
    void testCreateGroupTimetableWhenInputDayEmpty() throws Exception {
        DayTimetableDto timetableDto = new DayTimetableDto();
        timetableDto.setDay("");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/timetables/group")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(timetableDto));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(
                exception.getResolvedException().getMessage(), "400 BAD_REQUEST \"Date can not be empty!\""))                
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateWeekGroupTimetable() throws Exception {
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        List<DayTimetable> timetables = new ArrayList<>();
        timetables.add(new DayTimetable(LocalDate.parse("2020-10-20"), lessons));
        when(timetableServiceMock.createWeekGroupTimetable(LocalDate.now(), "test")).thenReturn(timetables);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/timetables/weekgroup/test")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].lessons[0].id", is(1)))
                .andExpect(jsonPath("$[0].lessons[1].id", is(2)))
                .andExpect(jsonPath("$[0].lessons[2].id", is(3)));
    }

    @Test
    void testCreateMonthGroupTimetable() throws Exception {
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        List<DayTimetable> timetables = new ArrayList<>();
        timetables.add(new DayTimetable(LocalDate.parse("2020-10-20"), lessons));
        when(timetableServiceMock.createMonthGroupTimetable(LocalDate.now(), "test")).thenReturn(timetables);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/timetables/monthgroup/test")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].lessons[0].id", is(1)))
                .andExpect(jsonPath("$[0].lessons[1].id", is(2)))
                .andExpect(jsonPath("$[0].lessons[2].id", is(3)));
    }

    @Test
    void testCreateTeacherTimetable() throws Exception {
        DayTimetableDto timetableDto = new DayTimetableDto();
        timetableDto.setDay("2020-10-20");
        timetableDto.setTeacherEmail("Test");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        DayTimetable timetable = new DayTimetable(LocalDate.parse("2020-10-20"), lessons);
        when(timetableServiceMock.createTeacherTimetable(LocalDate.of(2020, Month.OCTOBER, 20), "Test"))
                .thenReturn(timetable);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/timetables/teacher")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(timetableDto));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.lessons[0].id", is(1)))
                .andExpect(jsonPath("$.lessons[1].id", is(2)))
                .andExpect(jsonPath("$.lessons[2].id", is(3)));
    }

    @Test
    void testCreateTeacherTimetableWhenInputDayEmpty() throws Exception {
        DayTimetableDto timetableDto = new DayTimetableDto();
        timetableDto.setDay("");
        timetableDto.setTeacherEmail("Test");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/timetables/teacher")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(timetableDto));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(
                exception.getResolvedException().getMessage(), "400 BAD_REQUEST \"Date can not be empty!\""))                
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateWeekTeacherTimetable() throws Exception {
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        List<DayTimetable> timetables = new ArrayList<>();
        timetables.add(new DayTimetable(LocalDate.parse("2020-10-20"), lessons));
        when(timetableServiceMock.createWeekTeacherTimetable(LocalDate.now(), "test")).thenReturn(timetables);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/timetables/weekteacher/test")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].lessons[0].id", is(1)))
                .andExpect(jsonPath("$[0].lessons[1].id", is(2)))
                .andExpect(jsonPath("$[0].lessons[2].id", is(3)));
    }

    @Test
    void testCreateMonthTeacherTimetable() throws Exception {
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        List<DayTimetable> timetables = new ArrayList<>();
        timetables.add(new DayTimetable(LocalDate.parse("2020-10-20"), lessons));
        when(timetableServiceMock.createMonthTeacherTimetable(LocalDate.now(), "test")).thenReturn(timetables);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/timetables/monthteacher/test")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].lessons[0].id", is(1)))
                .andExpect(jsonPath("$[0].lessons[1].id", is(2)))
                .andExpect(jsonPath("$[0].lessons[2].id", is(3)));
    }

    @Test
    void testAddLesson() throws Exception {
        LessonDto lesson = new LessonDto();
        lesson.setStartLesson("2010-10-10T10:00");
        lesson.setEndLesson("2010-10-10T12:00");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/timetables")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(lesson));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isCreated());
        verify(lessonServiceMock).addLesson(lesson);
    }

    @Test
    void testAddLessonWhenInputInvalidTime() throws Exception {
        LessonDto lesson = new LessonDto();
        lesson.setStartLesson("2010-10-10T19:00");
        lesson.setEndLesson("2010-10-10T21:00");
        doThrow(new InvalidLessonTimeException("Message about invalid time!")).when(lessonServiceMock)
                .addLesson(lesson);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/timetables")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(lesson));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(
                exception.getResolvedException().getMessage(), "400 BAD_REQUEST \"Message about invalid time!\""))                
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddLessonWhenInputDateEmpty() throws Exception {
        LessonDto lesson = new LessonDto();
        lesson.setStartLesson("");
        lesson.setEndLesson("");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/timetables")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(lesson));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(MethodArgumentNotValidException.class,
                exception.getResolvedException().getClass()))
                .andExpect(exception -> assertTrue(
                        exception.getResolvedException().getMessage().contains("must not be empty")))
                .andExpect(exception -> assertTrue(exception.getResolvedException().getMessage().contains("startLesson")))
                .andExpect(exception -> assertTrue(exception.getResolvedException().getMessage().contains("endLesson")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddLessonWhenInputGroupMoreThanInputClassroomCapacity() throws Exception {
        LessonDto lesson = new LessonDto();
        lesson.setStartLesson("2010-10-10T10:00");
        lesson.setEndLesson("2010-10-10T12:00");
        doThrow(new InvalidClassroomCapacityException("Message about invalid classroom capacity!"))
                .when(lessonServiceMock).addLesson(lesson);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/timetables")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(lesson));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(
                exception.getResolvedException().getMessage(), "400 BAD_REQUEST \"Message about invalid classroom capacity!\""))                
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddLessonWhenInputClassroomBusy() throws Exception {
        LessonDto lesson = new LessonDto();
        lesson.setStartLesson("2010-10-10T10:00");
        lesson.setEndLesson("2010-10-10T12:00");
        doThrow(new ClassroomBusyException("Message about classroom is busy!")).when(lessonServiceMock)
                .addLesson(lesson);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/timetables")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(lesson));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(
                exception.getResolvedException().getMessage(), "400 BAD_REQUEST \"Message about classroom is busy!\""))                
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddLessonWhenInputInvalidLink() throws Exception {
        LessonDto lesson = new LessonDto();
        lesson.setStartLesson("2010-10-10T10:00");
        lesson.setEndLesson("2010-10-10T12:00");
        doThrow(new InvalidLinkException("Message about invalid link!")).when(lessonServiceMock).addLesson(lesson);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/timetables")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(lesson));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(
                exception.getResolvedException().getMessage(), "400 BAD_REQUEST \"Message about invalid link!\""))                
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteLesson() throws Exception {
        LessonDto lesson = new LessonDto();
        lesson.setStartLesson("2010-10-10T10:00");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/api/v1/timetables")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(lesson));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk());
        verify(lessonServiceMock).delete(lesson);
    }

    @Test
    void testEdit() throws Exception {
        LessonDto lesson = new LessonDto();
        lesson.setStartLesson("2010-10-10T10:00");
        lesson.setEndLesson("2010-10-10T12:00");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/api/v1/timetables")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(lesson));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk());
        verify(lessonServiceMock).edit(lesson);
    }

    @Test
    void testEditWhenInputDateEmpty() throws Exception {
        LessonDto lesson = new LessonDto();
        lesson.setStartLesson("");
        lesson.setEndLesson("");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/api/v1/timetables")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(lesson));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(MethodArgumentNotValidException.class,
                exception.getResolvedException().getClass()))
                .andExpect(exception -> assertTrue(
                        exception.getResolvedException().getMessage().contains("must not be empty")))
                .andExpect(exception -> assertTrue(exception.getResolvedException().getMessage().contains("startLesson")))
                .andExpect(exception -> assertTrue(exception.getResolvedException().getMessage().contains("endLesson")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEditWhenInputInvalidTime() throws Exception {
        LessonDto lesson = new LessonDto();
        lesson.setStartLesson("2010-10-10T19:00");
        lesson.setEndLesson("2010-10-10T21:00");
        doThrow(new InvalidLessonTimeException("Message about invalid time!")).when(lessonServiceMock)
                .edit(lesson);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/api/v1/timetables")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(lesson));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(
                exception.getResolvedException().getMessage(), "400 BAD_REQUEST \"Message about invalid time!\""))                
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEditWhenInputGroupMoreThanInputClassroomCapacity() throws Exception {
        LessonDto lesson = new LessonDto();
        lesson.setStartLesson("2010-10-10T10:00");
        lesson.setEndLesson("2010-10-10T12:00");
        doThrow(new InvalidClassroomCapacityException("Message about invalid classroom capacity!"))
                .when(lessonServiceMock).edit(lesson);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/api/v1/timetables")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(lesson));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(
                exception.getResolvedException().getMessage(), "400 BAD_REQUEST \"Message about invalid classroom capacity!\""))                
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEditWhenInputClassroomBusy() throws Exception {
        LessonDto lesson = new LessonDto();
        lesson.setStartLesson("2010-10-10T10:00");
        lesson.setEndLesson("2010-10-10T12:00");
        doThrow(new ClassroomBusyException("Message about classroom is busy!")).when(lessonServiceMock)
                .edit(lesson);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/api/v1/timetables")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(lesson));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(
                exception.getResolvedException().getMessage(), "400 BAD_REQUEST \"Message about classroom is busy!\""))                
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEditWhenInputInvalidLink() throws Exception {
        LessonDto lesson = new LessonDto();
        lesson.setStartLesson("2010-10-10T10:00");
        lesson.setEndLesson("2010-10-10T12:00");
        doThrow(new InvalidLinkException("Message about invalid link!")).when(lessonServiceMock).edit(lesson);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/api/v1/timetables")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(lesson));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(
                exception.getResolvedException().getMessage(), "400 BAD_REQUEST \"Message about invalid link!\""))                
                .andExpect(status().isBadRequest());
    }
}
