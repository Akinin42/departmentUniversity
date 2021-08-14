package org.university.utils;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.university.entity.Classroom;
import org.university.entity.Course;
import org.university.entity.Group;
import org.university.entity.Lesson;
import org.university.entity.Student;
import org.university.entity.Teacher;

public class CreatorTestEntities {
    
    public static List<Course> createCourses() {
        List<Course> courses = new ArrayList<>();
        Course course = Course.builder()
                .withId(1)
                .withName("Law")
                .withDescription("test-courses")
                .build();
        courses.add(course);
        course = Course.builder()
                .withId(2)
                .withName("Math")
                .withDescription("test-courses")
                .build();
        courses.add(course);
        course = Course.builder()
                .withId(3)
                .withName("Art")
                .withDescription("test-courses")
                .build();
        courses.add(course);
        return courses;
    }
    
    public static List<Group> createGroups() {
        List<Group> groups = new ArrayList<>();
        Set<Student> studentsFirstGroup = new HashSet<>();
        studentsFirstGroup.add(createStudents().get(0));
        studentsFirstGroup.add(createStudents().get(1));
        studentsFirstGroup.add(createStudents().get(2));
        studentsFirstGroup.add(createStudents().get(3));
        Group group = Group.builder()
                .withId(1)
                .withName("AB-22")
                .withStudents(studentsFirstGroup)
                .build();
        groups.add(group);
        Set<Student> studentsSecondGroup = new HashSet<>();
        studentsSecondGroup.add(createStudents().get(4));
        studentsSecondGroup.add(createStudents().get(5));
        group = Group.builder()
                .withId(2)
                .withName("FR-33")
                .withStudents(studentsSecondGroup)
                .build();
        groups.add(group);
        return groups;
    }
    
    public static List<Classroom> createClassrooms() {
        List<Classroom> classrooms = new ArrayList<>();
        Classroom classroom = Classroom.builder()
                .withId(1)
                .withAddress("Test-address")
                .withNumber(1)
                .withCapacity(10)
                .build();
        classrooms.add(classroom);
        classroom = Classroom.builder()
                .withId(2)
                .withAddress("Test-address")
                .withNumber(2)
                .withCapacity(15)
                .build();
        classrooms.add(classroom);
        return classrooms;
    }
    
    public static List<Teacher> createTeachers(){
        List<Teacher> teachers = new ArrayList<>();
        Teacher teacher = Teacher.builder()
                .withId(1)
                .withSex("Male")
                .withName("Bob Moren")
                .withEmail("Bob@mail.ru")
                .withPhone("89758657788")
                .withPassword("test-password")
                .withScientificDegree("professor")
                .withPhoto("default-male-teacher-photo")
                .build();
        teachers.add(teacher);
        teacher = Teacher.builder()
                .withId(2)
                .withSex("Female")
                .withName("Ann Moren")
                .withEmail("Ann@mail.ru")
                .withPhone("89758651122")
                .withPassword("test-password")
                .withScientificDegree("doctor")
                .withPhoto("default-female-teacher-photo")
                .build();
        teachers.add(teacher);
        return teachers;
    }
    
