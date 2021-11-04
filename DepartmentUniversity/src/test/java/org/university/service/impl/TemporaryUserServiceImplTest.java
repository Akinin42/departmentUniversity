package org.university.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.university.dao.RoleDao;
import org.university.dao.StudentDao;
import org.university.dao.TeacherDao;
import org.university.dao.TemporaryUserDao;
import org.university.dto.UserDto;
import org.university.entity.Role;
import org.university.entity.Student;
import org.university.entity.Teacher;
import org.university.entity.TemporaryUser;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EmailExistException;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.service.validator.UserValidator;
import org.university.utils.CreatorTestEntities;
import org.university.utils.Sex;

class TemporaryUserServiceImplTest {

    private static TemporaryUserServiceImpl temporaryService;
    private static RoleDao roleDaoMock;
    private static TemporaryUserDao temporaryDaoMock;
    private static StudentDao studentDaoMock;
    private static TeacherDao teacherDaoMock;

    @BeforeAll
    static void init() {
        roleDaoMock = createRoleDaoMock();
        temporaryDaoMock = createTemporaryUserDaoMock();        
        studentDaoMock = createStudentDaoMock();
        teacherDaoMock = createTeacherDaoMock();
        temporaryService = new TemporaryUserServiceImpl(temporaryDaoMock,
                new UserValidator(studentDaoMock, teacherDaoMock, temporaryDaoMock, createEncoderMock()), createEncoderMock(),
                roleDaoMock);
    }
    
    @Test
    void registerShouldSaveUserToDatabaseWhenInputUserNotExistThere() {
        UserDto user = new UserDto();        
        user.setSex(Sex.MALE);
        user.setName("Test");
        user.setEmail("test@test.ru");
        user.setPhone("78956547475");
        user.setPassword("Test");
        user.setDesiredRole("TEACHER");
        user.setDesiredDegree("professor");
        user.setPhotoName("test-photo");
        user.setConfirm(true);
        temporaryService.register(user);
        TemporaryUser userForSaving = TemporaryUser.builder()                
                .withSex(Sex.MALE)
                .withName("Test")
                .withEmail("test@test.ru")
                .withPhone("78956547475")
                .withPassword("encodePassword")                
                .withPhoto("test-photo")
                .withRole(roleDaoMock.findByName("USER").get())
                .withEnabled(true)
                .withDesiredRole(roleDaoMock.findByName("TEACHER").get())
                .withDesiredDegree("professor")
                .withConfirm(true)
                .withConfirmDescription(null)
                .build();
        verify(temporaryDaoMock).save(userForSaving);
    }
    
    @Test
    void registerShouldThrowEntityAlreadyExistExceptionWhenInputUserExistsInDatabase() {
        UserDto user = new UserDto();
        user.setId(1);
        user.setSex(Sex.MALE);
        user.setName("Test");
        user.setEmail("test@test.ru");
        user.setPhone("78956547475");
        user.setPassword("Test");
        user.setDesiredRole("TEACHER");
        user.setDesiredDegree("professor");
        user.setPhotoName("test-photo");
        user.setConfirm(true);               
        when(temporaryDaoMock.existsById(1)).thenReturn(true);
        assertThatThrownBy(() -> temporaryService.register(user)).isInstanceOf(EntityAlreadyExistException.class);
    }
    
    @Test
    void registerShouldThrowEmailExistExceptionWhenInputEmailRegistered() {
        UserDto user = new UserDto();        
        user.setSex(Sex.MALE);
        user.setName("Test");
        user.setEmail("existed@test.ru");
        user.setPhone("78956547475");
        user.setPassword("Test");
        user.setDesiredRole("TEACHER");
        user.setDesiredDegree("professor");
        user.setPhotoName("test-photo");
        user.setConfirm(true);
        assertThatThrownBy(() -> temporaryService.register(user)).isInstanceOf(EmailExistException.class);
    }
    
    @Test
    void registerShouldThrowEmailExistExceptionWhenInputEmailRegisteredHowStudent() {
        UserDto user = new UserDto();        
        user.setSex(Sex.MALE);
        user.setName("Test");
        user.setEmail("existestudent@test.ru");
        user.setPhone("78956547475");
        user.setPassword("Test");
        user.setDesiredRole("TEACHER");
        user.setDesiredDegree("professor");
        user.setPhotoName("test-photo");
        user.setConfirm(true);
        assertThatThrownBy(() -> temporaryService.register(user)).isInstanceOf(EmailExistException.class);
    }
    
