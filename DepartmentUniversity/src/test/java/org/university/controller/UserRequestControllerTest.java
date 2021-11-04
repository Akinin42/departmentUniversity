package org.university.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.university.dto.UserDto;
import org.university.entity.TemporaryUser;
import org.university.service.StudentService;
import org.university.service.TeacherService;
import org.university.service.TemporaryUserService;
import org.university.utils.CreatorTestEntities;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class UserRequestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TeacherService teacherServiceMock;

    @Mock
    private TemporaryUserService temporaryUserServiceMock;

    @Mock
    private StudentService studentServiceMock;

    private UserRequestController userRequestController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        userRequestController = new UserRequestController(studentServiceMock, teacherServiceMock,
                temporaryUserServiceMock);
        mockMvc = MockMvcBuilders.standaloneSetup(userRequestController)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void registerUserTestWhenDesiredRoleStudent() throws Exception {
        UserDto user = new UserDto();
        user.setEmail("test@mail.ru");
        user.setDesiredRole("STUDENT");
        TemporaryUser userFromDb = TemporaryUser.builder().withId(1).withEmail("test@mail.ru")
                .withDesiredRole(CreatorTestEntities.createRoles().get(0)).build();
        when(temporaryUserServiceMock.getByEmail("test@mail.ru")).thenReturn(userFromDb);
        when(temporaryUserServiceMock.mapEntityToDto(userFromDb)).thenReturn(user);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/requests").flashAttr("user", user);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/temporary"));
        verify(studentServiceMock).register(user);
        verify(temporaryUserServiceMock).delete(user);
    }

    @Test
    void registerUserTestWhenDesiredRoleTeacher() throws Exception {
        UserDto user = new UserDto();
        user.setEmail("test@mail.ru");
        user.setDesiredRole("TEACHER");
        TemporaryUser userFromDb = TemporaryUser.builder().withId(1).withEmail("test@mail.ru")
                .withDesiredRole(CreatorTestEntities.createRoles().get(1)).build();
        when(temporaryUserServiceMock.getByEmail("test@mail.ru")).thenReturn(userFromDb);
        when(temporaryUserServiceMock.mapEntityToDto(userFromDb)).thenReturn(user);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/requests").flashAttr("user", user);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/temporary"));
        verify(teacherServiceMock).register(user);
        verify(temporaryUserServiceMock).delete(user);
    }

    @Test
    void unconfirmUserTest() throws Exception {
        UserDto user = new UserDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/requests/unconfirm").flashAttr("user",
                user);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/temporary"));
        verify(temporaryUserServiceMock).addConfirmDescription(user);
    }
}
