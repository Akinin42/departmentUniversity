package org.university.entity;

import java.util.Objects;

public class Classroom {

    private final Integer id;
    private final Integer number;
    private final String address;
    private final Integer capacity;

    private Classroom(Builder builder) {
        this.id = builder.id;
        this.number = builder.number;
        this.address = builder.address;
        this.capacity = builder.capacity;
    }

    public static Builder builder() {
        return new Builder();
    }
    
    public Integer getId() {
        return id;
    }

    public Integer getNumber() {
        return number;
    }

    public String getAddress() {
        return address;
    }

    public Integer getCapacity() {
        return capacity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, capacity, number);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Classroom)) {
            return false;
        }
        Classroom other = (Classroom) obj;
        return Objects.equals(address, other.address) &&
               Objects.equals(capacity, other.capacity) &&
               Objects.equals(number, other.number);
    }

    public static class Builder {

        private Integer id;
        private Integer number;
        private String address;
        private Integer capacity;

        private Builder() {
        }
        
        public Builder withId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder withNumber(Integer number) {
            this.number = number;
            return this;
        }

        public Builder withAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder withCapacity(Integer capacity) {
            this.capacity = capacity;
            return this;
        }

        public Classroom build() {
            return new Classroom(this);
        }
    }
}
