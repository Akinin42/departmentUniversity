package org.university.entity;

import java.util.Objects;

public class Teacher extends User {

    private final String scientificDegree;

    protected Teacher(TeacherBuilder heirBuilder) {
        super(heirBuilder);
        this.scientificDegree = heirBuilder.scientificDegree;
    }

    public static TeacherBuilder builder() {
        return new TeacherBuilder();
    }

    public String getScientificDegree() {
        return scientificDegree;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getSex(), this.getName(), this.getEmail(), this.getPhone(),
                this.getPassword(), scientificDegree);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Teacher)) {
            return false;
        }
        Teacher other = (Teacher) obj;
        return Objects.equals(this.getId(), other.getId()) &&
               Objects.equals(this.getSex(), other.getSex()) &&
               Objects.equals(this.getName(), other.getName()) &&
               Objects.equals(this.getEmail(), other.getEmail()) &&
               Objects.equals(this.getPhone(), other.getPhone()) &&
               Objects.equals(this.getPassword(), other.getPassword()) &&
               Objects.equals(scientificDegree, other.scientificDegree);
    }

    public static class TeacherBuilder extends UserBuilder<TeacherBuilder> {

        private String scientificDegree;

        public TeacherBuilder() {
        }

        public TeacherBuilder withDegree(String scientificDegree) {
            this.scientificDegree = scientificDegree;
            return self();
        }

        @Override
        public TeacherBuilder self() {
            return this;
        }

        public Teacher build() {
            return new Teacher(self());
        }
    }
}
