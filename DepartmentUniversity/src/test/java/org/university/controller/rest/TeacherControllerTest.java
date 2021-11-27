package org.university.controller.rest;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
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
import org.university.entity.Teacher;
import org.university.exceptions.EmailExistException;
import org.university.exceptions.InvalidPhotoException;
import org.university.service.PhotoService;
import org.university.service.TeacherService;
import org.university.utils.CreatorTestEntities;
import org.university.utils.Sex;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class TeacherControllerTest {

    private MockMvc mockMvc;
    
    private ObjectMapper mapper;

    @Mock
    private TeacherService teacherServiceMock;

    @Mock
    private PhotoService photoServiceMock;

    private TeacherControllerRest teacherController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        mapper = new ObjectMapper();
        teacherController = new TeacherControllerRest(teacherServiceMock, photoServiceMock);
        mockMvc = MockMvcBuilders.standaloneSetup(teacherController).setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }
    
    @Test
    void testGetTeachersWhenInputNumberNegativeAndShowFirstTeachersYet() throws Exception {
        List<Teacher> teachers = CreatorTestEntities.createTeachers();
        when(teacherServiceMock.findNumberOfUsers(5, 0)).thenReturn(teachers);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/teachers?page=-1&size=5")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is("Bob Moren")))
                .andExpect(jsonPath("$[1].name", is("Ann Moren")))
                .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }

    @Test
    void testGetTeachers() throws Exception {
        List<Teacher> nextTeachers = CreatorTestEntities.createTeachers();
        nextTeachers.remove(1);
        when(teacherServiceMock.findNumberOfUsers(5, 1)).thenReturn(nextTeachers);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/teachers?page=1&size=5")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is("Bob Moren")))
                .andExpect(jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    void testGetTeachersWhenNextTeacherNotExist() throws Exception {
        List<Teacher> teachers = CreatorTestEntities.createTeachers();
        teachers.remove(1);
        when(teacherServiceMock.findNumberOfUsers(5, 0)).thenReturn(teachers);
        when(teacherServiceMock.findAll()).thenReturn(teachers);
        when(teacherServiceMock.findNumberOfUsers(5, 1)).thenReturn(new ArrayList<Teacher>());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/teachers?page=1&size=5")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is("Bob Moren")))
                .andExpect(jsonPath("$", Matchers.hasSize(1)));
    }



    @Test
    void testDelete() throws Exception {
        UserDto teacher = new UserDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/api/v1/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(teacher));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk());
        verify(teacherServiceMock).delete(teacher);
    }

    @Test
    void testFindTeacherByEmailWhenTeacherWithInputEmailAndPasswordExists() throws Exception {
        Teacher expectedTeacher = Teacher.builder().withEmail("test").build();
        when(teacherServiceMock.getByEmail("test")).thenReturn(expectedTeacher);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/teachers/test")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.email", is("test")));
    }

    @Test
    void testEdit() throws Exception {
        UserDto teacherDto = new UserDto();
        teacherDto.setName("validName");
        teacherDto.setEmail("validEmail@mail.ru");
        teacherDto.setPhone("80000000000");
        teacherDto.setPassword("password");
        teacherDto.setConfirmPassword("password");
        teacherDto.setPhotoName("photo name");
        MockMultipartFile teacher = createMultipartFile(mapper.writeValueAsString(teacherDto), "teacher", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        teacherDto.setPhoto(photo);
        when(photoServiceMock.savePhoto(teacherDto)).thenReturn("photo name");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/teachers")
                .file(teacher)
                .file(photo)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk());
        verify(teacherServiceMock).edit(teacherDto);
        verify(photoServiceMock).savePhoto(teacherDto);
    }

    @Test
    void testEditWhenInputDifferentPasswords() throws Exception {
        UserDto teacherDto = new UserDto();
        teacherDto.setName("validName");
        teacherDto.setEmail("validEmail@mail.ru");
        teacherDto.setPhone("80000000000");
        teacherDto.setPassword("password");
        teacherDto.setConfirmPassword("different password");
        MockMultipartFile teacher = createMultipartFile(mapper.writeValueAsString(teacherDto), "teacher", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/teachers")
                .file(teacher)
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
    void testEditWhenInputEmailExistsForOutherTeacher() throws Exception {
        UserDto teacherDto = new UserDto();
        teacherDto.setSex(Sex.MALE);
        teacherDto.setName("validName");
        teacherDto.setEmail("existedEmail@mail.ru");
        teacherDto.setPhone("80000000000");
        teacherDto.setPassword("password");
        teacherDto.setConfirmPassword("password");
        teacherDto.setPhotoName("photo name");
        MockMultipartFile teacher = createMultipartFile(mapper.writeValueAsString(teacherDto), "teacher", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        teacherDto.setPhoto(photo);
        when(photoServiceMock.savePhoto(teacherDto)).thenReturn("photo name");
        doThrow(new EmailExistException("teacheremailexist")).when(teacherServiceMock).edit(teacherDto);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/teachers")
                .file(teacher)
                .file(photo)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(
                exception.getResolvedException().getMessage(), "400 BAD_REQUEST \"teacheremailexist\""))                
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEditWhenInputInvalidPhoto() throws Exception {
        UserDto teacherDto = new UserDto();
        teacherDto.setSex(Sex.MALE);
        teacherDto.setName("validName");
        teacherDto.setEmail("existedEmail@mail.ru");
        teacherDto.setPhone("80000000000");
        teacherDto.setPassword("password");
        teacherDto.setConfirmPassword("password");
        MockMultipartFile teacher = createMultipartFile(mapper.writeValueAsString(teacherDto), "teacher", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        teacherDto.setPhoto(photo);
        doThrow(new InvalidPhotoException("Input file has invalid extension, it's not photo!")).when(photoServiceMock)
                .savePhoto(teacherDto);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/teachers")
                .file(teacher)
                .file(photo)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(
                exception.getResolvedException().getMessage(), "400 BAD_REQUEST \"Input file has invalid extension, it's not photo!\""))                
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEditWhenInputInvalidName() throws Exception {
        UserDto teacherDto = new UserDto();
        teacherDto.setSex(Sex.MALE);
        teacherDto.setName("   ");
        teacherDto.setEmail("existedEmail@mail.ru");
        teacherDto.setPhone("80000000000");
        teacherDto.setPassword("password");
        teacherDto.setConfirmPassword("password");
        MockMultipartFile teacher = createMultipartFile(mapper.writeValueAsString(teacherDto), "teacher", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/teachers")
                .file(teacher)
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
        UserDto teacherDto = new UserDto();
        teacherDto.setSex(Sex.MALE);
        teacherDto.setName("validName");
        teacherDto.setEmail("invalid email");
        teacherDto.setPhone("80000000000");
        teacherDto.setPassword("password");
        teacherDto.setConfirmPassword("password");
        MockMultipartFile teacher = createMultipartFile(mapper.writeValueAsString(teacherDto), "teacher", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/teachers")
                .file(teacher)
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
        UserDto teacherDto = new UserDto();
        teacherDto.setPhone("invalid phone");
        MockMultipartFile teacher = createMultipartFile(mapper.writeValueAsString(teacherDto), "teacher", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/teachers")
                .file(teacher)
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
