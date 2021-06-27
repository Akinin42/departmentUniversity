package org.university.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.university.dto.TeacherDto;
import org.university.entity.Teacher;
import org.university.service.TeacherService;
import org.university.utils.CreatorTestEntities;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class TeacherControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TeacherService teacherServiceMock;

    @InjectMocks
    private TeacherController teacherController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(teacherController).build();
    }

    @Test
    void testGetTeachers() throws Exception {
        List<Teacher> teachers = CreatorTestEntities.createTeachers();
        when(teacherServiceMock.findNumberOfUsers(5, 0)).thenReturn(teachers);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/teachers/");
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("teachers"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("teachers"))
                .andExpect(MockMvcResultMatchers.model().attribute("teachers", teachers));
    }

    @Test
    void testAddTeacher() throws Exception {
        TeacherDto teacher = new TeacherDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/teachers/addTeacher/")
                .flashAttr("teacher", teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/teachers"));
        verify(teacherServiceMock).register(teacher);
    }

    @Test
    void testDelete() throws Exception {
        TeacherDto teacher = new TeacherDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/teachers/delete/")
                .flashAttr("teacher", teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/teachers"));
        verify(teacherServiceMock).delete(teacher);
    }
    
    @Test
    void testNewTeacher() throws Exception {
        TeacherDto teacher = new TeacherDto();        
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/teachers/newTeacher/");
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("teacherform"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("teacher"))
                .andExpect(MockMvcResultMatchers.model().attribute("teacher", teacher));
    }
    
    @Test
    void testOtherTeachersWhenInputNumberNegativeAndShowFirstTeachersYet() throws Exception {        
        List<Teacher> teachers = CreatorTestEntities.createTeachers();
        when(teacherServiceMock.findNumberOfUsers(5, 0)).thenReturn(teachers);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/teachers/other/").param("number", "-5");
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("teachers"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("teachers"))
                .andExpect(MockMvcResultMatchers.model().attribute("teachers", teachers));
    }
    
    @Test
    void testOtherTeachers() throws Exception {
        List<Teacher> nextTeachers = CreatorTestEntities.createTeachers();
        nextTeachers.remove(1);
        when(teacherServiceMock.findNumberOfUsers(5, 1)).thenReturn(nextTeachers);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/teachers/other/").param("number", "1");
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("teachers"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("teachers"))
                .andExpect(MockMvcResultMatchers.model().attribute("teachers", nextTeachers));
    }
    
    @Test
    void testOtherTeachersWhenNextTeacherNotExist() throws Exception {
        List<Teacher> teachers = CreatorTestEntities.createTeachers();
        when(teacherServiceMock.findNumberOfUsers(5, 0)).thenReturn(teachers);
        when(teacherServiceMock.findNumberOfUsers(5, 5)).thenReturn(new ArrayList<Teacher>());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/teachers/other/").param("number", "5");
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("teachers"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("teachers"))
                .andExpect(MockMvcResultMatchers.model().attribute("teachers", teachers));
    }    
}