    public static List<Lesson> createLessons(){
        List<Lesson> lessons = new ArrayList<>();
        Lesson lesson = Lesson.builder()
                .withId(1)
                .withStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00))
                .withEndLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 12, 00, 00))
                .withOnlineLesson(false)
                .withLessonLink(null)
                .withClassroom(createClassrooms().get(0))
                .withCourse(createCourses().get(0))
                .withTeacher(createTeachers().get(0))
                .withGroup(createGroups().get(0))
                .build();
        lessons.add(lesson);
        lesson = Lesson.builder()
                .withId(2)
                .withStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 15, 00, 00))
                .withEndLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 17, 00, 00))
                .withOnlineLesson(true)
                .withLessonLink("test-link")
                .withClassroom(createClassrooms().get(1))
                .withCourse(createCourses().get(1))
                .withTeacher(createTeachers().get(1))
                .withGroup(createGroups().get(1))
                .build();
        lessons.add(lesson);
        lesson = Lesson.builder()
                .withId(3)
                .withStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 21, 00, 00))
                .withEndLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 22, 00, 00))
                .withOnlineLesson(true)
                .withLessonLink("test-link")
                .withClassroom(createClassrooms().get(1))
                .withCourse(createCourses().get(1))
                .withTeacher(createTeachers().get(1))
                .withGroup(createGroups().get(1))
                .build();
        lessons.add(lesson);
        return lessons;
    }
    
    public static List<Student> createStudents(){
        List<Student> students = new ArrayList<>();
        Set<Course> coursesFirstStudent = new HashSet<>();
        coursesFirstStudent.add(createCourses().get(0));
        coursesFirstStudent.add(createCourses().get(1));
        coursesFirstStudent.add(createCourses().get(2));
        Student student = Student.builder()
                .withId(1)
                .withSex("Female")
                .withName("Jane Wood")
                .withEmail("Wood@email.ru")
                .withPhone("test-phone")
                .withPassword("test-password")
                .withPhoto("default-female-photo")
                .withCourses(coursesFirstStudent)
                .build();
        students.add(student);
        Set<Course> courses = new HashSet<>();
        courses.add(createCourses().get(0));
        student = Student.builder()
                .withId(2)
                .withSex("Female")
                .withName("Ann Lee")
                .withEmail("Lee@email.ru")
                .withPhone("test-phone")
                .withPassword("test-password")
                .withPhoto("default-female-photo")
                .withCourses(courses)
                .build();
        students.add(student);
        student = Student.builder()
                .withId(3)
                .withSex("Female")
                .withName("Mary Born")
                .withEmail("Born@email.ru")
                .withPhone("test-phone")
                .withPassword("test-password")
                .withPhoto("default-female-photo")
                .withCourses(courses)
                .build();
        students.add(student);
        student = Student.builder()
                .withId(4)
                .withSex("Male")
                .withName("Rob Melon")
                .withEmail("Melon@email.ru")
                .withPhone("test-phone")
                .withPassword("test-password")
                .withPhoto("default-male-photo")
                .withCourses(courses)
                .build();
        students.add(student);
        student = Student.builder()
                .withId(5)
                .withSex("Male")
                .withName("John Brown")
                .withEmail("Brown@email.ru")
                .withPhone("test-phone")
                .withPassword("test-password")
                .withPhoto("default-male-photo")
                .withCourses(new HashSet<>())
                .build();
        students.add(student);
        student = Student.builder()
                .withId(6)
                .withSex("Male")
                .withName("Pol Hardy")
                .withEmail("Hardy@email.ru")
                .withPhone("test-phone")
                .withPassword("test-password")
                .withPhoto("default-male-photo")
                .withCourses(new HashSet<>())
                .build();
        students.add(student);
        return students;
    }
    
    public static Lesson createTestLesson() {
        Course course = Course.builder()                
                .withName("Law")
                .withDescription("test-courses")
                .build();
        Group group = Group.builder()                
                .withName("AB-22")                
                .build();
        Classroom classroom = Classroom.builder()
                .withAddress("Test-address")
                .withNumber(1)
                .withCapacity(10)
                .build();
        Teacher teacher = Teacher.builder()                
                .withSex("Male")
                .withName("Bob Moren")
                .withEmail("Bob@mail.ru")
                .withPhone("89758657788")
                .withPassword("test-password")
                .withScientificDegree("professor")
                .withPhoto("default-male-teacher-photo")
                .build();
        return Lesson.builder()
                .withStartLesson(LocalDateTime.of(2021, Month.JULY, 9, 11, 00, 00))
                .withEndLesson(LocalDateTime.of(2021, Month.JULY, 9, 13, 00, 00))
                .withOnlineLesson(false)
                .withLessonLink(null)
                .withCourse(course)
                .withGroup(group)
                .withClassroom(classroom)
                .withTeacher(teacher)
                .build();
    }
}
