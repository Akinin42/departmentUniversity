package org.university.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.university.dto.StudentDto;
import org.university.dto.UserDto;
import org.university.exceptions.InvalidPhotoException;
import org.university.service.AwsS3Service;
import org.university.service.validator.PhotoValidator;
import org.university.utils.Sex;

class PhotoServiceImplTest {

    private static AwsS3Service awsS3ServiceMock;
    private static PhotoServiceImpl photoService;

    @BeforeAll
    static void init() {
        awsS3ServiceMock = mock(AwsS3Service.class);
        photoService = new PhotoServiceImpl(awsS3ServiceMock, new PhotoValidator());
    }

    @Test
    void savePhotoShouldReturnDefaultStudentMaleFileUrlWhenInputStudentMaleWithoutPhoto() {
        UserDto user = new StudentDto();
        user.setDesiredRole("STUDENT");
        MultipartFile photoMock = mock(MultipartFile.class);
        when(photoMock.isEmpty()).thenReturn(true);
        user.setPhoto(photoMock);
        user.setSex(Sex.MALE);
        assertThat(photoService.savePhoto(user)).isEqualTo("~/university/static/images/malestudent.png");
    }
    
    @Test
    void savePhotoShouldReturnDefaultStudentMaleFileUrlWhenInputUserMaleWithoutPhoto() {
        UserDto user = new UserDto();
        user.setDesiredRole("STUDENT");
        MultipartFile photoMock = mock(MultipartFile.class);
        when(photoMock.isEmpty()).thenReturn(true);
        user.setPhoto(photoMock);
        user.setSex(Sex.MALE);
        assertThat(photoService.savePhoto(user)).isEqualTo("~/university/static/images/malestudent.png");
    }

    @Test
    void savePhotoShouldReturnDefaultStudentFemaleFileUrlWhenInputStudentFemaleWitoutPhoto() {
        UserDto user = new StudentDto();
        user.setDesiredRole("STUDENT");
        MultipartFile photoMock = mock(MultipartFile.class);
        when(photoMock.isEmpty()).thenReturn(true);
        user.setPhoto(photoMock);
        user.setSex(Sex.FEMALE);
        assertThat(photoService.savePhoto(user)).isEqualTo("~/university/static/images/femalestudent.png");
    }

    @Test
    void savePhotoShouldReturnDefaultTeacherMaleFileUrlWhenInputTeacherMaleWitoutPhoto() {
        UserDto user = new UserDto();
        user.setDesiredRole("TEACHER");
        MultipartFile photoMock = mock(MultipartFile.class);
        when(photoMock.isEmpty()).thenReturn(true);
        user.setPhoto(photoMock);
        user.setSex(Sex.MALE);
        assertThat(photoService.savePhoto(user)).isEqualTo("~/university/static/images/maleteacher.png");
    }
    
    @Test
    void savePhotoShouldReturnDefaultTeacherMaleFileUrlWhenInputUserMaleWithoutPhoto() {
        UserDto user = new UserDto();
        user.setDesiredRole(null);
        MultipartFile photoMock = mock(MultipartFile.class);
        when(photoMock.isEmpty()).thenReturn(true);
        user.setPhoto(photoMock);
        user.setSex(Sex.MALE);
        assertThat(photoService.savePhoto(user)).isEqualTo("~/university/static/images/maleteacher.png");
    }

    @Test
    void savePhotoShouldReturnDefaultTeacherFemaleFileUrlWhenInputTeacherFemaleWitoutPhoto() {
        UserDto user = new UserDto();
        user.setDesiredRole("TEACHER");
        MultipartFile photoMock = mock(MultipartFile.class);
        when(photoMock.isEmpty()).thenReturn(true);
        user.setPhoto(photoMock);
        user.setSex(Sex.FEMALE);
        assertThat(photoService.savePhoto(user)).isEqualTo("~/university/static/images/femaleteacher.png");
    }

    @Test
    void savePhotoShouldThrowInvalidPhotoExceptionWhenInputFileNotImage() throws IOException {
        UserDto user = new UserDto();
        File textFile = new File("src/test/resources/testfile.txt");
        FileInputStream input = new FileInputStream(textFile);
        MultipartFile multipartFile = new MockMultipartFile("textfile", textFile.getName(), "text/plain",
                IOUtils.toByteArray(input));
        user.setPhoto(multipartFile);
        assertThatThrownBy(() -> photoService.savePhoto(user)).isInstanceOf(InvalidPhotoException.class);
    }
    
    @Test
    void savePhotoShouldThrowInvalidPhotoExceptionWhenInputFileHasHeightMoreThanExpected() throws IOException {
        UserDto user = new UserDto();
        File file = new File("src/test/resources/filewithbigheight.png");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("filewithbigheight", file.getName(), "image/png",
                IOUtils.toByteArray(input));
        user.setPhoto(multipartFile);
        assertThatThrownBy(() -> photoService.savePhoto(user)).isInstanceOf(InvalidPhotoException.class);
    }
    
    @Test
    void savePhotoShouldThrowInvalidPhotoExceptionWhenInputFileHasWidthMoreThanExpected() throws IOException {
        UserDto user = new UserDto();
        File file = new File("src/test/resources/filewithbigwidth.png");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("filewithbigwidth", file.getName(), "image/png",
                IOUtils.toByteArray(input));
        user.setPhoto(multipartFile);
        assertThatThrownBy(() -> photoService.savePhoto(user)).isInstanceOf(InvalidPhotoException.class);
    }
    
    @Test
    void savePhotoShouldSavePhotoWhenInputValidImage() throws IOException {
        UserDto user = new UserDto();
        File file = new File("src/test/resources/validimage.png");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("validimage", file.getName(), "image/png",
                IOUtils.toByteArray(input));
        user.setPhoto(multipartFile);
        photoService.savePhoto(user);
        verify(awsS3ServiceMock).uploadFile(multipartFile);
    }
    
    @Test
    void savePhotoShouldThrowInvalidPhotoExceptionWhenInpupFileCantReading() throws IOException {
        UserDto user = new StudentDto();
        MultipartFile photoMock = mock(MultipartFile.class);
        when(photoMock.isEmpty()).thenReturn(false);
        when(photoMock.getInputStream()).thenThrow(IOException.class);
        user.setPhoto(photoMock);
        assertThatThrownBy(() -> photoService.savePhoto(user)).isInstanceOf(InvalidPhotoException.class);
    }
}
