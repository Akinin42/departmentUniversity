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

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class StudentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StudentService studentServiceMock;

    @Mock
    private CourseService courseServiceMock;

    @Mock
    private PhotoService photoServiceMock;

    private StudentController studentController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        studentController = new StudentController(studentServiceMock, courseServiceMock, photoServiceMock);
        mockMvc = MockMvcBuilders.standaloneSetup(studentController).setControllerAdvice(new GlobalExceptionHandler())
                .build();
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
    void testGetStudentsWhenNumberUsersNotNull() throws Exception {
        List<Student> students = CreatorTestEntities.createStudents();
        List<Course> courses = CreatorTestEntities.createCourses();
        when(studentServiceMock.findNumberOfUsers(10, 0)).thenReturn(students);
        when(courseServiceMock.findAllCourses()).thenReturn(courses);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/students/").sessionAttr("numberUsers", 10);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("students"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("students"))
                .andExpect(MockMvcResultMatchers.model().attribute("students", students))
                .andExpect(MockMvcResultMatchers.model().attributeExists("courses"))
                .andExpect(MockMvcResultMatchers.model().attribute("courses", courses));
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
    void testOtherStudentsWhenInputNumberNegativeAndShowFirstStudentsYet() throws Exception {
        List<Student> students = CreatorTestEntities.createStudents();
        when(studentServiceMock.findNumberOfUsers(5, 0)).thenReturn(students);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/students/-1").sessionAttr("pagesNumber", 0)
                .sessionAttr("numberUsers", 5);
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
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/students/1").sessionAttr("pagesNumber", 0)
                .sessionAttr("numberUsers", 5);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("students"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("students"))
                .andExpect(MockMvcResultMatchers.model().attribute("students", nextStudents));
    }

    @Test
    void testOtherStudentsWhenNextStudentNotExist() throws Exception {
        List<Student> students = CreatorTestEntities.createStudents();
        when(studentServiceMock.findNumberOfUsers(5, 1)).thenReturn(students);
        when(studentServiceMock.findNumberOfUsers(5, 2)).thenReturn(new ArrayList<Student>());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/students/1").sessionAttr("pagesNumber", 1)
                .sessionAttr("numberUsers", 5);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("students"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("students"))
                .andExpect(MockMvcResultMatchers.model().attribute("students", students));
    }

    @Test
    void testSetNumberUsers() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/students/numbers/10");
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/students"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("numberUsers"))
                .andExpect(MockMvcResultMatchers.model().attribute("numberUsers", 10));
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
    void testGetProfileWhenStudentWithInputEmailAndPasswordExists() throws Exception {
        StudentDto student = new StudentDto();
        student.setEmail("test");
        student.setPassword("test");
        Student expectedStudent = Student.builder().withEmail("test").build();
        when(studentServiceMock.getByEmail(student.getEmail())).thenReturn(expectedStudent);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/profile/").flashAttr("student",
                student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("userprofile"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", expectedStudent));
    }

    @Test
    void testGetEditForm() throws Exception {
        StudentDto student = new StudentDto();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/edit/").flashAttr("student",
                student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/student"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("student"))
                .andExpect(MockMvcResultMatchers.model().attribute("student", student));
    }

    @Test
    void testEdit() throws Exception {
        StudentDto student = new StudentDto();
        student.setName("validName");
        student.setEmail("validEmail@mail.ru");
        student.setPhone("80000000000");
        student.setPassword("password");
        student.setConfirmPassword("password");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/update/").flashAttr("student",
                student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/students"));
        verify(studentServiceMock).edit(student);
        verify(photoServiceMock).savePhoto(student);
    }
    
    @Test
    void testEditWhenInputDifferentPasswords() throws Exception {
        StudentDto student = new StudentDto();
        student.setName("validName");
        student.setEmail("validEmail@mail.ru");
        student.setPhone("80000000000");
        student.setPassword("password");
        student.setConfirmPassword("different passwords");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/update/").flashAttr("student",
                student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/student"))
                .andExpect(MockMvcResultMatchers.model().hasErrors())
                .andExpect(MockMvcResultMatchers.model().attribute("message", "passwordmatch"));
    }

    @Test
    void testEditWhenInputEmailExistsForOutherStudent() throws Exception {
        StudentDto student = new StudentDto();
        student.setSex(Sex.MALE);
        student.setName("validName");
        student.setEmail("existedEmail@mail.ru");
        student.setPhone("80000000000");
        student.setPassword("password");
        student.setConfirmPassword("password");
        MultipartFile mockFile = mock(MultipartFile.class);
        student.setPhoto(mockFile);
        doThrow(new EmailExistException("studentemailexist")).when(studentServiceMock).edit(student);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/update/").flashAttr("student",
                student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/student"))
                .andExpect(MockMvcResultMatchers.model().attribute("message", "studentemailexist"));
    }

    @Test
    void testEditWhenInputInvalidPhoto() throws Exception {
        StudentDto student = new StudentDto();
        student.setSex(Sex.MALE);
        student.setName("validName");
        student.setEmail("existedEmail@mail.ru");
        student.setPhone("80000000000");
        student.setPassword("password");
        student.setConfirmPassword("password");
        MultipartFile mockFile = mock(MultipartFile.class);
        student.setPhoto(mockFile);
        doThrow(new InvalidPhotoException("Input file has invalid extension, it's not photo!")).when(photoServiceMock)
                .savePhoto(student);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/update/").flashAttr("student",
                student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/student")).andExpect(MockMvcResultMatchers
                .model().attribute("message", "Input file has invalid extension, it's not photo!"));
    }

    @Test
    void testEditWhenInputInvalidName() throws Exception {
        StudentDto student = new StudentDto();
        student.setSex(Sex.MALE);
        student.setName("   ");
        student.setEmail("existedEmail@mail.ru");
        student.setPhone("80000000000");
        student.setPassword("password");
        student.setConfirmPassword("password");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/update/").flashAttr("student",
                student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/student"))
                .andExpect(MockMvcResultMatchers.model().hasErrors());
    }

    @Test
    void testEditWhenInputInvalidEmail() throws Exception {
        StudentDto student = new StudentDto();
        student.setSex(Sex.MALE);
        student.setName("validName");
        student.setEmail("invalid email");
        student.setPhone("80000000000");
        student.setPassword("password");
        student.setConfirmPassword("password");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/update/").flashAttr("student",
                student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/student"))
                .andExpect(MockMvcResultMatchers.model().hasErrors());
    }

    @Test
    void testEditWhenInputInvalidPhone() throws Exception {
        StudentDto student = new StudentDto();
        student.setPhone("invalid phone");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/update/").flashAttr("student",
                student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("updateforms/student"))
                .andExpect(MockMvcResultMatchers.model().hasErrors());
    }

    @Test
    void testShouldReturnNotFoundedErrorViewWhenPageNotMapping() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/notexistpage");
        mockMvc.getDispatcherServlet().setThrowExceptionIfNoHandlerFound(true);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.view().name("error404"));
    }
    
    @Test
    void testEditShouldReturnServerExceptionWhenOutherExcetion() throws Exception {
        StudentDto student = new StudentDto();
        student.setName("validName");
        student.setEmail("validEmail@mail.ru");
        student.setPhone("80000000000");
        student.setPassword("password");
        student.setConfirmPassword("password");
        doThrow(new IllegalArgumentException()).when(studentServiceMock).edit(student);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/students/update/").flashAttr("student",
                student);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("error500"));
    }
}
