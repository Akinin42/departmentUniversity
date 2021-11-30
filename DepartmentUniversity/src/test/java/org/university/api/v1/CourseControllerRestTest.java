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
import org.university.dto.CourseDto;
import org.university.entity.Course;
import org.university.service.CourseService;
import org.university.utils.CreatorTestEntities;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class CourseControllerRestTest {
    
    private MockMvc mockMvc;
    
    private ObjectMapper mapper;

    @Mock
    private CourseService courseServiceMock;

    private CourseControllerRest courseController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        mapper = new ObjectMapper();
        courseController = new CourseControllerRest(courseServiceMock);
        mockMvc = MockMvcBuilders.standaloneSetup(courseController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetAll() throws Exception {
        List<Course> courses = CreatorTestEntities.createCourses();
        when(courseServiceMock.findAllCourses()).thenReturn(courses);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is("Law")))
                .andExpect(jsonPath("$[1].name", is("Math")))
                .andExpect(jsonPath("$[2].name", is("Art")))
                .andExpect(jsonPath("$", Matchers.hasSize(3)));
    }

    @Test
    void testAdd() throws Exception {
        CourseDto course = new CourseDto();
        course.setName("test course");
        course.setDescription("it is a valid course");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(course));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isCreated());
        verify(courseServiceMock).addCourse(course);
    }

    @Test
    void testAddWhenInputInvalidName() throws Exception {
        CourseDto course = new CourseDto();
        course.setName("d");
        course.setDescription("it is a valid course");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(course));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(MethodArgumentNotValidException.class,
                exception.getResolvedException().getClass()))
                .andExpect(exception -> assertTrue(
                        exception.getResolvedException().getMessage().contains("must match \"[A-Za-z ]{2,50}\"")))
                .andExpect(exception -> assertTrue(exception.getResolvedException().getMessage().contains("name")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddWhenInputInvalidDescription() throws Exception {
        CourseDto course = new CourseDto();
        course.setName("test course");
        course.setDescription("f");        
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(course));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(MethodArgumentNotValidException.class,
                exception.getResolvedException().getClass()))
                .andExpect(exception -> assertTrue(
                        exception.getResolvedException().getMessage().contains("size must be between 5 and 2147483647")))
                .andExpect(exception -> assertTrue(exception.getResolvedException().getMessage().contains("description")))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testEdit() throws Exception {
        CourseDto course = new CourseDto();
        course.setName("test course");
        course.setDescription("it is a valid course");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(course));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk());
        verify(courseServiceMock).edit(course);
    }

    @Test
    void testEditWhenInputInvalidName() throws Exception {
        CourseDto course = new CourseDto();
        course.setName("d");
        course.setDescription("it is a valid course");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(course));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(MethodArgumentNotValidException.class,
                exception.getResolvedException().getClass()))
                .andExpect(exception -> assertTrue(
                        exception.getResolvedException().getMessage().contains("must match \"[A-Za-z ]{2,50}\"")))
                .andExpect(exception -> assertTrue(exception.getResolvedException().getMessage().contains("name")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEditWhenInputInvalidDescription() throws Exception {
        CourseDto course = new CourseDto();
        course.setName("test course");
        course.setDescription("f");        
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(course));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(MethodArgumentNotValidException.class,
                exception.getResolvedException().getClass()))
                .andExpect(exception -> assertTrue(
                        exception.getResolvedException().getMessage().contains("size must be between 5 and 2147483647")))
                .andExpect(exception -> assertTrue(exception.getResolvedException().getMessage().contains("description")))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testDelete() throws Exception {
        CourseDto course = new CourseDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(course));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk());
        verify(courseServiceMock).delete(course);
    }
}
