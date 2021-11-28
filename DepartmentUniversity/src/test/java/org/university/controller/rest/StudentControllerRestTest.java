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
import org.university.dto.StudentDto;
import org.university.entity.Course;
import org.university.entity.Student;
import org.university.exceptions.EmailExistException;
import org.university.exceptions.InvalidPhotoException;
import org.university.service.CourseService;
import org.university.service.PhotoService;
import org.university.service.StudentService;
import org.university.utils.CreatorTestEntities;
import org.university.utils.Sex;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class StudentControllerRestTest {

    private MockMvc mockMvc;

    private ObjectMapper mapper;

    @Mock
    private StudentService studentServiceMock;

    @Mock
    private CourseService courseServiceMock;

    @Mock
    private PhotoService photoServiceMock;

    private StudentControllerRest studentController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        mapper = new ObjectMapper();
        studentController = new StudentControllerRest(studentServiceMock, courseServiceMock, photoServiceMock);
        mockMvc = MockMvcBuilders.standaloneSetup(studentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetStudentsWhenInputNumberNegativeAndShowFirstStudentsYet() throws Exception {
        List<Student> students = CreatorTestEntities.createStudents();
        when(studentServiceMock.findNumberOfUsers(5, 0)).thenReturn(students);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/students?page=-1&size=5")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[4].name", is("John Brown")))
                .andExpect(jsonPath("$[5].name", is("Pol Hardy")))
                .andExpect(jsonPath("$", Matchers.hasSize(6)));
    }

    @Test
    void testGetStudents() throws Exception {
        List<Student> nextStudents = CreatorTestEntities.createStudents();
        nextStudents.remove(1);
        nextStudents.remove(0);
        when(studentServiceMock.findNumberOfUsers(5, 1)).thenReturn(nextStudents);
        when(studentServiceMock.findAll()).thenReturn(nextStudents);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/students?page=1&size=5")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[2].name", is("John Brown")))
                .andExpect(jsonPath("$[3].name", is("Pol Hardy")))
                .andExpect(jsonPath("$", Matchers.hasSize(4)));
    }

    @Test
    void testGetStudentsWhenNextStudentNotExist() throws Exception {
        List<Student> students = CreatorTestEntities.createStudents();
        students.remove(1);
        when(studentServiceMock.findNumberOfUsers(5, 0)).thenReturn(students);
        when(studentServiceMock.findAll()).thenReturn(students);
        when(studentServiceMock.findNumberOfUsers(5, 1)).thenReturn(new ArrayList<Student>());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/students?page=1&size=5")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[3].name", is("John Brown")))
                .andExpect(jsonPath("$[4].name", is("Pol Hardy")))
                .andExpect(jsonPath("$", Matchers.hasSize(5)));
    }

    @Test
    void testDelete() throws Exception {
        StudentDto student = new StudentDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(student));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk());
        verify(studentServiceMock).delete(student);
    }

    @Test
    void testAddCourse() throws Exception {
        StudentDto student = new StudentDto();
        student.setCourseName("test");
        Course course = Course.builder().withName("test").build();
        when(courseServiceMock.createCourse("test")).thenReturn(course);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/students/course")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(student));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk());
        verify(studentServiceMock).addStudentToCourse(student, course);
    }

    @Test
    void testDeleteCourse() throws Exception {
        StudentDto student = new StudentDto();
        student.setCourseName("test");
        Course course = Course.builder().withName("test").build();
        when(courseServiceMock.createCourse("test")).thenReturn(course);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/api/v1/students/course")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(student));
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk());
        verify(studentServiceMock).deleteStudentFromCourse(student, course);
    }

    @Test
    void testfFindStudentByEmailWhenStudentWithInputEmailAndPasswordExists() throws Exception {
        Student expectedStudent = Student.builder().withEmail("test").build();
        when(studentServiceMock.getByEmail("test")).thenReturn(expectedStudent);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/students/test")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.email", is("test")));
    }

    @Test
    void testEdit() throws Exception {
        StudentDto studentDto = new StudentDto();
        studentDto.setName("validName");
        studentDto.setEmail("validEmail@mail.ru");
        studentDto.setPhone("80000000000");
        studentDto.setPassword("password");
        studentDto.setConfirmPassword("password");
        studentDto.setPhotoName("photo name");
        MockMultipartFile student = createMultipartFile(mapper.writeValueAsString(studentDto), "student", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        studentDto.setPhoto(photo);
        when(photoServiceMock.savePhoto(studentDto)).thenReturn("photo name");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/students")
                .file(student)
                .file(photo)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk());
        verify(studentServiceMock).edit(studentDto);
        verify(photoServiceMock).savePhoto(studentDto);
    }

    @Test
    void testEditWhenInputDifferentPasswords() throws Exception {
        StudentDto studentDto = new StudentDto();
        studentDto.setName("validName");
        studentDto.setEmail("validEmail@mail.ru");
        studentDto.setPhone("80000000000");
        studentDto.setPassword("password");
        studentDto.setConfirmPassword("different passwords");
        MockMultipartFile student = createMultipartFile(mapper.writeValueAsString(studentDto), "student", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/students")
                .file(student)
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
    void testEditWhenInputEmailExistsForOutherStudent() throws Exception {
        StudentDto studentDto = new StudentDto();
        studentDto.setSex(Sex.MALE);
        studentDto.setName("validName");
        studentDto.setEmail("existedEmail@mail.ru");
        studentDto.setPhone("80000000000");
        studentDto.setPassword("password");
        studentDto.setConfirmPassword("password");
        studentDto.setPhotoName("photo name");
        MockMultipartFile student = createMultipartFile(mapper.writeValueAsString(studentDto), "student", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        studentDto.setPhoto(photo);
        when(photoServiceMock.savePhoto(studentDto)).thenReturn("photo name");
        doThrow(new EmailExistException("studentemailexist")).when(studentServiceMock).edit(studentDto);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/students")
                .file(student)
                .file(photo)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(
                exception.getResolvedException().getMessage(), "400 BAD_REQUEST \"studentemailexist\""))                
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEditWhenInputInvalidPhoto() throws Exception {
        StudentDto studentDto = new StudentDto();
        studentDto.setSex(Sex.MALE);
        studentDto.setName("validName");
        studentDto.setEmail("existedEmail@mail.ru");
        studentDto.setPhone("80000000000");
        studentDto.setPassword("password");
        studentDto.setConfirmPassword("password");
        MockMultipartFile student = createMultipartFile(mapper.writeValueAsString(studentDto), "student", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        studentDto.setPhoto(photo);
        doThrow(new InvalidPhotoException("Input file has invalid extension, it's not photo!")).when(photoServiceMock)
                .savePhoto(studentDto);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/students")
                .file(student)
                .file(photo)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(
                exception.getResolvedException().getMessage(), "400 BAD_REQUEST \"Input file has invalid extension, it's not photo!\""))                
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEditWhenInputInvalidName() throws Exception {
        StudentDto studentDto = new StudentDto();
        studentDto.setSex(Sex.MALE);
        studentDto.setName("   ");
        studentDto.setEmail("existedEmail@mail.ru");
        studentDto.setPhone("80000000000");
        studentDto.setPassword("password");
        studentDto.setConfirmPassword("password");
        MockMultipartFile student = createMultipartFile(mapper.writeValueAsString(studentDto), "student", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/students")
                .file(student)
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
        StudentDto studentDto = new StudentDto();
        studentDto.setSex(Sex.MALE);
        studentDto.setName("validName");
        studentDto.setEmail("invalid email");
        studentDto.setPhone("80000000000");
        studentDto.setPassword("password");
        studentDto.setConfirmPassword("password");
        MockMultipartFile student = createMultipartFile(mapper.writeValueAsString(studentDto), "student", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/students")
                .file(student)
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
        StudentDto studentDto = new StudentDto();
        studentDto.setPhone("invalid phone");
        MockMultipartFile student = createMultipartFile(mapper.writeValueAsString(studentDto), "student", "",
                "application/json");
        MockMultipartFile photo = createMultipartFile("", "photo", "photo name", "image/jpeg");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/api/v1/students")
                .file(student)
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
    void testShouldReturnNotFoundedStatusWhenPageNotMapping() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/notexistedpage")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.getDispatcherServlet().setThrowExceptionIfNoHandlerFound(true);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isNotFound());
    }

    @Test
    void testShouldReturnServerExceptionWhenOutherExcetion() throws Exception {
        doThrow(new IllegalArgumentException()).when(studentServiceMock).findNumberOfUsers(5, 1);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/students?page=1&size=5")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().is5xxServerError());
    }

    private MockMultipartFile createMultipartFile(String originalContent, String requetsPart, String filename,
            String contentType) {
        return new MockMultipartFile(requetsPart, filename, contentType, originalContent.getBytes());
    }
}
