package org.university.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
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
import org.springframework.web.multipart.MultipartFile;
import org.university.dto.UserDto;
import org.university.entity.Teacher;
import org.university.exceptions.EmailExistException;
import org.university.exceptions.InvalidPhotoException;
import org.university.service.PhotoService;
import org.university.service.TeacherService;
import org.university.utils.CreatorTestEntities;
import org.university.utils.Sex;

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
        mockMvc = MockMvcBuilders.standaloneSetup(teacherController).setControllerAdvice(new GlobalExceptionHandler())
                .build();
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
    void testGetTeachersWhenNumberUsersNotNull() throws Exception {
        List<Teacher> teachers = CreatorTestEntities.createTeachers();
        when(teacherServiceMock.findNumberOfUsers(10, 0)).thenReturn(teachers);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/teachers/").sessionAttr("numberUsers", 10);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("teachers"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("teachers"))
                .andExpect(MockMvcResultMatchers.model().attribute("teachers", teachers));
    }

    @Test
    void testDelete() throws Exception {
        UserDto teacher = new UserDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/teachers/").flashAttr("teacher",
                teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/teachers"));
        verify(teacherServiceMock).delete(teacher);
    }

    @Test
    void testOtherTeachersWhenInputNumberNegativeAndShowFirstTeachersYet() throws Exception {
        List<Teacher> teachers = CreatorTestEntities.createTeachers();
        when(teacherServiceMock.findNumberOfUsers(5, 0)).thenReturn(teachers);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/teachers/-1").sessionAttr("pagesNumber", 0)
                .sessionAttr("numberUsers", 5);
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
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/teachers/1").sessionAttr("pagesNumber", 0)
                .sessionAttr("numberUsers", 5);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("teachers"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("teachers"))
                .andExpect(MockMvcResultMatchers.model().attribute("teachers", nextTeachers));
    }

    @Test
    void testOtherTeachersWhenNextTeacherNotExist() throws Exception {
        List<Teacher> teachers = CreatorTestEntities.createTeachers();
        when(teacherServiceMock.findNumberOfUsers(5, 0)).thenReturn(teachers);
        when(teacherServiceMock.findNumberOfUsers(5, 1)).thenReturn(new ArrayList<Teacher>());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/teachers/1").sessionAttr("pagesNumber", 0)
                .sessionAttr("numberUsers", 5);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("teachers"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("teachers"))
                .andExpect(MockMvcResultMatchers.model().attribute("teachers", teachers));
    }

    @Test
    void testSetNumberUsers() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/teachers/numbers/10");
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/teachers"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("numberUsers"))
                .andExpect(MockMvcResultMatchers.model().attribute("numberUsers", 10));
    }

    @Test
    void testGetProfileWhenTeacherWithInputEmailAndPasswordExists() throws Exception {
        UserDto teacher = new UserDto();
        teacher.setEmail("test");
        teacher.setPassword("test");
        Teacher expectedTeacher = Teacher.builder().withEmail("test").build();
        when(teacherServiceMock.getByEmail(teacher.getEmail())).thenReturn(expectedTeacher);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/teachers/profile/").flashAttr("teacher",
                teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("userprofile"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("teacher"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", expectedTeacher));
    }

    @Test
    void testGetEditForm() throws Exception {
        UserDto teacher = new UserDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/teachers/edit/").flashAttr("teacher",
                teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/teacher"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("teacher"))
                .andExpect(MockMvcResultMatchers.model().attribute("teacher", teacher));
    }

    @Test
    void testEdit() throws Exception {
        UserDto teacher = new UserDto();
        teacher.setName("validName");
        teacher.setEmail("validEmail@mail.ru");
        teacher.setPhone("80000000000");
        teacher.setPassword("password");
        teacher.setConfirmPassword("password");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/teachers/update/").flashAttr("teacher",
                teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/teachers"));
        verify(teacherServiceMock).edit(teacher);
    }

    @Test
    void testEditWhenInputDifferentPasswords() throws Exception {
        UserDto teacher = new UserDto();
        teacher.setName("validName");
        teacher.setEmail("validEmail@mail.ru");
        teacher.setPhone("80000000000");
        teacher.setPassword("password");
        teacher.setConfirmPassword("different password");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/teachers/update/").flashAttr("teacher",
                teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/teacher"))
                .andExpect(MockMvcResultMatchers.model().hasErrors())
                .andExpect(MockMvcResultMatchers.model().attribute("message", "passwordmatch"));

    }

    @Test
    void testEditWhenInputEmailExistsForOutherTeacher() throws Exception {
        UserDto teacher = new UserDto();
        teacher.setSex(Sex.MALE);
        teacher.setName("validName");
        teacher.setEmail("existedEmail@mail.ru");
        teacher.setPhone("80000000000");
        teacher.setPassword("password");
        teacher.setConfirmPassword("password");
        MultipartFile mockFile = mock(MultipartFile.class);
        teacher.setPhoto(mockFile);
        doThrow(new EmailExistException("teacheremailexist")).when(teacherServiceMock).edit(teacher);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/teachers/update/").flashAttr("teacher",
                teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/teacher"))
                .andExpect(MockMvcResultMatchers.model().attribute("message", "teacheremailexist"));
    }

    @Test
    void testEditWhenInputInvalidPhoto() throws Exception {
        UserDto teacher = new UserDto();
        teacher.setSex(Sex.MALE);
        teacher.setName("validName");
        teacher.setEmail("existedEmail@mail.ru");
        teacher.setPhone("80000000000");
        teacher.setPassword("password");
        teacher.setConfirmPassword("password");
        MultipartFile mockFile = mock(MultipartFile.class);
        teacher.setPhoto(mockFile);
        doThrow(new InvalidPhotoException("Input file has invalid extension, it's not photo!")).when(photoServiceMock)
                .savePhoto(teacher);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/teachers/update/").flashAttr("teacher",
                teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/teacher")).andExpect(MockMvcResultMatchers
                .model().attribute("message", "Input file has invalid extension, it's not photo!"));
    }

    @Test
    void testEditWhenInputInvalidName() throws Exception {
        UserDto teacher = new UserDto();
        teacher.setSex(Sex.MALE);
        teacher.setName("   ");
        teacher.setEmail("existedEmail@mail.ru");
        teacher.setPhone("80000000000");
        teacher.setPassword("password");
        teacher.setConfirmPassword("password");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/teachers/update/").flashAttr("teacher",
                teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/teacher"))
                .andExpect(MockMvcResultMatchers.model().hasErrors());
    }

    @Test
    void testEditWhenInputInvalidEmail() throws Exception {
        UserDto teacher = new UserDto();
        teacher.setSex(Sex.MALE);
        teacher.setName("validName");
        teacher.setEmail("invalid email");
        teacher.setPhone("80000000000");
        teacher.setPassword("password");
        teacher.setConfirmPassword("password");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/teachers/update/").flashAttr("teacher",
                teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/teacher"))
                .andExpect(MockMvcResultMatchers.model().hasErrors());
    }

    @Test
    void testEditWhenInputInvalidPhone() throws Exception {
        UserDto teacher = new UserDto();
        teacher.setPhone("invalid phone");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/teachers/update/").flashAttr("teacher",
                teacher);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/teacher"))
                .andExpect(MockMvcResultMatchers.model().hasErrors());
    }    
}
