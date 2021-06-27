package org.university.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.university.dto.CourseDto;
import org.university.entity.Course;
import org.university.service.CourseService;
import org.university.utils.CreatorTestEntities;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class CourseControllerTest {

    private MockMvc mockMvc; 
    
    @Mock
    private CourseService courseServiceMock;

    @InjectMocks
    private CourseController courseController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();
    }

    @Test
    void testGetAll() throws Exception {
        List<Course> courses = CreatorTestEntities.createCourses();
        when(courseServiceMock.findAllCourses()).thenReturn(courses);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/courses/");
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("courses"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("courses"))
                .andExpect(MockMvcResultMatchers.model().attribute("courses", courses));
    }
    
    @Test
    void testAdd() throws Exception {
        CourseDto course = new CourseDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/courses/add/").flashAttr("course", course);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/courses"));
        verify(courseServiceMock).addCourse(course);     
    }
    
    @Test
    void testDelete() throws Exception {
        CourseDto course = new CourseDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/courses/delete/").flashAttr("course", course);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/courses"));
        verify(courseServiceMock).delete(course);
    }
}
