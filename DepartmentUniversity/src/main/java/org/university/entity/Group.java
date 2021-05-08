package org.university.entity;

import java.util.List;
import java.util.Objects;

public class Group {

    private final Integer id;
    private final String name;
    private final List<Student> students;

    private Group(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.students = builder.students;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Student> getStudents() {
        return students;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, students);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Group)) {
            return false;
        }
        Group other = (Group) obj;
        return Objects.equals(id, other.id) &&
               Objects.equals(name, other.name) &&
               Objects.equals(students, other.students);
    }

    public static class Builder {

        private Integer id;
        private String name;
        private List<Student> students;

        private Builder() {
        }

        public Builder withId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withStudents(List<Student> students) {
            this.students = students;
            return this;
        }

        public Group build() {
            return new Group(this);
        }
    }
}
