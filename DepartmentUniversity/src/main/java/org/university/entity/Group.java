package org.university.entity;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Getter
@EqualsAndHashCode
@Builder(setterPrefix = "with")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Group {
    Integer id;
    String name;
    List<Student> students;
}
