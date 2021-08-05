package org.university.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.university.dto.ClassroomDto;
import org.university.entity.Classroom;
import org.university.exceptions.InvalidAddressException;
import org.university.exceptions.InvalidClassroomCapacityException;
import org.university.exceptions.InvalidClassroomNumberException;
import org.university.service.ClassroomService;
import org.university.utils.CreatorTestEntities;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class ClassroomControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClassroomService classroomServiceMock;
    
    private ClassroomController classroomController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        classroomController = new ClassroomController(classroomServiceMock);
        mockMvc = MockMvcBuilders.standaloneSetup(classroomController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetAll() throws Exception {
        List<Classroom> classrooms = CreatorTestEntities.createClassrooms();
        when(classroomServiceMock.findAllClassrooms()).thenReturn(classrooms);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/classrooms/");
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("classrooms"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("classrooms"))
                .andExpect(MockMvcResultMatchers.model().attribute("classrooms", classrooms));
    }

    @Test
    void testAdd() throws Exception {
        ClassroomDto classroom = new ClassroomDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/classrooms/")
                .flashAttr("classroom", classroom);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/classrooms"));
        verify(classroomServiceMock).addClassroom(classroom);
    }
    
    @Test
    void testAddWhenInputInvalidNumber() throws Exception {
        ClassroomDto classroom = new ClassroomDto();
        classroom.setNumber(-5);
        doThrow(new InvalidClassroomNumberException("Input classroom number can't be negative or zero!"))
            .when(classroomServiceMock).addClassroom(classroom);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/classrooms").servletPath("/classrooms")
                .flashAttr("classroom", classroom);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/classrooms"))
                .andExpect(MockMvcResultMatchers.model().attribute("message", "Input classroom number can't be negative or zero!"));
    }
    
    @Test
    void testAddWhenInputInvalidCapacity() throws Exception {
        ClassroomDto classroom = new ClassroomDto();
        classroom.setCapacity(-5);
        doThrow(new InvalidClassroomCapacityException("Input classroom capacity can't be negative or zero!"))
            .when(classroomServiceMock).addClassroom(classroom);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/classrooms").servletPath("/classrooms")
                .flashAttr("classroom", classroom);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/classrooms"))
                .andExpect(MockMvcResultMatchers.model().attribute("message", "Input classroom capacity can't be negative or zero!"));
    }
    
    @Test
    void testAddWhenInputInvalidAddress() throws Exception {
        ClassroomDto classroom = new ClassroomDto();
        classroom.setAddress("fff");
        doThrow(new InvalidAddressException("Input address isn't valid!"))
            .when(classroomServiceMock).addClassroom(classroom);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/classrooms").servletPath("/classrooms")
                .flashAttr("classroom", classroom);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/classrooms"))
                .andExpect(MockMvcResultMatchers.model().attribute("message", "Input address isn't valid!"));
    }

    @Test
    void testDelete() throws Exception {
        ClassroomDto classroom = new ClassroomDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/classrooms/")
                .flashAttr("classroom", classroom);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/classrooms"));
        verify(classroomServiceMock).delete(classroom);
    }
    
    @Test
    void testGetEditForm() throws Exception {
        ClassroomDto classroom = new ClassroomDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/classrooms/edit/")
                .flashAttr("classroom", classroom);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/classroom"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("classroom"))
                .andExpect(MockMvcResultMatchers.model().attribute("classroom", classroom));
    }
    
    @Test
    void testEdit() throws Exception {
        ClassroomDto classroom = new ClassroomDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/classrooms/")
                .flashAttr("classroom", classroom);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/classrooms"));
        verify(classroomServiceMock).edit(classroom);
    }
    
    @Test
    void testEditWhenInputInvalidNumber() throws Exception {
        ClassroomDto classroom = new ClassroomDto();
        classroom.setNumber(-5);
        doThrow(new InvalidClassroomNumberException("Input classroom number can't be negative or zero!"))
            .when(classroomServiceMock).edit(classroom);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/classrooms/")
                .flashAttr("classroom", classroom);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/classroom"))
                .andExpect(MockMvcResultMatchers.model().attribute("message", "Input classroom number can't be negative or zero!"));
    }
    
    @Test
    void testEditWhenInputInvalidCapacity() throws Exception {
        ClassroomDto classroom = new ClassroomDto();
        classroom.setCapacity(-5);
        doThrow(new InvalidClassroomCapacityException("Input classroom capacity can't be negative or zero!"))
            .when(classroomServiceMock).edit(classroom);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/classrooms/")
                .flashAttr("classroom", classroom);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/classroom"))
                .andExpect(MockMvcResultMatchers.model().attribute("message", "Input classroom capacity can't be negative or zero!"));
    }
    
    @Test
    void testEditWhenInputInvalidAddress() throws Exception {
        ClassroomDto classroom = new ClassroomDto();
        classroom.setAddress("fff");
        doThrow(new InvalidAddressException("Input address isn't valid!")).when(classroomServiceMock).edit(classroom);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/classrooms/")
                .flashAttr("classroom", classroom);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/classroom"))
                .andExpect(MockMvcResultMatchers.model().attribute("message", "Input address isn't valid!"));
    }
}
