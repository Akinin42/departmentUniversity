package org.university.controller;

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
import org.university.dto.GroupDto;
import org.university.dto.StudentDto;
import org.university.entity.Group;
import org.university.entity.Student;
import org.university.service.GroupService;
import org.university.service.StudentService;
import org.university.utils.CreatorTestEntities;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class GroupControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GroupService groupServiceMock;

    @Mock
    private StudentService studentServiceMock;

    private GroupController groupController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        groupController = new GroupController(groupServiceMock, studentServiceMock);
        mockMvc = MockMvcBuilders.standaloneSetup(groupController).build();
    }

    @Test
    void testGetAll() throws Exception {
        List<Group> groups = CreatorTestEntities.createGroups();
        List<Student> students = CreatorTestEntities.createStudents();
        when(groupServiceMock.findAllGroups()).thenReturn(groups);
        when(studentServiceMock.findAll()).thenReturn(students);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/groups/");
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("groups"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("groups"))
                .andExpect(MockMvcResultMatchers.model().attribute("groups", groups))
                .andExpect(MockMvcResultMatchers.model().attributeExists("students"))
                .andExpect(MockMvcResultMatchers.model().attribute("students", students));
    }

    @Test
    void testAdd() throws Exception {
        GroupDto group = new GroupDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/groups/").flashAttr("group", group);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/groups"));
        verify(groupServiceMock).addGroup(group);
    }

    @Test
    void testDelete() throws Exception {
        GroupDto group = new GroupDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/groups/").flashAttr("group",
                group);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/groups"));
        verify(groupServiceMock).delete(group);
    }

    @Test
    void testAddStudent() throws Exception {
        StudentDto student = new StudentDto();
        student.setGroupName("test");
        Group group = Group.builder()
                .withName("test")
                .build();
        when(groupServiceMock.createGroup("test")).thenReturn(group);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/groups/student/")
                .flashAttr("student", student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/groups"));
        verify(studentServiceMock).addStudentToGroup(student, group);
    }

    @Test
    void testDeleteStudent() throws Exception {
        StudentDto student = new StudentDto();
        student.setGroupName("test");
        Group group = Group.builder().withName("test").build();
        when(groupServiceMock.createGroup("test")).thenReturn(group);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/groups/student/")
                .flashAttr("student", student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/groups"));
        verify(studentServiceMock).deleteStudentFromGroup(student, group);
    }
}
