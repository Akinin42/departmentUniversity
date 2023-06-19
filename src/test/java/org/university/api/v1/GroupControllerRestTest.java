package org.university.api.v1;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.university.controller.GlobalExceptionHandler;
import org.university.dto.GroupDto;
import org.university.dto.StudentDto;
import org.university.entity.Group;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.service.GroupService;
import org.university.service.StudentService;
import org.university.utils.CreatorTestEntities;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class GroupControllerRestTest {

    private MockMvc mockMvc;
    
    private ObjectMapper mapper;

    @Mock
    private GroupService groupServiceMock;

    @Mock
    private StudentService studentServiceMock;

    private GroupControllerRest groupController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        mapper = new ObjectMapper();
        groupController = new GroupControllerRest(groupServiceMock);
        mockMvc = MockMvcBuilders.standaloneSetup(groupController).setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetAll() throws Exception {
        List<Group> groups = CreatorTestEntities.createGroups();
        when(groupServiceMock.findAllGroups()).thenReturn(groups);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/groups")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is("AB-22")))
                .andExpect(jsonPath("$[1].name", is("FR-33")))
                .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }

    @Test
    void testAdd() throws Exception {
        GroupDto group = new GroupDto();
        group.setName("AA-37");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(group));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isCreated());
        verify(groupServiceMock).addGroup(group);
    }

    @Test
    void testAddWhenInputInvalidName() throws Exception {
        GroupDto group = new GroupDto();
        group.setName("invalid name");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(group));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(MethodArgumentNotValidException.class,
                exception.getResolvedException().getClass()))
                .andExpect(exception -> assertTrue(
                        exception.getResolvedException().getMessage().contains("must match \"[A-Z]{2}-\\d{2}\"")))
                .andExpect(exception -> assertTrue(exception.getResolvedException().getMessage().contains("name")))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testAddWhenInputNameExist() throws Exception {
        GroupDto group = new GroupDto();
        group.setName("AB-22");
        doThrow(new EntityAlreadyExistException("groupexist")).when(groupServiceMock)
                .addGroup(group);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(group));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(
                        exception.getResolvedException().getMessage(), "400 BAD_REQUEST \"groupexist\""))                
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDelete() throws Exception {
        GroupDto group = new GroupDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/api/v1/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(group));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk());
        verify(groupServiceMock).delete(group);
    }

    @Test
    void testAddStudent() throws Exception {
        StudentDto student = new StudentDto();
        student.setGroupName("test");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/groups/student")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(student));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk());
        verify(groupServiceMock).addStudentToGroup(student);
    }

    @Test
    void testDeleteStudent() throws Exception {
        StudentDto student = new StudentDto();
        student.setGroupName("test");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/api/v1/groups/student")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(student));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk());
        verify(groupServiceMock).deleteStudentFromGroup(student);
    }

    @Test
    void testEdit() throws Exception {
        GroupDto group = new GroupDto();
        group.setName("AA-37");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/api/v1/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(group));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk());
        verify(groupServiceMock).edit(group);
    }

    @Test
    void testEditInputInvalidName() throws Exception {
        GroupDto group = new GroupDto();
        group.setName("invalid name");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/api/v1/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(group));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(MethodArgumentNotValidException.class,
                exception.getResolvedException().getClass()))
                .andExpect(exception -> assertTrue(
                        exception.getResolvedException().getMessage().contains("must match \"[A-Z]{2}-\\d{2}\"")))
                .andExpect(exception -> assertTrue(exception.getResolvedException().getMessage().contains("name")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEditInputExistName() throws Exception {
        GroupDto group = new GroupDto();
        group.setName("AB-22");
        doThrow(new EntityAlreadyExistException("groupexist")).when(groupServiceMock)
                .edit(group);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/api/v1/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(group));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(
                        exception.getResolvedException().getMessage(), "400 BAD_REQUEST \"groupexist\""))                
                .andExpect(status().isBadRequest());
    }
}
