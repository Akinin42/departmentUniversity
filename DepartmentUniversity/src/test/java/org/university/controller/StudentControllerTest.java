package org.university.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
import org.university.dto.StudentDto;
import org.university.entity.Course;
import org.university.entity.Student;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EntityNotExistException;
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

    @InjectMocks
    private StudentController studentController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
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
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/addStudent/")
                .flashAttr("student", student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/students"));
        verify(studentServiceMock).register(student);
    }

    @Test
    void testDelete() throws Exception {
        StudentDto student = new StudentDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/delete/")
                .flashAttr("student", student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/students"));
        verify(studentServiceMock).delete(student);
    }

    @Test
    void testNewStudent() throws Exception {
        StudentDto student = new StudentDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/students/newStudent/");
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("studentform"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("student"))
                .andExpect(MockMvcResultMatchers.model().attribute("student", student));
    }

    @Test
    void testOtherStudentsWhenInputNumberNegativeAndShowFirstStudentsYet() throws Exception {
        List<Student> students = CreatorTestEntities.createStudents();
        when(studentServiceMock.findNumberOfUsers(5, 0)).thenReturn(students);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/students/other/").param("number", "-5").sessionAttr("numberUsers", 0);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("students"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("students"))
                .andExpect(MockMvcResultMatchers.model().attribute("students", students));
    }

    @Test
    void testOtherStudents() throws Exception {
        List<Student> nextStudents = CreatorTestEntities.createStudents();
        nextStudents.remove(1);
        when(studentServiceMock.findNumberOfUsers(5, 1)).thenReturn(nextStudents);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/students/other/").param("number", "1").sessionAttr("numberUsers", 0);
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
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/students/other/").param("number", "5").sessionAttr("numberUsers", 1);
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
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/addCourse/")
                .flashAttr("student", student);
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
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/deleteCourse/")
                .flashAttr("student", student);
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
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/login/")
                .flashAttr("student", student);
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
        when(studentServiceMock.login(student.getEmail(), student.getPassword())).thenThrow(EntityNotExistException.class);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/login/")
                .flashAttr("student", student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("studentform"));
    }
    
    @Test
    void testLoginWhenStudentWithInputEmailExistsButPasswordNotCorrect() throws Exception {
        StudentDto student = new StudentDto();
        student.setEmail("test");
        student.setPassword("incorrect password");        
        when(studentServiceMock.login(student.getEmail(), student.getPassword())).thenThrow(AuthorisationFailException.class);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/login/")
                .flashAttr("student", student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("passwordFailMessage"));
    }    
}
