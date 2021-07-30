package org.university.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

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
import org.university.dto.StudentDto;
import org.university.entity.Course;
import org.university.entity.Student;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EntityNotExistException;
import org.university.exceptions.InvalidEmailException;
import org.university.exceptions.InvalidPhoneException;
import org.university.exceptions.InvalidUserNameException;
import org.university.service.AwsS3Service;
import org.university.service.CourseService;
import org.university.service.StudentService;
import org.university.utils.CreatorTestEntities;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class StudentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StudentService studentServiceMock;

    @Mock
    private CourseService courseServiceMock;
    
    @Mock
    private AwsS3Service awsS3ServiceMock;

    private StudentController studentController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        studentController = new StudentController(studentServiceMock, courseServiceMock, awsS3ServiceMock);
        mockMvc = MockMvcBuilders.standaloneSetup(studentController).build();
    }

    @Test
    void testGetStudents() throws Exception {
        List<Student> students = CreatorTestEntities.createStudents();
        List<Course> courses = CreatorTestEntities.createCourses();
        when(studentServiceMock.findNumberOfUsers(5, 0)).thenReturn(students);
        when(courseServiceMock.findAllCourses()).thenReturn(courses);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/students/");
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("students"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("students"))
                .andExpect(MockMvcResultMatchers.model().attribute("students", students))
                .andExpect(MockMvcResultMatchers.model().attributeExists("courses"))
                .andExpect(MockMvcResultMatchers.model().attribute("courses", courses));
    }

    @Test
    void testAddStudent() throws Exception {
        StudentDto student = new StudentDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/").flashAttr("student", student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/students"));
        verify(studentServiceMock).register(student);
    }

    @Test
    void testAddStudentWhenInputInvalidName() throws Exception {
        StudentDto student = new StudentDto();
        student.setName("invalid name");
        doThrow(new InvalidUserNameException("Input name isn't valid!")).when(studentServiceMock).register(student);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/").flashAttr("student", student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("studentform"))
                .andExpect(MockMvcResultMatchers.model().attribute("message", "Input name isn't valid!"));
    }
    
    @Test
    void testAddStudentWhenInputInvalidEmail() throws Exception {
        StudentDto student = new StudentDto();
        student.setEmail("invalid email");
        doThrow(new InvalidEmailException("Input email isn't valid!")).when(studentServiceMock).register(student);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/").flashAttr("student", student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("studentform"))
        .andExpect(MockMvcResultMatchers.model().attribute("message", "Input email isn't valid!"));
    }
    
    @Test
    void testAddStudentWhenInputInvalidPhone() throws Exception {
        StudentDto student = new StudentDto();
        student.setEmail("invalid email");
        doThrow(new InvalidPhoneException("Input phone isn't valid!")).when(studentServiceMock).register(student);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/").flashAttr("student", student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("studentform"))
        .andExpect(MockMvcResultMatchers.model().attribute("message", "Input phone isn't valid!"));
    }

    @Test
    void testDelete() throws Exception {
        StudentDto student = new StudentDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/students/").flashAttr("student",
                student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/students"));
        verify(studentServiceMock).delete(student);
    }

    @Test
    void testNewStudent() throws Exception {
        StudentDto student = new StudentDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/students/new/");
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("studentform"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("student"))
                .andExpect(MockMvcResultMatchers.model().attribute("student", student));
    }

    @Test
    void testOtherStudentsWhenInputNumberNegativeAndShowFirstStudentsYet() throws Exception {
        List<Student> students = CreatorTestEntities.createStudents();
        when(studentServiceMock.findNumberOfUsers(5, 0)).thenReturn(students);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/students/-1").sessionAttr("numberUsers",
                0);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("students"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("students"))
                .andExpect(MockMvcResultMatchers.model().attribute("students", students));
    }

    @Test
    void testOtherStudents() throws Exception {
        List<Student> nextStudents = CreatorTestEntities.createStudents();
        nextStudents.remove(1);
        when(studentServiceMock.findNumberOfUsers(5, 5)).thenReturn(nextStudents);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/students/1").sessionAttr("numberUsers", 0);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("students"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("students"))
                .andExpect(MockMvcResultMatchers.model().attribute("students", nextStudents));
    }

    @Test
    void testOtherStudentsWhenNextStudentNotExist() throws Exception {
        List<Student> students = CreatorTestEntities.createStudents();
        when(studentServiceMock.findNumberOfUsers(5, 1)).thenReturn(students);
        when(studentServiceMock.findNumberOfUsers(5, 6)).thenReturn(new ArrayList<Student>());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/students/1").sessionAttr("numberUsers", 1);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("students"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("students"))
                .andExpect(MockMvcResultMatchers.model().attribute("students", students));
    }

    @Test
    void testAddCourse() throws Exception {
        StudentDto student = new StudentDto();
        student.setCourseName("test");
        Course course = Course.builder().withName("test").build();
        when(courseServiceMock.createCourse("test")).thenReturn(course);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/course/").flashAttr("student",
                student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/students"));
        verify(studentServiceMock).addStudentToCourse(student, course);
    }

    @Test
    void testDeleteCourse() throws Exception {
        StudentDto student = new StudentDto();
        student.setCourseName("test");
        Course course = Course.builder().withName("test").build();
        when(courseServiceMock.createCourse("test")).thenReturn(course);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/students/course/").flashAttr("student",
                student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/students"));
        verify(studentServiceMock).deleteStudentFromCourse(student, course);
    }

    @Test
    void testLoginWhenStudentWithInputEmailAndPasswordExists() throws Exception {
        StudentDto student = new StudentDto();
        student.setEmail("test");
        student.setPassword("test");
        Student expectedStudent = Student.builder().withEmail("test").build();
        when(studentServiceMock.login(student.getEmail(), student.getPassword())).thenReturn(expectedStudent);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/login/").flashAttr("student",
                student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("studentprofile"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("student"))
                .andExpect(MockMvcResultMatchers.model().attribute("student", expectedStudent));
    }

    @Test
    void testLoginWhenStudentWithInputEmailNotExists() throws Exception {
        StudentDto student = new StudentDto();
        student.setEmail("notexistemail");
        student.setPassword("test");
        when(studentServiceMock.login(student.getEmail(), student.getPassword()))
                .thenThrow(EntityNotExistException.class);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/login/").flashAttr("student",
                student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("studentform"));
    }

    @Test
    void testLoginWhenStudentWithInputEmailExistsButPasswordNotCorrect() throws Exception {
        StudentDto student = new StudentDto();
        student.setEmail("test");
        student.setPassword("incorrect password");
        when(studentServiceMock.login(student.getEmail(), student.getPassword()))
                .thenThrow(AuthorisationFailException.class);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/login/").flashAttr("student",
                student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/students"));
    }
    
    @Test
    void testGetEditForm() throws Exception {
        StudentDto student = new StudentDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/edit/")
                .flashAttr("student", student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/student"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("student"))
                .andExpect(MockMvcResultMatchers.model().attribute("student", student));
    }
    
    @Test
    void testEdit() throws Exception {
        StudentDto student = new StudentDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/students/")
                .flashAttr("student", student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/students"));
        verify(studentServiceMock).edit(student);
    }
    
    @Test
    void testEditWhenInputInvalidName() throws Exception {
        StudentDto student = new StudentDto();
        student.setName("invalid name");
        doThrow(new InvalidUserNameException("Input name isn't valid!")).when(studentServiceMock).edit(student);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/students/").flashAttr("student", student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/student"))
                .andExpect(MockMvcResultMatchers.model().attribute("message", "Input name isn't valid!"));
    }
    
    @Test
    void testEditWhenInputInvalidEmail() throws Exception {
        StudentDto student = new StudentDto();
        student.setEmail("invalid email");
        doThrow(new InvalidEmailException("Input email isn't valid!")).when(studentServiceMock).edit(student);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/students/").flashAttr("student", student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/student"))
        .andExpect(MockMvcResultMatchers.model().attribute("message", "Input email isn't valid!"));
    }
    
    @Test
    void testEditWhenInputInvalidPhone() throws Exception {
        StudentDto student = new StudentDto();
        student.setEmail("invalid email");
        doThrow(new InvalidPhoneException("Input phone isn't valid!")).when(studentServiceMock).edit(student);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch("/students/").flashAttr("student", student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/student"))
        .andExpect(MockMvcResultMatchers.model().attribute("message", "Input phone isn't valid!"));
    }
}
