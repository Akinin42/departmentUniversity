package org.university.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.university.dao.impl.CourseDaoImpl;
import org.university.dao.impl.GroupDaoImpl;
import org.university.dao.impl.StudentDaoImpl;
import org.university.dto.StudentDto;
import org.university.entity.Course;
import org.university.entity.Group;
import org.university.entity.Student;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.exceptions.InvalidEmailException;
import org.university.exceptions.InvalidPhoneException;
import org.university.exceptions.InvalidUserNameException;
import org.university.service.validator.UserValidator;
import org.university.utils.CreatorTestEntities;

class StudentServiceImplTest {

    private static StudentServiceImpl studentService;
    private static StudentDaoImpl studentDaoMock;

    @BeforeAll
    static void init() {
        studentDaoMock = createStudentDaoMock();
        studentService = new StudentServiceImpl(studentDaoMock, createGroupDaoMock(), createCourseDaoMock(),
                new UserValidator<Student>(studentDaoMock, null), createEncoderMock());
    }

    @Test
    void registerShouldSaveStudentToDatabaseWhenInputStudentNotExistThere() {
        StudentDto student = new StudentDto();        
        student.setSex("Test");
        student.setName("Test");
        student.setEmail("test@test.ru");
        student.setPhone("79236170788");
        student.setPassword("Test");
        studentService.register(student);
        Student studentWithEncodePassword = Student.builder()
                .withSex("Test")
                .withName("Test")
                .withEmail("test@test.ru")
                .withPhone("79236170788")
                .withPassword("encodePassword")
                .build();
        verify(studentDaoMock).save(studentWithEncodePassword);
    }

    @Test
    void registerShouldThrowEntityAlreadyExistExceptionWhenInputStudentExistsInDatabase() {
        StudentDto student = new StudentDto();
        student.setId(1);
        student.setSex("Female");
        student.setName("Jane Wood");
        student.setEmail("Wood@email.ru");
        student.setPhone("78954756666");
        student.setPassword("test-password");
        assertThatThrownBy(() -> studentService.register(student)).isInstanceOf(EntityAlreadyExistException.class);
    }

    @Test
    void registerShouldThrowInvalidEmailExceptionWhenInputStudentHasInvalidEmail() {
        StudentDto student = getTestStudentDto();
        student.setEmail("invalidemail");
        assertThatThrownBy(() -> studentService.register(student)).isInstanceOf(InvalidEmailException.class);
    }
    
    @Test
    void registerShouldThrowInvalidUserNameExceptionWhenInputStudentHasInvalidName() {
        StudentDto student = getTestStudentDto();
        student.setName("4");
        assertThatThrownBy(() -> studentService.register(student)).isInstanceOf(InvalidUserNameException.class);
    }
    
    @Test
    void registerShouldThrowInvalidPhoneExceptionWhenInputStudentHasInvalidPhone() {
        StudentDto student = getTestStudentDto();
        student.setPhone("invalidphone");
        assertThatThrownBy(() -> studentService.register(student)).isInstanceOf(InvalidPhoneException.class);
    }

    @Test
    void registerShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> studentService.register(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteShouldDeleteStudentFromDatabase() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(1);
        studentService.delete(studentDto);
        verify(studentDaoMock).deleteById(1);
    }

