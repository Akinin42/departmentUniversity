package org.university.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.university.entity.Lesson;

@Repository
public interface LessonDao extends JpaRepository<Lesson, Integer> {

    List<Lesson> findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(LocalDateTime startLesson, LocalDateTime endLesson, int teacherId);
    
    List<Lesson> findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(LocalDateTime startLesson, LocalDateTime endLesson, int groupId);

    Optional<Lesson> findByStartLessonAndTeacherIdAndGroupId(LocalDateTime startLesson, int teacherId, int groupId);
    
    List<Lesson> findAllByStartLessonBetweenOrderByStartLesson(LocalDateTime startLesson, LocalDateTime endLesson);
}
