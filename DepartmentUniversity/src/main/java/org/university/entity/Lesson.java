package org.university.entity;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "lessons")
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "lesson_id", unique = true, nullable = false)
    Integer id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lesson_course", referencedColumnName = "course_id")
    Course course;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "lesson_teacher", referencedColumnName = "teacher_id")
    Teacher teacher;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lesson_group", referencedColumnName = "group_id")
    Group group;

    @Column(name = "lesson_start", columnDefinition = "TIMESTAMP", nullable = false)    
    LocalDateTime startLesson;

    @Column(name = "lesson_end", columnDefinition = "TIMESTAMP", nullable = false)    
    LocalDateTime endLesson;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lesson_classroom", referencedColumnName = "classroom_id")
    Classroom classroom;

    @Column(name = "lesson_online")
    Boolean onlineLesson;

    @Column(name = "lesson_link")
    String lessonLink;
}
