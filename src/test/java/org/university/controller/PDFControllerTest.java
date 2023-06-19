package org.university.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.willAnswer;
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
import org.mockito.Mockito;
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
import org.university.utils.CreatorTestEntities;
import org.university.utils.PDFDataGenerator;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class PDFControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DayTimetableService timetableServiceMock;

    private PDFController pdfController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        pdfController = new PDFController(timetableServiceMock, new PDFDataGenerator());
        mockMvc = MockMvcBuilders.standaloneSetup(pdfController).build();
    }

    @Test
    void testCreateWeekGroupTimetablePDF() throws Exception {
        GroupDto groupDto = new GroupDto();
        groupDto.setName("test group week name");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        List<DayTimetable> timetables = new ArrayList<>();
        timetables.add(new DayTimetable(LocalDate.parse("2020-10-20"), lessons));
        when(timetableServiceMock.createWeekGroupTimetable(LocalDate.now(), "test group week name"))
                .thenReturn(timetables);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/pdf/weekgroup/").flashAttr("group",
                groupDto);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.content().contentType("application/pdf"));
        PdfTextExtractor parser = new PdfTextExtractor(
                new PdfReader(result.andReturn().getResponse().getContentAsByteArray()));
        String[] fileRows = parser.getTextFromPage(1).split("\n");
        assertThat(fileRows[0]).isEqualTo("test group week name");
        assertThat(fileRows[1]).isEqualTo(" ");
        assertThat(fileRows[2]).isEqualTo(" Date Start lessonEnd lessonTeacherCourseClassroomLesson link");
        assertThat(fileRows[3]).isEqualTo(" 2020-10-20 10:00 12:00 Bob MorenLaw1");
        assertThat(fileRows[4]).isEqualTo(" 15:00 17:00 Ann MorenMath2test-link");
        assertThat(fileRows[5]).isEqualTo(" 21:00 22:00 Ann MorenMath2test-link");
    }

    @Test
    void testCreateMonthGroupTimetablePDF() throws Exception {
        GroupDto groupDto = new GroupDto();
        groupDto.setName("test group mounth name");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        lessons.remove(2);
        List<DayTimetable> timetables = new ArrayList<>();
        timetables.add(new DayTimetable(LocalDate.parse("2020-10-20"), lessons));
        when(timetableServiceMock.createMonthGroupTimetable(LocalDate.now(), "test group mounth name"))
                .thenReturn(timetables);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/pdf/monthgroup/").flashAttr("group",
                groupDto);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.content().contentType("application/pdf"));
        PdfTextExtractor parser = new PdfTextExtractor(
                new PdfReader(result.andReturn().getResponse().getContentAsByteArray()));
        String[] fileRows = parser.getTextFromPage(1).split("\n");
        assertThat(fileRows[0]).isEqualTo("test group mounth name");
        assertThat(fileRows[1]).isEqualTo(" ");
        assertThat(fileRows[2]).isEqualTo(" Date Start lessonEnd lessonTeacherCourseClassroomLesson link");
        assertThat(fileRows[3]).isEqualTo(" 2020-10-20 10:00 12:00 Bob MorenLaw1");
        assertThat(fileRows[4]).isEqualTo(" 15:00 17:00 Ann MorenMath2test-link");
    }

    @Test
    void testCreateWeekTeacherTimetablePDF() throws Exception {
        UserDto teacher = new UserDto();
        teacher.setName("test teacher week name");
        teacher.setEmail("test email");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        List<DayTimetable> timetables = new ArrayList<>();
        timetables.add(new DayTimetable(LocalDate.parse("2020-10-20"), lessons));
        when(timetableServiceMock.createWeekTeacherTimetable(LocalDate.now(), "test email")).thenReturn(timetables);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/pdf/weekteacher/").flashAttr("teacher",
                teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.content().contentType("application/pdf"));
        PdfTextExtractor parser = new PdfTextExtractor(
                new PdfReader(result.andReturn().getResponse().getContentAsByteArray()));
        String[] fileRows = parser.getTextFromPage(1).split("\n");
        assertThat(fileRows[0]).isEqualTo("test teacher week name");
        assertThat(fileRows[1]).isEqualTo(" ");
        assertThat(fileRows[2]).isEqualTo(" Date Start lessonEnd lessonGroupCourseClassroomLesson link");
        assertThat(fileRows[3]).isEqualTo(" 2020-10-20 10:00 12:00 AB-22 Law 1");
        assertThat(fileRows[4]).isEqualTo(" 15:00 17:00 FR-33 Math 2 test-link");
        assertThat(fileRows[5]).isEqualTo(" 21:00 22:00 FR-33 Math 2 test-link");
    }

    @Test
    void testCreateMonthTeacherTimetablePDF() throws Exception {
        UserDto teacher = new UserDto();
        teacher.setName("test teacher month name");
        teacher.setEmail("test email");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        lessons.remove(2);
        List<DayTimetable> timetables = new ArrayList<>();
        timetables.add(new DayTimetable(LocalDate.parse("2020-10-20"), lessons));
        when(timetableServiceMock.createMonthTeacherTimetable(LocalDate.now(), "test email")).thenReturn(timetables);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/pdf/monthteacher/").flashAttr("teacher",
                teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.content().contentType("application/pdf"));
        PdfTextExtractor parser = new PdfTextExtractor(
                new PdfReader(result.andReturn().getResponse().getContentAsByteArray()));
        String[] fileRows = parser.getTextFromPage(1).split("\n");
        assertThat(fileRows[0]).isEqualTo("test teacher month name");
        assertThat(fileRows[1]).isEqualTo(" ");
        assertThat(fileRows[2]).isEqualTo(" Date Start lessonEnd lessonGroupCourseClassroomLesson link");
        assertThat(fileRows[3]).isEqualTo(" 2020-10-20 10:00 12:00 AB-22 Law 1");
        assertThat(fileRows[4]).isEqualTo(" 15:00 17:00 FR-33 Math 2 test-link");
    }

    @Test
    void testCreateWeekGroupTimetablePDFWhenIOException() throws Exception {
        PDFDataGenerator dataGeneratorMock = Mockito.spy(PDFDataGenerator.class);
        PDFController pdfController = new PDFController(timetableServiceMock, dataGeneratorMock);
        mockMvc = MockMvcBuilders.standaloneSetup(pdfController).build();
        willAnswer(invocation -> {
            throw new IOException();
        }).given(dataGeneratorMock).generateGroupTimetable(Mockito.any(), Mockito.any(), Mockito.any());
        Logger PDFControllerLogger = (Logger) LoggerFactory.getLogger(PDFController.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        PDFControllerLogger.addAppender(listAppender);
        GroupDto groupDto = new GroupDto();
        groupDto.setName("test group week name");
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        List<DayTimetable> timetables = new ArrayList<>();
        timetables.add(new DayTimetable(LocalDate.parse("2020-10-20"), lessons));
        when(timetableServiceMock.createWeekGroupTimetable(LocalDate.now(), "test group week name"))
                .thenReturn(timetables);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/pdf/weekgroup/").flashAttr("group",
                groupDto);
        mockMvc.perform(request);
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals("File creation failed!", logsList.get(0).getMessage());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
    }
}
