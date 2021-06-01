package org.university.entity;

import lombok.Value;
import lombok.Builder;

@Value
@Builder
public class Course{

    Integer id;
    String name;
    String description;
}
