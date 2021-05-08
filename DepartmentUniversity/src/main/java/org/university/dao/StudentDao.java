package org.university.dao;

import java.util.List;
import java.util.Optional;
import org.university.entity.Course;
import org.university.entity.Group;
import org.university.entity.Student;

public interface StudentDao extends CrudDao<Student, Integer> {

    List<Student> findAllByCourse(String courseName);

    List<Student> findAllByGroup(String groupName);

    void insertStudentToCourses(Student student, List<Course> courses);

    void insertStudentToGroup(Student student, Group group);

    void deleteStudentFromGroup(int studentId, int groupId);

    void deleteStudentFromCourse(int studentId, int courseId);

    Optional<Student> findByEmail(String email);
}