    @Test
    void registerShouldThrowEmailExistExceptionWhenInputEmailRegisteredHowTeacher() {
        UserDto user = new UserDto();        
        user.setSex(Sex.MALE);
        user.setName("Test");
        user.setEmail("existteachermail@test.ru");
        user.setPhone("78956547475");
        user.setPassword("Test");
        user.setDesiredRole("TEACHER");
        user.setDesiredDegree("professor");
        user.setPhotoName("test-photo");
        user.setConfirm(true);
        assertThatThrownBy(() -> temporaryService.register(user)).isInstanceOf(EmailExistException.class);
    }

    @Test
    void registerShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> temporaryService.register(null)).isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void findAllConfirmUserShouldReturnExpectedUsers() {
        List<TemporaryUser> expectedUsers = CreatorTestEntities.createTemporaryUsers();
        assertThat(temporaryService.findAllConfirmUser()).isEqualTo(expectedUsers);
    }
    
    @Test
    void getByEmailShouldReturnExpectedUserWhenItExists() {
        TemporaryUser existedUser = TemporaryUser.builder()                
                .withSex(Sex.MALE)
                .withName("Test")
                .withEmail("existed@test.ru")
                .withPhone("78956547475")
                .withPassword("encodePassword")                
                .withPhoto("test-photo")
                .withRole(roleDaoMock.findByName("USER").get())
                .withEnabled(true)
                .withDesiredRole(roleDaoMock.findByName("TEACHER").get())
                .withDesiredDegree("professor")
                .withConfirm(true)
                .withConfirmDescription(null)
                .build();
        assertThat(temporaryService.getByEmail("existed@test.ru")).isEqualTo(existedUser);
    }
    
    @Test
    void getByEmailShouldThrowEntityNotExistExceptionWhenTeacherNotExists() {        
        assertThatThrownBy(() -> temporaryService.getByEmail("usernotexiststodb@mail.ru"))
            .isInstanceOf(EntityNotExistException.class).hasMessage("User with usernotexiststodb@mail.ru not found");
    }
    
    @Test
    void addConfirmDescriptionShouldAddDescriptionToUserFromDb() {
        UserDto user = new UserDto();
        user.setEmail("existed@test.ru");
        user.setConfirmDescription("description");        
        TemporaryUser userWithDescription = TemporaryUser.builder()                
                .withSex(Sex.MALE)
                .withName("Test")
                .withEmail("existed@test.ru")
                .withPhone("78956547475")
                .withPassword("encodePassword")                
                .withPhoto("test-photo")
                .withRole(roleDaoMock.findByName("USER").get())
                .withEnabled(true)
                .withDesiredRole(roleDaoMock.findByName("TEACHER").get())
                .withDesiredDegree("professor")
                .withConfirm(false)
                .withConfirmDescription("description")
                .build();
        temporaryService.addConfirmDescription(user);
        verify(temporaryDaoMock).save(userWithDescription);
    }
    
    @Test
    void editShouldThrowAuthorisationFailExceptionWhenPasswordNotCorrect() {
        UserDto user = new UserDto();
        user.setId(5);
        user.setSex(Sex.MALE);
        user.setName("Test");
        user.setEmail("existed@test.ru");
        user.setPhone("78956547475");
        user.setPassword("incorrect password");
        user.setDesiredRole("TEACHER");
        user.setDesiredDegree("professor");
        user.setPhotoName("test-photo");
        user.setConfirm(true);
        assertThatThrownBy(() -> temporaryService.edit(user))
        .isInstanceOf(AuthorisationFailException.class);
    }
    
    @Test
    void editShouldThrowEmailExistExceptionWhenEmailChangeAndExistsYet() {
        UserDto user = new UserDto();
        user.setId(5);
        user.setSex(Sex.MALE);
        user.setName("Test");
        user.setEmail("changedAndExisted@test.ru");
        user.setPhone("78956547475");
        user.setPassword("Test");
        user.setDesiredRole("TEACHER");
        user.setDesiredDegree("professor");
        user.setPhotoName("test-photo");
        user.setConfirm(true);
        assertThatThrownBy(() -> temporaryService.edit(user))
                .isInstanceOf(EmailExistException.class);
    }
    
    private static TemporaryUserDao createTemporaryUserDaoMock() {
        TemporaryUserDao temporaryDaoMock = mock(TemporaryUserDao.class);
        TemporaryUser user = TemporaryUser.builder()                
                .withSex(Sex.MALE)
                .withName("Test")
                .withEmail("existed@test.ru")
                .withPhone("78956547475")
                .withPassword("encodePassword")                
                .withPhoto("test-photo")
                .withRole(roleDaoMock.findByName("USER").get())
                .withEnabled(true)
                .withDesiredRole(roleDaoMock.findByName("TEACHER").get())
                .withDesiredDegree("professor")
                .withConfirm(true)
                .withConfirmDescription(null)
                .build();
        TemporaryUser existedOutherUser = TemporaryUser.builder()
                .withId(6)
                .withSex(Sex.MALE)
                .withName("Test")
                .withEmail("changedAndExisted@test.ru")
                .withPhone("78956547475")
                .withPassword("encodePassword")                
                .withPhoto("test-photo")
                .withRole(roleDaoMock.findByName("USER").get())
                .withEnabled(true)
                .withDesiredRole(roleDaoMock.findByName("TEACHER").get())
                .withDesiredDegree("professor")
                .withConfirm(true)
                .withConfirmDescription(null)
                .build();
        when(temporaryDaoMock.findByEmail("existed@test.ru")).thenReturn(Optional.ofNullable(user));
        when(temporaryDaoMock.findById(5)).thenReturn(Optional.ofNullable(user));
        when(temporaryDaoMock.findByEmail("changedAndExisted@test.ru")).thenReturn(Optional.ofNullable(existedOutherUser));
        when(temporaryDaoMock.findById(6)).thenReturn(Optional.ofNullable(existedOutherUser));
        List<TemporaryUser> users = CreatorTestEntities.createTemporaryUsers();
        when(temporaryDaoMock.findAllByConfirm(true)).thenReturn(users);
        return temporaryDaoMock;
    }

    private static PasswordEncoder createEncoderMock() {
        PasswordEncoder encoderMock = mock(PasswordEncoder.class);
        when(encoderMock.encode("Test")).thenReturn("encodePassword");
        when(encoderMock.matches("Test", "encodePassword")).thenReturn(true);        
//        when(encoderMock.matches("test-password", "test-password")).thenReturn(true);        
        return encoderMock;
    }

    private static StudentDao createStudentDaoMock() {
        StudentDao studentDaoMock = mock(StudentDao.class);
        when(studentDaoMock.findByEmail("existed@test.ru")).thenReturn(Optional.empty());
        Student existStudent = Student.builder()
                .withId(15)
                .withEmail("existestudent@test.ru")                
                .build();
        when(studentDaoMock.findByEmail("existestudent@test.ru")).thenReturn(Optional.ofNullable(existStudent));
        return studentDaoMock;
    }
    
    private static RoleDao createRoleDaoMock() {
        RoleDao roleDaoMock = mock(RoleDao.class);
        Role studentRole = Role.builder()
                .withId(1)
                .withName("STUDENT")
                .build();
        when(roleDaoMock.findByName("STUDENT")).thenReturn(Optional.ofNullable(studentRole));
        Role teacherRole = Role.builder()
                .withId(2)
                .withName("TEACHER")
                .build();
        when(roleDaoMock.findByName("TEACHER")).thenReturn(Optional.ofNullable(teacherRole)); 
        Role userRole = Role.builder()
                .withId(4)
                .withName("USER")
                .build();
        when(roleDaoMock.findByName("USER")).thenReturn(Optional.ofNullable(userRole));        
        return roleDaoMock;
    }
    
    private static TeacherDao createTeacherDaoMock() {
        TeacherDao teacherDaoMock = mock(TeacherDao.class);
        when(teacherDaoMock.findByEmail("existed@test.ru")).thenReturn(Optional.empty());
        Teacher existTeacher = Teacher.builder()
                .withId(15)
                .withEmail("existteachermail@test.ru")
                .build();
        when(teacherDaoMock.findByEmail("existteachermail@test.ru")).thenReturn(Optional.ofNullable(existTeacher));
        return teacherDaoMock;
    }

}
