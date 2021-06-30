package org.university.entity;

import lombok.Value;
import lombok.Builder;

@Value
@Builder(setterPrefix = "with")
public class Course {
    Integer id;
    String name;
    String description;
}
