package org.university.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.university.dto.GroupDto;
import org.university.dto.UserDto;
import org.university.entity.DayTimetable;
import org.university.entity.Lesson;
import org.university.service.DayTimetableService;
import org.university.utils.CSVDataGenerator;
import org.university.utils.CreatorTestEntities;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class CSVControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DayTimetableService timetableServiceMock;

    private CSVController csvController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        csvController = new CSVController(timetableServiceMock, new CSVDataGenerator());
        mockMvc = MockMvcBuilders.standaloneSetup(csvController).build();
    }

    @Test
    void createWeekGroupTimetableCSV() throws Exception {
        GroupDto groupDto = new GroupDto();
        groupDto.setName("test");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        List<DayTimetable> timetables = new ArrayList<>();
        timetables.add(new DayTimetable(LocalDate.parse("2020-10-20"), lessons));
        when(timetableServiceMock.createWeekGroupTimetable(LocalDate.now(), "test")).thenReturn(timetables);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/csv/weekgroup/").flashAttr("group",
                groupDto);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.content().contentType("text/csv")).andExpect(MockMvcResultMatchers
                .header().string("Content-Disposition", "attachment; filename=test timetable.csv"));
        String fileContent = new String(result.andReturn().getResponse().getContentAsByteArray(), "UTF-8");
        String[] fileRows = fileContent.split("\n");
        assertThat(fileRows[0]).isEqualTo(
                "\"Date\",\"Start lesson\",\"End lesson\",\"Teacher\",\"Course\",\"Classroom\",\"Lesson link\"");
        assertThat(fileRows[1]).isEqualTo("\"2020-10-20\",\"10:00\",\"12:00\",\"Bob Moren\",\"Law\",\"1\",");
        assertThat(fileRows[2]).isEqualTo("\"\",\"15:00\",\"17:00\",\"Ann Moren\",\"Math\",\"2\",\"test-link\"");
        assertThat(fileRows[3]).isEqualTo("\"\",\"21:00\",\"22:00\",\"Ann Moren\",\"Math\",\"2\",\"test-link\"");
    }

    @Test
    void createMonthGroupTimetableCSV() throws Exception {
        GroupDto groupDto = new GroupDto();
        groupDto.setName("test");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        lessons.remove(2);
        List<DayTimetable> timetables = new ArrayList<>();
        timetables.add(new DayTimetable(LocalDate.parse("2020-10-20"), lessons));
        when(timetableServiceMock.createMonthGroupTimetable(LocalDate.now(), "test")).thenReturn(timetables);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/csv/monthgroup/").flashAttr("group",
                groupDto);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.content().contentType("text/csv")).andExpect(MockMvcResultMatchers
                .header().string("Content-Disposition", "attachment; filename=test timetable.csv"));
        String fileContent = new String(result.andReturn().getResponse().getContentAsByteArray(), "UTF-8");
        String[] fileRows = fileContent.split("\n");
        assertThat(fileRows[0]).isEqualTo(
                "\"Date\",\"Start lesson\",\"End lesson\",\"Teacher\",\"Course\",\"Classroom\",\"Lesson link\"");
        assertThat(fileRows[1]).isEqualTo("\"2020-10-20\",\"10:00\",\"12:00\",\"Bob Moren\",\"Law\",\"1\",");
        assertThat(fileRows[2]).isEqualTo("\"\",\"15:00\",\"17:00\",\"Ann Moren\",\"Math\",\"2\",\"test-link\"");
    }

    @Test
    void createWeekTeacherTimetableCSV() throws Exception {
        UserDto teacher = new UserDto();
        teacher.setName("test name");
        teacher.setEmail("test email");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        List<DayTimetable> timetables = new ArrayList<>();
        timetables.add(new DayTimetable(LocalDate.parse("2020-10-20"), lessons));
        when(timetableServiceMock.createWeekTeacherTimetable(LocalDate.now(), "test email")).thenReturn(timetables);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/csv/weekteacher/").flashAttr("teacher",
                teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.content().contentType("text/csv")).andExpect(MockMvcResultMatchers
                .header().string("Content-Disposition", "attachment; filename=test name timetable.csv"));
        String fileContent = new String(result.andReturn().getResponse().getContentAsByteArray(), "UTF-8");
        String[] fileRows = fileContent.split("\n");
        assertThat(fileRows[0]).isEqualTo(
                "\"Date\",\"Start lesson\",\"End lesson\",\"Group\",\"Course\",\"Classroom\",\"Lesson link\"");
        assertThat(fileRows[1]).isEqualTo("\"2020-10-20\",\"10:00\",\"12:00\",\"AB-22\",\"Law\",\"1\",");
        assertThat(fileRows[2]).isEqualTo("\"\",\"15:00\",\"17:00\",\"FR-33\",\"Math\",\"2\",\"test-link\"");
        assertThat(fileRows[3]).isEqualTo("\"\",\"21:00\",\"22:00\",\"FR-33\",\"Math\",\"2\",\"test-link\"");
    }

    @Test
    void createMonthTeacherTimetableCSV() throws Exception {
        UserDto teacher = new UserDto();
        teacher.setName("test name");
        teacher.setEmail("test email");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        lessons.remove(2);
        List<DayTimetable> timetables = new ArrayList<>();
        timetables.add(new DayTimetable(LocalDate.parse("2020-10-20"), lessons));
        when(timetableServiceMock.createMonthTeacherTimetable(LocalDate.now(), "test email")).thenReturn(timetables);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/csv/monthteacher/").flashAttr("teacher",
                teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.content().contentType("text/csv")).andExpect(MockMvcResultMatchers
                .header().string("Content-Disposition", "attachment; filename=test name timetable.csv"));
        String fileContent = new String(result.andReturn().getResponse().getContentAsByteArray(), "UTF-8");
        String[] fileRows = fileContent.split("\n");
        assertThat(fileRows[0]).isEqualTo(
                "\"Date\",\"Start lesson\",\"End lesson\",\"Group\",\"Course\",\"Classroom\",\"Lesson link\"");
        assertThat(fileRows[1]).isEqualTo("\"2020-10-20\",\"10:00\",\"12:00\",\"AB-22\",\"Law\",\"1\",");
        assertThat(fileRows[2]).isEqualTo("\"\",\"15:00\",\"17:00\",\"FR-33\",\"Math\",\"2\",\"test-link\"");
    }

    @Test
    void createWeekGroupTimetableCSVWhenIOException() throws Exception {
        CSVDataGenerator csvDataGeneratorMock = mock(CSVDataGenerator.class);
        CSVController csvController = new CSVController(timetableServiceMock, csvDataGeneratorMock);
        mockMvc = MockMvcBuilders.standaloneSetup(csvController).build();
        Logger csvControllerLogger = (Logger) LoggerFactory.getLogger(CSVController.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        csvControllerLogger.addAppender(listAppender);
        GroupDto groupDto = new GroupDto();
        groupDto.setName("test");
        List<DayTimetable> timetables = new ArrayList<>();
        when(timetableServiceMock.createWeekGroupTimetable(LocalDate.now(), "test")).thenReturn(timetables);
        when(csvDataGeneratorMock.generateGroupsData(timetables)).thenThrow(IOException.class);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/csv/weekgroup/").flashAttr("group",
                groupDto);
        mockMvc.perform(request);
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals("File creation failed!", logsList.get(0).getMessage());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
    }

    @Test
    void createWeekTeacherTimetableCSVWhenIOException() throws Exception {
        CSVDataGenerator csvDataGeneratorMock = mock(CSVDataGenerator.class);
        CSVController csvController = new CSVController(timetableServiceMock, csvDataGeneratorMock);
        mockMvc = MockMvcBuilders.standaloneSetup(csvController).build();
        Logger csvControllerLogger = (Logger) LoggerFactory.getLogger(CSVController.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        csvControllerLogger.addAppender(listAppender);
        UserDto teacher = new UserDto();
        teacher.setName("test name");
        teacher.setEmail("test email");
        List<DayTimetable> timetables = new ArrayList<>();
        when(timetableServiceMock.createWeekTeacherTimetable(LocalDate.now(), "test email")).thenReturn(timetables);
        when(csvDataGeneratorMock.generateTeachersData(timetables)).thenThrow(IOException.class);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/csv/weekteacher/").flashAttr("teacher",
                teacher);
        mockMvc.perform(request);
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals("File creation failed!", logsList.get(0).getMessage());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
    }
}
