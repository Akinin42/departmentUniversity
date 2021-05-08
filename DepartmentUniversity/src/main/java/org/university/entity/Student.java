package org.university.entity;

import java.util.Objects;
import java.util.Set;

public class Student extends User {

    private final Set<Course> courses;

    protected Student(StudentBuilder heirBuilder) {
        super(heirBuilder);
        this.courses = heirBuilder.courses;
    }

    public static StudentBuilder builder() {
        return new StudentBuilder();
    }

    public Set<Course> getCourses() {
        return courses;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getSex(), this.getName(), this.getEmail(), this.getPhone(),
                this.getPassword(), courses);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Student)) {
            return false;
        }
        Student other = (Student) obj;
        return Objects.equals(this.getId(), other.getId()) && 
               Objects.equals(this.getSex(), other.getSex()) &&
               Objects.equals(this.getName(), other.getName()) &&
               Objects.equals(this.getEmail(), other.getEmail()) && 
               Objects.equals(this.getPhone(), other.getPhone()) && 
               Objects.equals(this.getPassword(), other.getPassword()) && 
               Objects.equals(courses, other.courses);
    }

    public static class StudentBuilder extends UserBuilder<StudentBuilder> {

        private Set<Course> courses;

        public StudentBuilder() {
        }

        public StudentBuilder withCourses(Set<Course> courses) {
            this.courses = courses;
            return self();
        }

        @Override
        public StudentBuilder self() {
            return this;
        }

        public Student build() {
            return new Student(self());
        }
    }
}