    @Test
    void deleteShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> studentService.delete(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findNumberOfUsersShouldReturnExpectedStudentsWhenInputLimitAndOffset() {
        List<Student> students = new ArrayList<>();        
        students.add(CreatorTestEntities.createStudents().get(0));
        when(studentDaoMock.findAll(1, 0)).thenReturn(students);        
        Student student = Student.builder()
                .withId(1)
                .withSex("Female")
                .withName("Jane Wood")
                .withEmail("Wood@email.ru")
                .withPhone("test-phone")
                .withPassword("test-password")
                .withCourses(new HashSet<Course>(CreatorTestEntities.createCourses()))               
                .build();
        assertThat(studentService.findNumberOfUsers(1, 0)).containsExactly(student);
    }

    @Test
    void findNumberOfUsersShouldReturnEmptyListWhenInputOffsetMoreTableSize() {
        when(studentDaoMock.findAll(2, 10)).thenReturn(new ArrayList<>());
        assertThat(studentService.findNumberOfUsers(2, 10)).isEmpty();
    }
    
    @Test
    void findAllShouldReturnExpectedStudentsWhenTheyExist() {
        List<Student> students = CreatorTestEntities.createStudents();
        when(studentDaoMock.findAll()).thenReturn(students);
        assertThat(studentService.findAll()).isEqualTo(students);
    }
    
    @Test
    void findAllShouldReturnEmptyListWhenTheyNotExist() {        
        when(studentDaoMock.findAll()).thenReturn(new ArrayList<Student>());
        assertThat(studentService.findAll()).isEmpty();
    }

    @Test
    void loginShouldReturnExpectedStudentWhenStudentExistsInDatabase() {
        Student student = Student.builder()
                .withId(7)
                .withSex("Test")
                .withName("Test")
                .withEmail("test2@test.ru")
                .withPhone("Test")
                .withPassword("encodePassword")
                .withCourses(new HashSet<Course>())
                .build();
        assertThat(studentService.login("test2@test.ru", "Test")).isEqualTo(student);
    }

    @Test
    void loginShouldThrowAuthorisationFailExceptionWhenInputNotValide() {
        assertThatThrownBy(() -> studentService.login("test2@test.ru", "invalidpassword"))
                .isInstanceOf(AuthorisationFailException.class);
    }

    @Test
    void loginShouldThrowEntityNotExistExceptionWhenInputEmailNotExistsInDatabase() {
        assertThatThrownBy(() -> studentService.login("notExistenEmail", "test@test.ru"))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void addStudentToGroupShouldAddStudentToGroupWhenInputGroupAndStudentExists() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(1);       
        Group group = CreatorTestEntities.createGroups().get(1);
        studentService.addStudentToGroup(studentDto, group);
        Student student = CreatorTestEntities.createStudents().get(0);
        verify(studentDaoMock).insertStudentToGroup(student, group);
    }

    @Test
    void addStudentToGroupShouldNotAddStudentToGroupWhenStudentInThisGroupYet() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(6);
        Group group = CreatorTestEntities.createGroups().get(1);
        studentService.addStudentToGroup(studentDto, group);
        Student student = CreatorTestEntities.createStudents().get(5);
        verify(studentDaoMock, never()).insertStudentToGroup(student, group);
    }

