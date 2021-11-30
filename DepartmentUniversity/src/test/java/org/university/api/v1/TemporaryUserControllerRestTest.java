package org.university.api.v1;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Locale;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.university.controller.GlobalExceptionHandler;
import org.university.dto.UserDto;
import org.university.entity.TemporaryUser;
import org.university.exceptions.EmailExistException;
import org.university.exceptions.InvalidPhotoException;
import org.university.service.PhotoService;
import org.university.service.TemporaryUserService;
import org.university.utils.CreatorTestEntities;
import org.university.utils.Sex;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class TemporaryUserControllerRestTest {

    private MockMvc mockMvc;
    
    private ObjectMapper mapper;

    @Mock
    private TemporaryUserService temporaryUserServiceMock;

    @Mock
    private PhotoService photoServiceMock;

    private TemporaryUserControllerRest temporaryUserController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        mapper = new ObjectMapper();
        temporaryUserController = new TemporaryUserControllerRest(temporaryUserServiceMock, photoServiceMock);
        mockMvc = MockMvcBuilders.standaloneSetup(temporaryUserController).setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }
    
    @Test
    void testGetAllUsers() throws Exception {
        List<TemporaryUser> users = CreatorTestEntities.createTemporaryUsers();
        when(temporaryUserServiceMock.findAllConfirmUser()).thenReturn(users);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/temporary")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is("First User")))
                .andExpect(jsonPath("$[1].name", is("Second User")))
                .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }
    
    @Test
    void testAddUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setSex(Sex.MALE);
        userDto.setName("validName");
        userDto.setEmail("validEmail@mail.ru");
        userDto.setPhone("80000000000");
        userDto.setPassword("password");
        userDto.setConfirmPassword("password");
        userDto.setPhotoName("photo name");
        userDto.setLocale(new Locale("en"));
        userDto.setConfirm(true);
        MockMultipartFile user = createMultipartFile(mapper.writeValueAsString(userDto), "user", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        userDto.setPhoto(photo);
        when(photoServiceMock.savePhoto(userDto)).thenReturn("photo name");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/temporary")
                .file(user)
                .file(photo)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isCreated());
        verify(temporaryUserServiceMock).register(userDto);
        verify(photoServiceMock).savePhoto(userDto);
    }

    @Test
    void testAddUserWhenInputDifferentPasswords() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("validName");
        userDto.setEmail("validEmail@mail.ru");
        userDto.setPhone("80000000000");
        userDto.setPassword("password");
        userDto.setConfirmPassword("different password");
        MockMultipartFile user = createMultipartFile(mapper.writeValueAsString(userDto), "user", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/temporary")
                .file(user)
                .file(photo)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(MethodArgumentNotValidException.class,
                exception.getResolvedException().getClass()))
                .andExpect(exception -> assertTrue(
                        exception.getResolvedException().getMessage().contains("passwordmatch")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddUserWhenInputInvalidName() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setSex(Sex.MALE);
        userDto.setName("   ");
        userDto.setEmail("existedEmail@mail.ru");
        userDto.setPhone("80000000000");
        userDto.setPassword("password");
        userDto.setConfirmPassword("password");
        MockMultipartFile user = createMultipartFile(mapper.writeValueAsString(userDto), "user", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/temporary")
                .file(user)
                .file(photo)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(MethodArgumentNotValidException.class,
                exception.getResolvedException().getClass()))
                .andExpect(exception -> assertTrue(
                        exception.getResolvedException().getMessage().contains("must not be blank")))
                .andExpect(exception -> assertTrue(exception.getResolvedException().getMessage().contains("name")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddUserWhenInputExistedEmail() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("validName");
        userDto.setEmail("existedEmail@mail.ru");
        userDto.setPhone("80000000000");
        userDto.setPassword("password");
        userDto.setConfirmPassword("password");
        userDto.setPhotoName("photo name");
        userDto.setLocale(new Locale("en"));
        userDto.setConfirm(true);
        MockMultipartFile user = createMultipartFile(mapper.writeValueAsString(userDto), "user", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        userDto.setPhoto(photo);
        when(photoServiceMock.savePhoto(userDto)).thenReturn("photo name");
        doThrow(new EmailExistException("useremailexist")).when(temporaryUserServiceMock).register(userDto);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/temporary")
                .file(user)
                .file(photo)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(
                exception.getResolvedException().getMessage(), "400 BAD_REQUEST \"useremailexist\""))                
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddUserWhenInputInvalidPhoto() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("validName");
        userDto.setEmail("validEmail@mail.ru");
        userDto.setPhone("80000000000");
        userDto.setPassword("password");
        userDto.setConfirmPassword("password");
        userDto.setLocale(new Locale("en"));
        userDto.setConfirm(true);
        MockMultipartFile user = createMultipartFile(mapper.writeValueAsString(userDto), "user", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        userDto.setPhoto(photo);
        doThrow(new InvalidPhotoException("Input file has invalid extension, it's not photo!")).when(photoServiceMock)
                .savePhoto(userDto);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/temporary")
                .file(user)
                .file(photo)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(
                exception.getResolvedException().getMessage(), "400 BAD_REQUEST \"Input file has invalid extension, it's not photo!\""))                
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddUserWhenInputInvalidEmail() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setSex(Sex.MALE);
        userDto.setName("validName");
        userDto.setEmail("invalid email");
        userDto.setPhone("80000000000");
        userDto.setPassword("password");
        userDto.setConfirmPassword("password");
        MockMultipartFile user = createMultipartFile(mapper.writeValueAsString(userDto), "user", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/temporary")
                .file(user)
                .file(photo)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(MethodArgumentNotValidException.class,
                exception.getResolvedException().getClass()))
                .andExpect(exception -> assertTrue(
                        exception.getResolvedException().getMessage().contains("must be a well-formed email address")))
                .andExpect(exception -> assertTrue(exception.getResolvedException().getMessage().contains("email")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddUserWhenInputInvalidPhone() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setPhone("invalid phone");
        MockMultipartFile user = createMultipartFile(mapper.writeValueAsString(userDto), "user", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/temporary")
                .file(user)
                .file(photo)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(MethodArgumentNotValidException.class,
                exception.getResolvedException().getClass()))
                .andExpect(exception -> assertTrue(
                        exception.getResolvedException().getMessage().contains("{phone.invalid}")))
                .andExpect(exception -> assertTrue(exception.getResolvedException().getMessage().contains("phone")))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testFindUserByEmailWhenUserWithInputEmailAndPasswordExists() throws Exception {
        TemporaryUser expectedUser = TemporaryUser.builder().withEmail("test").build();
        when(temporaryUserServiceMock.getByEmail("test")).thenReturn(expectedUser);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/temporary/test")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.email", is("test")));
    }    

    @Test
    void testEdit() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setSex(Sex.MALE);
        userDto.setName("validName");
        userDto.setEmail("validEmail@mail.ru");
        userDto.setPhone("80000000000");
        userDto.setPassword("password");
        userDto.setConfirmPassword("password");
        userDto.setPhotoName("photo name");
        userDto.setLocale(new Locale("en"));
        userDto.setConfirm(true);
        MockMultipartFile user = createMultipartFile(mapper.writeValueAsString(userDto), "user", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        userDto.setPhoto(photo);
        when(photoServiceMock.savePhoto(userDto)).thenReturn("photo name");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/temporary/update")
                .file(user)
                .file(photo)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk());
        verify(temporaryUserServiceMock).edit(userDto);
        verify(photoServiceMock).savePhoto(userDto);
    }

    @Test
    void testEditWhenInputDifferentPasswords() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("validName");
        userDto.setEmail("validEmail@mail.ru");
        userDto.setPhone("80000000000");
        userDto.setPassword("password");
        userDto.setConfirmPassword("different password");
        MockMultipartFile user = createMultipartFile(mapper.writeValueAsString(userDto), "user", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/temporary/update")
                .file(user)
                .file(photo)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(MethodArgumentNotValidException.class,
                exception.getResolvedException().getClass()))
                .andExpect(exception -> assertTrue(
                        exception.getResolvedException().getMessage().contains("passwordmatch")))
                .andExpect(status().isBadRequest());

    }

    @Test
    void testEditWhenInputEmailExistsForOutherUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("validName");
        userDto.setEmail("existedEmail@mail.ru");
        userDto.setPhone("80000000000");
        userDto.setPassword("password");
        userDto.setConfirmPassword("password");
        userDto.setPhotoName("photo name");
        userDto.setLocale(new Locale("en"));
        userDto.setConfirm(true);
        MockMultipartFile user = createMultipartFile(mapper.writeValueAsString(userDto), "user", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        userDto.setPhoto(photo);
        when(photoServiceMock.savePhoto(userDto)).thenReturn("photo name");
        doThrow(new EmailExistException("useremailexist")).when(temporaryUserServiceMock).edit(userDto);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/temporary/update")
                .file(user)
                .file(photo)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(
                exception.getResolvedException().getMessage(), "400 BAD_REQUEST \"useremailexist\""))                
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEditWhenInputInvalidPhoto() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("validName");
        userDto.setEmail("validEmail@mail.ru");
        userDto.setPhone("80000000000");
        userDto.setPassword("password");
        userDto.setConfirmPassword("password");
        userDto.setLocale(new Locale("en"));
        userDto.setConfirm(true);
        MockMultipartFile user = createMultipartFile(mapper.writeValueAsString(userDto), "user", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        userDto.setPhoto(photo);
        doThrow(new InvalidPhotoException("Input file has invalid extension, it's not photo!")).when(photoServiceMock)
                .savePhoto(userDto);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/temporary/update")
                .file(user)
                .file(photo)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(
                exception.getResolvedException().getMessage(), "400 BAD_REQUEST \"Input file has invalid extension, it's not photo!\""))                
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEditWhenInputInvalidName() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setSex(Sex.MALE);
        userDto.setName("   ");
        userDto.setEmail("existedEmail@mail.ru");
        userDto.setPhone("80000000000");
        userDto.setPassword("password");
        userDto.setConfirmPassword("password");
        MockMultipartFile user = createMultipartFile(mapper.writeValueAsString(userDto), "user", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/temporary/update")
                .file(user)
                .file(photo)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(MethodArgumentNotValidException.class,
                exception.getResolvedException().getClass()))
                .andExpect(exception -> assertTrue(
                        exception.getResolvedException().getMessage().contains("must not be blank")))
                .andExpect(exception -> assertTrue(exception.getResolvedException().getMessage().contains("name")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEditWhenInputInvalidEmail() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setSex(Sex.MALE);
        userDto.setName("validName");
        userDto.setEmail("invalid email");
        userDto.setPhone("80000000000");
        userDto.setPassword("password");
        userDto.setConfirmPassword("password");
        MockMultipartFile user = createMultipartFile(mapper.writeValueAsString(userDto), "user", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/temporary/update")
                .file(user)
                .file(photo)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(MethodArgumentNotValidException.class,
                exception.getResolvedException().getClass()))
                .andExpect(exception -> assertTrue(
                        exception.getResolvedException().getMessage().contains("must be a well-formed email address")))
                .andExpect(exception -> assertTrue(exception.getResolvedException().getMessage().contains("email")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEditWhenInputInvalidPhone() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setPhone("invalid phone");
        MockMultipartFile user = createMultipartFile(mapper.writeValueAsString(userDto), "user", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/temporary/update")
                .file(user)
                .file(photo)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(MethodArgumentNotValidException.class,
                exception.getResolvedException().getClass()))
                .andExpect(exception -> assertTrue(
                        exception.getResolvedException().getMessage().contains("{phone.invalid}")))
                .andExpect(exception -> assertTrue(exception.getResolvedException().getMessage().contains("phone")))
                .andExpect(status().isBadRequest());
    }
    
    private MockMultipartFile createMultipartFile(String originalContent, String requetsPart, String filename,
            String contentType) {
        return new MockMultipartFile(requetsPart, filename, contentType, originalContent.getBytes());
    }
}
