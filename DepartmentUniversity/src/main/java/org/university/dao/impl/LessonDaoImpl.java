package org.university.dao.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;
import org.university.dao.LessonDao;
import org.university.entity.Lesson;

@Repository
public class LessonDaoImpl extends AbstractCrudImpl<Lesson> implements LessonDao {

    public LessonDaoImpl(EntityManager entityManager) {
        super(entityManager, Lesson.class);
    }

    @Override
    public List<Lesson> findAllByDateAndTeacher(LocalDate date, int teacherId) {
        return entityManager.createQuery(
                "from Lesson where date(startLesson) =: date and lesson_teacher =:teacherId order by startLesson",
                Lesson.class)
                .setParameter("date", date)
                .setParameter("teacherId", teacherId)
                .getResultList();
    }

    @Override
    public List<Lesson> findAllByDateAndGroup(LocalDate date, int groupId) {
        return entityManager
                .createQuery(
                        "from Lesson where date(startLesson) =:date and lesson_group =:groupId order by startLesson",
                        Lesson.class)
                .setParameter("date", date)
                .setParameter("groupId", groupId)
                .getResultList();
    }

    @Override
    public List<Lesson> findAllByMonthAndTeacher(int month, int teacherId) {
        return entityManager.createQuery(
                "from Lesson where month(startLesson) =:month and lesson_teacher =:teacherId order by startLesson",
                Lesson.class)
                .setParameter("month", month)
                .setParameter("teacherId", teacherId)
                .getResultList();
    }

    @Override
    public List<Lesson> findAllByMonthAndGroup(int month, int groupId) {
        return entityManager
                .createQuery(
                        "from Lesson where month(startLesson) =:month and lesson_group =:groupId order by startLesson",
                        Lesson.class)
                .setParameter("month", month)
                .setParameter("groupId", groupId)
                .getResultList();
    }

    @Override
    public Optional<Lesson> findByTimeAndTeacherAndGroup(LocalDateTime date, int teacherId, int groupId) {
        Lesson lesson = null;
        try {
            lesson = entityManager.createQuery(
                    "from Lesson where startLesson =:date and lesson_teacher =:teacherId and lesson_group =:groupId",
                    Lesson.class)
                    .setParameter("date", date)
                    .setParameter("teacherId", teacherId)
                    .setParameter("groupId", groupId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return Optional.empty();
        }
        return Optional.ofNullable(lesson);
    }

    @Override
    public List<Lesson> findAllByDate(LocalDate date) {
        return entityManager
                .createQuery("from Lesson where date(startLesson) =:date order by startLesson", Lesson.class)
                .setParameter("date", date).getResultList();
    }

    @Override
    public List<Lesson> findAllByWeekAndTeacher(LocalDate startWeek, LocalDate endWeek, int teacherId) {
        return entityManager.createQuery(
                "from Lesson where date(startLesson) between :start and :end and lesson_teacher =:teacherId order by startLesson",
                Lesson.class)
                .setParameter("start", startWeek)
                .setParameter("end", endWeek)
                .setParameter("teacherId", teacherId)
                .getResultList();
    }

    @Override
    public List<Lesson> findAllByWeekAndGroup(LocalDate startWeek, LocalDate endWeek, int groupId) {
        return entityManager.createQuery(
                "from Lesson where date(startLesson) between :start and :end and lesson_group =:groupId order by startLesson",
                Lesson.class)
                .setParameter("start", startWeek)
                .setParameter("end", endWeek)
                .setParameter("groupId", groupId)
                .getResultList();
    }
}