    @Test
    void addStudentToGroupShouldThrowEntityNotExistExceptionWhenInputStudentNotExists() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(10);
        Group group = CreatorTestEntities.createGroups().get(1);
        assertThatThrownBy(() -> studentService.addStudentToGroup(studentDto, group))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void addStudentToGroupShouldThrowEntityNotExistExceptionWhenInputGroupNotExists() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(1);
        Group group = Group.builder()
                .withId(5)
                .withName("notExist")
                .build();
        assertThatThrownBy(() -> studentService.addStudentToGroup(studentDto, group))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void addStudentToGroupShouldThrowIllegalArgumentExceptionWhenInputStudentNull() {
        Group group = CreatorTestEntities.createGroups().get(1);
        assertThatThrownBy(() -> studentService.addStudentToGroup(null, group))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addStudentToGroupShouldThrowIllegalArgumentExceptionWhenInputGroupNull() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(1);
        assertThatThrownBy(() -> studentService.addStudentToGroup(studentDto, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteStudentFromGroupShouldDeleteStudentFromGroupWhenInputGroupAndStudentExists() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(1);
        Group group = CreatorTestEntities.createGroups().get(0);
        studentService.deleteStudentFromGroup(studentDto, group);
        verify(studentDaoMock).deleteStudentFromGroup(1, 1);
    }

    @Test
    void deleteStudentFromGroupShouldThrowIllegalArgumentExceptionWhenInputStudentNull() {
        Group group = CreatorTestEntities.createGroups().get(0);
        assertThatThrownBy(() -> studentService.deleteStudentFromGroup(null, group))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteStudentFromGroupShouldThrowIllegalArgumentExceptionWhenInputGroupNull() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(1);
        assertThatThrownBy(() -> studentService.deleteStudentFromGroup(studentDto, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addStudentToCourseShouldAddStudentToCourseWhenInputCourseAndStudentExists() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(6);
        Course course = CreatorTestEntities.createCourses().get(0);
        List<Course> courses = new ArrayList<>();
        courses.add(course);
        studentService.addStudentToCourse(studentDto, course);
        Student student = CreatorTestEntities.createStudents().get(5);
        verify(studentDaoMock).insertStudentToCourses(student, courses);
    }

    @Test
    void addStudentToCourseShouldNotAddStudentToCourseWhenStudentHasThisCourseYet() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(1);        
        Course course = CreatorTestEntities.createCourses().get(0);
        List<Course> courses = new ArrayList<>();
        courses.add(course);
        studentService.addStudentToCourse(studentDto, course);
        Student student = CreatorTestEntities.createStudents().get(0);
        verify(studentDaoMock, never()).insertStudentToCourses(student, courses);
    }

    @Test
    void addStudentToCourseShouldThrowEntityNotExistExceptionWhenInputStudentNotExists() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(10);
        Course course = CreatorTestEntities.createCourses().get(0);
        assertThatThrownBy(() -> studentService.addStudentToCourse(studentDto, course))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void addStudentToCourseShouldThrowEntityNotExistExceptionWhenInputCourseNotExists() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(1);
        Course course = Course.builder()
                .withId(5)
                .withName("notExist")
                .withDescription("test")
                .build();
        assertThatThrownBy(() -> studentService.addStudentToCourse(studentDto, course))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void addStudentToCourseShouldThrowIllegalArgumentExceptionWhenInputStudentNull() {
        Course course = CreatorTestEntities.createCourses().get(0);
        assertThatThrownBy(() -> studentService.addStudentToCourse(null, course))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addStudentToCourseShouldThrowIllegalArgumentExceptionWhenInputCourseNull() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(1);
        assertThatThrownBy(() -> studentService.addStudentToCourse(studentDto, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteStudentFromCourseShouldDeleteStudentFromCourseWhenInputGroupAndStudentExists() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(1);
        Course course = CreatorTestEntities.createCourses().get(0);
        studentService.deleteStudentFromCourse(studentDto, course);
        verify(studentDaoMock).deleteStudentFromCourse(1, 1);
    }

    @Test
    void deleteStudentFromCourseShouldThrowIllegalArgumentExceptionWhenInputStudentNull() {
        Course course = CreatorTestEntities.createCourses().get(0);
        assertThatThrownBy(() -> studentService.deleteStudentFromCourse(null, course))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteStudentFromCourseShouldThrowIllegalArgumentExceptionWhenInputCourseNull() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(1);
        assertThatThrownBy(() -> studentService.deleteStudentFromCourse(studentDto, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void editShouldThrowInvalidEmailExceptionWhenStudentWithInputEmailExists() {
        StudentDto student = new StudentDto();
        student.setId(1);
        student.setSex("Female");
        student.setName("Jane Wood");
        student.setEmail("test2@test.ru");
        student.setPhone("78954756666");
        student.setPassword("test-password");
        assertThatThrownBy(() -> studentService.edit(student)).isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void editShouldThrowInvalidEmailExceptionWhenInputInvalidEmail() {
        StudentDto student = getTestStudentDto();        
        student.setEmail("invalidemail");
        assertThatThrownBy(() -> studentService.edit(student)).isInstanceOf(InvalidEmailException.class);
    }
    
    @Test
    void editShouldThrowInvalidUserNameExceptionWhenInputInvalidName() {
        StudentDto student = getTestStudentDto();
        student.setName("4");
        assertThatThrownBy(() -> studentService.edit(student)).isInstanceOf(InvalidUserNameException.class);
    }
    
    @Test
    void editShouldThrowInvalidPhoneExceptionWhenInputInvalidPhone() {
        StudentDto student = getTestStudentDto();
        student.setPhone("invalidphone");
        assertThatThrownBy(() -> studentService.edit(student)).isInstanceOf(InvalidPhoneException.class);
    }

    @Test
    void editShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> studentService.edit(null)).isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void editShouldUpdateStudentInDatabaseWhenInputValidStudent() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(1);
        studentDto.setSex("Female");
        studentDto.setName("Jane Wood");
        studentDto.setEmail("test@test.ru");
        studentDto.setPhone("78954756666");
        studentDto.setPassword("Test");
        Student student = Student.builder()
                .withId(1)
                .withSex("Female")
                .withName("Jane Wood")
                .withEmail("test@test.ru")
                .withPhone("78954756666")
                .withPassword("encodePassword")                
                .build();
        studentService.edit(studentDto);
        verify(studentDaoMock).update(student);
    }
    
    @Test
    void editShouldUpdateStudentInDatabaseWhenInputValidStudentAndEmailNotChange() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(8);
        studentDto.setSex("Test");
        studentDto.setName("Test");
        studentDto.setEmail("existmail@test.ru");
        studentDto.setPhone("79236170788");
        studentDto.setPassword("Test");        
        Student student = Student.builder()
                .withId(8)
                .withSex("Test")
                .withName("Test")
                .withEmail("existmail@test.ru")
                .withPhone("79236170788")
                .withPassword("encodePassword")                
                .build();
        studentService.edit(studentDto);
        verify(studentDaoMock).update(student);
    }

    private StudentDto getTestStudentDto() {
        StudentDto student = new StudentDto();
        student.setId(7);
        student.setSex("Test");
        student.setName("Test");
        student.setEmail("test@test.ru");
        student.setPhone("79236170788");
        student.setPassword("Test");
        return student;
    }

    private static CourseDaoImpl createCourseDaoMock() {
        CourseDaoImpl courseDaoMock = mock(CourseDaoImpl.class);
        when(courseDaoMock.findById(1)).thenReturn(Optional.ofNullable(CreatorTestEntities.createCourses().get(0)));
        when(courseDaoMock.findAllByStudent(1)).thenReturn(CreatorTestEntities.createCourses());
        return courseDaoMock;
    }

    private static GroupDaoImpl createGroupDaoMock() {
        GroupDaoImpl groupDaoMock = mock(GroupDaoImpl.class);
        when(groupDaoMock.findById(2)).thenReturn(Optional.ofNullable(CreatorTestEntities.createGroups().get(1)));
        when(groupDaoMock.findById(1)).thenReturn(Optional.ofNullable(CreatorTestEntities.createGroups().get(0)));
        return groupDaoMock;
    }

    private static PasswordEncoder createEncoderMock() {
        PasswordEncoder encoderMock = mock(PasswordEncoder.class);
        when(encoderMock.encode("Test")).thenReturn("encodePassword");
        when(encoderMock.matches("Test", "encodePassword")).thenReturn(true);        
        return encoderMock;
    }

    private static StudentDaoImpl createStudentDaoMock() {
        StudentDaoImpl studentDaoMock = mock(StudentDaoImpl.class);
        Student student = Student.builder()
                .withId(7)
                .withSex("Test")
                .withName("Test")
                .withEmail("test2@test.ru")
                .withPhone("Test")
                .withPassword("encodePassword")
                .withCourses(new HashSet<Course>())
                .build();
        when(studentDaoMock.findByEmail("test@test.ru")).thenReturn(Optional.empty());
        when(studentDaoMock.findByEmail("test2@test.ru")).thenReturn(Optional.ofNullable(student));
        Student newStudent = Student.builder()
                .withId(8)
                .withSex("Test")
                .withName("Test")
                .withEmail("existmail@test.ru")
                .withPhone("79236170788")
                .withPassword("encodePassword")                
                .build();
        when(studentDaoMock.findByEmail("existmail@test.ru")).thenReturn(Optional.ofNullable(newStudent));
        when(studentDaoMock.findById(8)).thenReturn(Optional.ofNullable(newStudent));
        when(studentDaoMock.findById(1)).thenReturn(Optional.ofNullable(CreatorTestEntities.createStudents().get(0)));
        when(studentDaoMock.findById(6)).thenReturn(Optional.ofNullable(CreatorTestEntities.createStudents().get(5)));       
        List<Student> students = CreatorTestEntities.createStudents();
        students.remove(5);
        students.remove(4);
        when(studentDaoMock.findAllByCourse("Law")).thenReturn(students);
        students = new ArrayList<>();
        students.add(CreatorTestEntities.createStudents().get(4));
        students.add(CreatorTestEntities.createStudents().get(5));
        when(studentDaoMock.findAllByGroup("FR-33")).thenReturn(students);
        return studentDaoMock;
    }
}
