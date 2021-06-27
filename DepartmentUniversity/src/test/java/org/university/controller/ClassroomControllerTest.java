package org.university.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.university.dto.ClassroomDto;
import org.university.entity.Classroom;
import org.university.service.ClassroomService;
import org.university.utils.CreatorTestEntities;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class ClassroomControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClassroomService classroomServiceMock;

    @InjectMocks
    private ClassroomController classroomController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(classroomController).build();
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
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/classrooms/add/").flashAttr("classroom",
                classroom);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/classrooms"));
        verify(classroomServiceMock).addClassroom(classroom);
    }

    @Test
    void testDelete() throws Exception {
        ClassroomDto classroom = new ClassroomDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/classrooms/delete/")
                .flashAttr("classroom", classroom);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/classrooms"));
        verify(classroomServiceMock).delete(classroom);
    }
}
