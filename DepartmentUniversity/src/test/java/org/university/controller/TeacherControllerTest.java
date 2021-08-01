package org.university.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.university.dto.TeacherDto;
import org.university.entity.Teacher;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EntityNotExistException;
import org.university.exceptions.InvalidEmailException;
import org.university.exceptions.InvalidPhoneException;
import org.university.exceptions.InvalidUserNameException;
import org.university.service.PhotoService;
import org.university.service.TeacherService;
import org.university.utils.CreatorTestEntities;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class TeacherControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TeacherService teacherServiceMock;
    
    @Mock
    private PhotoService photoServiceMock;
    
    private TeacherController teacherController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        teacherController = new TeacherController(teacherServiceMock, photoServiceMock);
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
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/teachers/")
                .flashAttr("teacher", teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/teachers"));
        verify(teacherServiceMock).register(teacher);
    }
    
    @Test
    void testAddTeacherWhenInputInvalidName() throws Exception {
        TeacherDto teacher = new TeacherDto();
        teacher.setName("invalid name");
        doThrow(new InvalidUserNameException("Input name isn't valid!")).when(teacherServiceMock).register(teacher);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/teachers/")
                .flashAttr("teacher", teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("teacherform"))
                .andExpect(MockMvcResultMatchers.model().attribute("message", "Input name isn't valid!"));
    }
    
    @Test
    void testAddTeacherWhenInputInvalidEmail() throws Exception {
        TeacherDto teacher = new TeacherDto();
        teacher.setEmail("invalid email");
        doThrow(new InvalidEmailException("Input email isn't valid!")).when(teacherServiceMock).register(teacher);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/teachers/")
                .flashAttr("teacher", teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("teacherform"))
        .andExpect(MockMvcResultMatchers.model().attribute("message", "Input email isn't valid!"));
    }
    
    @Test
    void testAddTeacherWhenInputInvalidPhone() throws Exception {
        TeacherDto teacher = new TeacherDto();
        teacher.setEmail("invalid email");
        doThrow(new InvalidPhoneException("Input phone isn't valid!")).when(teacherServiceMock).register(teacher);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/teachers/")
                .flashAttr("teacher", teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("teacherform"))
        .andExpect(MockMvcResultMatchers.model().attribute("message", "Input phone isn't valid!"));
    }

    @Test
    void testDelete() throws Exception {
        TeacherDto teacher = new TeacherDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/teachers/")
                .flashAttr("teacher", teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/teachers"));
        verify(teacherServiceMock).delete(teacher);
    }
    
    @Test
    void testNewTeacher() throws Exception {
        TeacherDto teacher = new TeacherDto();        
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/teachers/new/");
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("teacherform"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("teacher"))
                .andExpect(MockMvcResultMatchers.model().attribute("teacher", teacher));
    }
    
    @Test
    void testOtherTeachersWhenInputNumberNegativeAndShowFirstTeachersYet() throws Exception {        
        List<Teacher> teachers = CreatorTestEntities.createTeachers();
        when(teacherServiceMock.findNumberOfUsers(5, 0)).thenReturn(teachers);        
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/teachers/-1").sessionAttr("pagesNumber", 0);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("teachers"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("teachers"))
                .andExpect(MockMvcResultMatchers.model().attribute("teachers", teachers));
    }
    
    @Test
    void testOtherTeachers() throws Exception {
        List<Teacher> nextTeachers = CreatorTestEntities.createTeachers();
        nextTeachers.remove(1);
        when(teacherServiceMock.findNumberOfUsers(5, 5)).thenReturn(nextTeachers);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/teachers/1").sessionAttr("pagesNumber", 0);
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
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/teachers/1").sessionAttr("pagesNumber", 0);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("teachers"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("teachers"))
                .andExpect(MockMvcResultMatchers.model().attribute("teachers", teachers));
    }
    
    @Test
    void testLoginWhenTeacherWithInputEmailAndPasswordExists() throws Exception {
        TeacherDto teacher = new TeacherDto();
        teacher.setEmail("test");
        teacher.setPassword("test");
        Teacher expectedTeacher = Teacher.builder().withEmail("test").build();
        when(teacherServiceMock.login(teacher.getEmail(), teacher.getPassword())).thenReturn(expectedTeacher);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/teachers/login/")
                .flashAttr("teacher", teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("teacherprofile"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("teacher"))
                .andExpect(MockMvcResultMatchers.model().attribute("teacher", expectedTeacher));        
    }
    
    @Test
    void testLoginWhenTeacherWithInputEmailNotExists() throws Exception {
        TeacherDto teacher = new TeacherDto();
        teacher.setEmail("notexistemail");
        teacher.setPassword("test");        
        when(teacherServiceMock.login(teacher.getEmail(), teacher.getPassword())).thenThrow(EntityNotExistException.class);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/teachers/login/")
                .flashAttr("teacher", teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("teacherform"));
    }
    
    @Test
    void testLoginWhenTeacherWithInputEmailExistsButPasswordNotCorrect() throws Exception {
        TeacherDto teacher = new TeacherDto();
        teacher.setEmail("test");
        teacher.setPassword("incorrect password");        
        when(teacherServiceMock.login(teacher.getEmail(), teacher.getPassword())).thenThrow(AuthorisationFailException.class);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/teachers/login/")
                .flashAttr("teacher", teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/teachers"));
    }
    
    @Test
    void testGetEditForm() throws Exception {
        TeacherDto teacher = new TeacherDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/teachers/edit/")
                .flashAttr("teacher", teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/teacher"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("teacher"))
                .andExpect(MockMvcResultMatchers.model().attribute("teacher", teacher));
    }
    
    @Test
    void testEdit() throws Exception {
        TeacherDto teacher = new TeacherDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/teachers/update/")
                .flashAttr("teacher", teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/teachers"));
        verify(teacherServiceMock).edit(teacher);
    }
    
    @Test
    void testEditWhenInputInvalidName() throws Exception {
        TeacherDto teacher = new TeacherDto();
        teacher.setName("invalid name");
        doThrow(new InvalidUserNameException("Input name isn't valid!")).when(teacherServiceMock).edit(teacher);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/teachers/update/")
                .flashAttr("teacher", teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/teacher"))
                .andExpect(MockMvcResultMatchers.model().attribute("message", "Input name isn't valid!"));
    }
    
    @Test
    void testEditWhenInputInvalidEmail() throws Exception {
        TeacherDto teacher = new TeacherDto();
        teacher.setEmail("invalid email");
        doThrow(new InvalidEmailException("Input email isn't valid!")).when(teacherServiceMock).edit(teacher);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/teachers/update/")
                .flashAttr("teacher", teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/teacher"))
        .andExpect(MockMvcResultMatchers.model().attribute("message", "Input email isn't valid!"));
    }
    
    @Test
    void testEditWhenInputInvalidPhone() throws Exception {
        TeacherDto teacher = new TeacherDto();
        teacher.setEmail("invalid email");
        doThrow(new InvalidPhoneException("Input phone isn't valid!")).when(teacherServiceMock).edit(teacher);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/teachers/update/")
                .flashAttr("teacher", teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/teacher"))
        .andExpect(MockMvcResultMatchers.model().attribute("message", "Input phone isn't valid!"));
    }
}
