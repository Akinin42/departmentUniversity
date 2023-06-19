package org.university.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
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
import org.springframework.web.multipart.MultipartFile;
import org.university.dto.UserDto;
import org.university.entity.TemporaryUser;
import org.university.exceptions.EmailExistException;
import org.university.exceptions.InvalidPhotoException;
import org.university.service.PhotoService;
import org.university.service.TemporaryUserService;
import org.university.utils.CreatorTestEntities;
import org.university.utils.Sex;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class TemporaryUserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TemporaryUserService temporaryUserServiceMock;

    @Mock
    private PhotoService photoServiceMock;

    private TemporaryUserController temporaryUserController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        temporaryUserController = new TemporaryUserController(temporaryUserServiceMock, photoServiceMock);
        mockMvc = MockMvcBuilders.standaloneSetup(temporaryUserController).setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }
    
    @Test
    void testGetAllUsers() throws Exception {
        List<TemporaryUser> users = CreatorTestEntities.createTemporaryUsers();
        when(temporaryUserServiceMock.findAllConfirmUser()).thenReturn(users);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/temporary/");
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("requests"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("temporaryUsers"))
                .andExpect(MockMvcResultMatchers.model().attribute("temporaryUsers", users));
    }
    
    @Test
    void testAddUser() throws Exception {
        UserDto user = new UserDto();
        user.setSex(Sex.MALE);
        user.setName("validName");
        user.setEmail("validEmail@mail.ru");
        user.setPhone("80000000000");
        user.setPassword("password");
        user.setConfirmPassword("password");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/temporary/").flashAttr("user", user);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("mainmenu"));
        verify(temporaryUserServiceMock).register(user);
    }

    @Test
    void testAddUserWhenInputDifferentPasswords() throws Exception {
        UserDto user = new UserDto();
        user.setName("validName");
        user.setEmail("validEmail@mail.ru");
        user.setPhone("80000000000");
        user.setPassword("password");
        user.setConfirmPassword("different password");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/temporary/").flashAttr("user", user);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("userform"))
                .andExpect(MockMvcResultMatchers.model().hasErrors())
                .andExpect(MockMvcResultMatchers.model().attribute("message", "passwordmatch"));
    }

    @Test
    void testAddUserWhenInputInvalidName() throws Exception {
        UserDto user = new UserDto();
        user.setSex(Sex.MALE);
        user.setName("   ");
        user.setEmail("existedEmail@mail.ru");
        user.setPhone("80000000000");
        user.setPassword("password");
        user.setConfirmPassword("password");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/temporary").servletPath("/temporary")
                .flashAttr("user", user);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("userform"))
                .andExpect(MockMvcResultMatchers.model().hasErrors());
    }

    @Test
    void testAddUserWhenInputExistedEmail() throws Exception {
        UserDto user = new UserDto();
        user.setName("validName");
        user.setEmail("existedEmail@mail.ru");
        user.setPhone("80000000000");
        user.setPassword("password");
        user.setConfirmPassword("password");
        MultipartFile mockFile = mock(MultipartFile.class);
        user.setPhoto(mockFile);
        doThrow(new EmailExistException("teacheremailexist")).when(temporaryUserServiceMock).register(user);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/temporary").servletPath("/temporary")
                .flashAttr("user", user);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("userform"))
                .andExpect(MockMvcResultMatchers.model().attribute("message", "teacheremailexist"));
    }

    @Test
    void testAddUserWhenInputInvalidPhoto() throws Exception {
        UserDto user = new UserDto();
        user.setName("validName");
        user.setEmail("validEmail@mail.ru");
        user.setPhone("80000000000");
        user.setPassword("password");
        user.setConfirmPassword("password");
        MultipartFile mockFile = mock(MultipartFile.class);
        user.setPhoto(mockFile);
        doThrow(new InvalidPhotoException("Input file has invalid extension, it's not photo!")).when(photoServiceMock)
                .savePhoto(user);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/temporary").servletPath("/temporary")
                .flashAttr("user", user);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("userform")).andExpect(MockMvcResultMatchers.model()
                .attribute("message", "Input file has invalid extension, it's not photo!"));
    }

    @Test
    void testAddUserWhenInputInvalidEmail() throws Exception {
        UserDto user = new UserDto();
        user.setSex(Sex.MALE);
        user.setName("validName");
        user.setEmail("invalid email");
        user.setPhone("80000000000");
        user.setPassword("password");
        user.setConfirmPassword("password");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/temporary").servletPath("/temporary")
                .flashAttr("user", user);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("userform"))
                .andExpect(MockMvcResultMatchers.model().hasErrors());
    }

    @Test
    void testAddTeacherWhenInputInvalidPhone() throws Exception {
        UserDto user = new UserDto();
        user.setPhone("invalid phone");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/temporary").servletPath("/temporary")
                .flashAttr("user", user);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("userform"))
                .andExpect(MockMvcResultMatchers.model().hasErrors());
    }
    
    @Test
    void testGetProfileWhenUserWithInputEmailAndPasswordExists() throws Exception {
        UserDto user = new UserDto();
        user.setEmail("test");
        user.setPassword("test");
        TemporaryUser expectedUser = TemporaryUser.builder().withEmail("test").build();
        when(temporaryUserServiceMock.getByEmail(user.getEmail())).thenReturn(expectedUser);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/temporary/profile/").flashAttr("user", user);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("userprofile"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", expectedUser));
    }
    
    @Test
    void testGetEditForm() throws Exception {
        UserDto user = new UserDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/temporary/edit/").flashAttr("user",
                user);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/user"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", user));
    }

    @Test
    void testEdit() throws Exception {
        UserDto user = new UserDto();
        user.setName("validName");
        user.setEmail("validEmail@mail.ru");
        user.setPhone("80000000000");
        user.setPassword("password");
        user.setConfirmPassword("password");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/temporary/update/").flashAttr("user",
                user);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("userprofile"));
        verify(temporaryUserServiceMock).edit(user);
    }

    @Test
    void testEditWhenInputDifferentPasswords() throws Exception {
        UserDto user = new UserDto();
        user.setName("validName");
        user.setEmail("validEmail@mail.ru");
        user.setPhone("80000000000");
        user.setPassword("password");
        user.setConfirmPassword("different password");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/temporary/update/").flashAttr("user",
                user);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/user"))
                .andExpect(MockMvcResultMatchers.model().hasErrors())
                .andExpect(MockMvcResultMatchers.model().attribute("message", "passwordmatch"));

    }

    @Test
    void testEditWhenInputEmailExistsForOutherUser() throws Exception {
        UserDto user = new UserDto();
        user.setSex(Sex.MALE);
        user.setName("validName");
        user.setEmail("existedEmail@mail.ru");
        user.setPhone("80000000000");
        user.setPassword("password");
        user.setConfirmPassword("password");
        MultipartFile mockFile = mock(MultipartFile.class);
        user.setPhoto(mockFile);
        doThrow(new EmailExistException("teacheremailexist")).when(temporaryUserServiceMock).edit(user);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/temporary/update/").flashAttr("user",
                user);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/user"))
                .andExpect(MockMvcResultMatchers.model().attribute("message", "teacheremailexist"));
    }

    @Test
    void testEditWhenInputInvalidPhoto() throws Exception {
        UserDto user = new UserDto();
        user.setSex(Sex.MALE);
        user.setName("validName");
        user.setEmail("existedEmail@mail.ru");
        user.setPhone("80000000000");
        user.setPassword("password");
        user.setConfirmPassword("password");
        MultipartFile mockFile = mock(MultipartFile.class);
        user.setPhoto(mockFile);
        doThrow(new InvalidPhotoException("Input file has invalid extension, it's not photo!")).when(photoServiceMock)
                .savePhoto(user);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/temporary/update/").flashAttr("user",
                user);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/user")).andExpect(MockMvcResultMatchers
                .model().attribute("message", "Input file has invalid extension, it's not photo!"));
    }

    @Test
    void testEditWhenInputInvalidName() throws Exception {
        UserDto user = new UserDto();
        user.setSex(Sex.MALE);
        user.setName("   ");
        user.setEmail("existedEmail@mail.ru");
        user.setPhone("80000000000");
        user.setPassword("password");
        user.setConfirmPassword("password");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/temporary/update/").flashAttr("user",
                user);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/user"))
                .andExpect(MockMvcResultMatchers.model().hasErrors());
    }

    @Test
    void testEditWhenInputInvalidEmail() throws Exception {
        UserDto user = new UserDto();
        user.setSex(Sex.MALE);
        user.setName("validName");
        user.setEmail("invalid email");
        user.setPhone("80000000000");
        user.setPassword("password");
        user.setConfirmPassword("password");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/temporary/update/").flashAttr("user",
                user);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/user"))
                .andExpect(MockMvcResultMatchers.model().hasErrors());
    }

    @Test
    void testEditWhenInputInvalidPhone() throws Exception {
        UserDto user = new UserDto();
        user.setPhone("invalid phone");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/temporary/update/").flashAttr("user",
                user);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/user"))
                .andExpect(MockMvcResultMatchers.model().hasErrors());
    }
    
    @Test
    void testNewUser() throws Exception {
        UserDto teacher = new UserDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/temporary/new/");
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("userform"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", teacher));
    }

}
