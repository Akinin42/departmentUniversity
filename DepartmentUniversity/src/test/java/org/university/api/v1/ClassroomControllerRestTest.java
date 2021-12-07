package org.university.api.v1;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.university.controller.GlobalExceptionHandler;
import org.university.dto.ClassroomDto;
import org.university.entity.Classroom;
import org.university.service.ClassroomService;
import org.university.utils.CreatorTestEntities;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class ClassroomControllerRestTest {

    private MockMvc mockMvc;

    private ObjectMapper mapper;

    @Mock
    private ClassroomService classroomServiceMock;

    private ClassroomControllerRest classroomController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        mapper = new ObjectMapper();
        classroomController = new ClassroomControllerRest(classroomServiceMock);
        mockMvc = MockMvcBuilders.standaloneSetup(classroomController)
                                    .setControllerAdvice(new GlobalExceptionHandler())
                                    .build();
    }

    @Test
    void testGetAll() throws Exception {
        List<Classroom> classrooms = CreatorTestEntities.createClassrooms();
        when(classroomServiceMock.findAllClassrooms()).thenReturn(classrooms);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/classrooms")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].number", is(1)))
                .andExpect(jsonPath("$[1].number", is(2)))
                .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }

    @Test
    void testAdd() throws Exception {
        ClassroomDto classroom = new ClassroomDto();
        classroom.setNumber(5);
        classroom.setAddress("test address");
        classroom.setCapacity(10);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/classrooms")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(classroom));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isCreated());
        verify(classroomServiceMock).addClassroom(classroom);
    }

    @Test
    void testAddWhenInputInvalidNumber() throws Exception {
        ClassroomDto classroom = new ClassroomDto();
        classroom.setNumber(-5);
        classroom.setAddress("test address");
        classroom.setCapacity(10);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/classrooms")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(classroom));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(MethodArgumentNotValidException.class,
                exception.getResolvedException().getClass()))
                .andExpect(exception -> assertTrue(
                        exception.getResolvedException().getMessage().contains("must be greater than or equal to 1")))
                .andExpect(exception -> assertTrue(exception.getResolvedException().getMessage().contains("number")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddWhenInputInvalidCapacity() throws Exception {
        ClassroomDto classroom = new ClassroomDto();
        classroom.setNumber(10);
        classroom.setAddress("test address");
        classroom.setCapacity(-5);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/classrooms")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(classroom));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(MethodArgumentNotValidException.class,
                exception.getResolvedException().getClass()))
                .andExpect(exception -> assertTrue(
                        exception.getResolvedException().getMessage().contains("must be greater than or equal to 1")))
                .andExpect(exception -> assertTrue(exception.getResolvedException().getMessage().contains("capacity")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddWhenInputInvalidAddress() throws Exception {
        ClassroomDto classroom = new ClassroomDto();
        classroom.setNumber(10);
        classroom.setAddress("fff");
        classroom.setCapacity(20);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/classrooms")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(classroom));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(MethodArgumentNotValidException.class,
                exception.getResolvedException().getClass()))
                .andExpect(exception -> assertTrue(
                        exception.getResolvedException().getMessage().contains("size must be between 5 and 100")))
                .andExpect(exception -> assertTrue(exception.getResolvedException().getMessage().contains("address")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDelete() throws Exception {
        ClassroomDto classroom = new ClassroomDto();
        classroom.setId(1);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/api/v1/classrooms/1")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk());
        verify(classroomServiceMock).delete(classroom);
    }

    @Test
    void testEdit() throws Exception {
        ClassroomDto classroom = new ClassroomDto();
        classroom.setNumber(5);
        classroom.setAddress("test address");
        classroom.setCapacity(10);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/api/v1/classrooms")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(classroom));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk());
        verify(classroomServiceMock).edit(classroom);
    }

    @Test
    void testEditWhenInputInvalidNumber() throws Exception {
        ClassroomDto classroom = new ClassroomDto();
        classroom.setNumber(-5);
        classroom.setAddress("test address");
        classroom.setCapacity(10);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/api/v1/classrooms")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(classroom));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(MethodArgumentNotValidException.class,
                exception.getResolvedException().getClass()))
                .andExpect(exception -> assertTrue(
                        exception.getResolvedException().getMessage().contains("must be greater than or equal to 1")))
                .andExpect(exception -> assertTrue(exception.getResolvedException().getMessage().contains("number")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEditWhenInputInvalidCapacity() throws Exception {
        ClassroomDto classroom = new ClassroomDto();
        classroom.setNumber(10);
        classroom.setAddress("test address");
        classroom.setCapacity(-5);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/api/v1/classrooms")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(classroom));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(MethodArgumentNotValidException.class,
                exception.getResolvedException().getClass()))
                .andExpect(exception -> assertTrue(
                        exception.getResolvedException().getMessage().contains("must be greater than or equal to 1")))
                .andExpect(exception -> assertTrue(exception.getResolvedException().getMessage().contains("capacity")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEditWhenInputInvalidAddress() throws Exception {
        ClassroomDto classroom = new ClassroomDto();
        classroom.setNumber(10);
        classroom.setAddress("fff");
        classroom.setCapacity(20);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/api/v1/classrooms")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(classroom));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(MethodArgumentNotValidException.class,
                exception.getResolvedException().getClass()))
                .andExpect(exception -> assertTrue(
                        exception.getResolvedException().getMessage().contains("size must be between 5 and 100")))
                .andExpect(exception -> assertTrue(exception.getResolvedException().getMessage().contains("address")))
                .andExpect(status().isBadRequest());
    }
}
