package org.university.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "teachers")
@SuperBuilder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@AttributeOverride(name = "id", column = @Column(name = "teacher_id"))
@AttributeOverride(name = "sex", column = @Column(name = "teacher_sex"))
@AttributeOverride(name = "name", column = @Column(name = "teacher_name"))
@AttributeOverride(name = "email", column = @Column(name = "teacher_email"))
@AttributeOverride(name = "phone", column = @Column(name = "teacher_phone"))
@AttributeOverride(name = "password", column = @Column(name = "teacher_password"))
@AttributeOverride(name = "photo", column = @Column(name = "teacher_photo"))
@AttributeOverride(name = "enabled", column = @Column(name = "teacher_enabled"))
public class Teacher extends User {

    @Column(name = "teacher_degree", length = 50)
    String scientificDegree;
}
