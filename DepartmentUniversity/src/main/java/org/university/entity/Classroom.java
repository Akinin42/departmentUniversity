package org.university.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@EqualsAndHashCode
@Builder(setterPrefix = "with")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Classroom {

    Integer id;
    Integer number;
    String address;
    Integer capacity;
}
