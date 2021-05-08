package org.university.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Lesson {

    private final Integer id;
    private final Course course;
    private final Teacher teacher;
    private final Group group;
    private final LocalDateTime startLesson;
    private final LocalDateTime endLesson;
    private final Classroom classroom;
    private final Boolean onlineLesson;
    private final String lessonLink;

    private Lesson(Builder builder) {
        this.id = builder.id;
        this.course = builder.course;
        this.teacher = builder.teacher;
        this.group = builder.group;
        this.startLesson = builder.startLesson;
        this.endLesson = builder.endLesson;
        this.classroom = builder.classroom;
        this.onlineLesson = builder.onlineLesson;
        this.lessonLink = builder.lessonLink;
    }

    public static Builder builder() {
        return new Builder();
    }
    
    public Integer getId() {
        return id;
    }

    public Course getCourse() {
        return course;
    }

    public Teacher getTeacher() {
        return teacher;
    }
    
    public Group getGroup() {
        return group;
    }

    public LocalDateTime getStartLesson() {
        return startLesson;
    }
    
    public LocalDateTime getEndLesson() {
        return endLesson;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public Boolean getOnlineLesson() {
        return onlineLesson;
    }

    public String getLessonLink() {
        return lessonLink;
    }

    @Override
    public int hashCode() {
        return Objects.hash(classroom, course, lessonLink, onlineLesson, teacher, startLesson, endLesson);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Lesson)) {
            return false;
        }
        Lesson other = (Lesson) obj;
        return Objects.equals(classroom, other.classroom) &&
               Objects.equals(course, other.course) &&
               Objects.equals(lessonLink, other.lessonLink) &&
               Objects.equals(onlineLesson, other.onlineLesson) &&
               Objects.equals(teacher, other.teacher) &&
               Objects.equals(startLesson, other.startLesson) &&
               Objects.equals(endLesson, other.endLesson);
    }

    public static class Builder {

        private Integer id;
        private Course course;
        private Teacher teacher;
        private Group group;
        private LocalDateTime startLesson;
        private LocalDateTime endLesson;
        private Classroom classroom;
        private Boolean onlineLesson;
        private String lessonLink;

        private Builder() {
        }
        
        public Builder withId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder withCourse(Course course) {
            this.course = course;
            return this;
        }

        public Builder withTeacher(Teacher teacher) {
            this.teacher = teacher;
            return this;
        }
        
        public Builder withGroup(Group group) {
            this.group = group;
            return this;
        }

        public Builder withStartLesson(LocalDateTime startLesson) {
            this.startLesson = startLesson;
            return this;
        }
        
        public Builder withEndLesson(LocalDateTime endLesson) {
            this.endLesson = endLesson;
            return this;
        }

        public Builder withClassroom(Classroom classroom) {
            this.classroom = classroom;
            return this;
        }

        public Builder withOnlineLesson(Boolean onlineLesson) {
            this.onlineLesson = onlineLesson;
            return this;
        }

        public Builder withLessonLink(String lessonLink) {
            this.lessonLink = lessonLink;
            return this;
        }

        public Lesson build() {
            return new Lesson(this);
        }
    }
}
