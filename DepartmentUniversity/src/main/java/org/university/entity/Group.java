package org.university.entity;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Builder
public class Group {

    private final Integer id;
    private final String name;
    private final List<Student> students;
}
