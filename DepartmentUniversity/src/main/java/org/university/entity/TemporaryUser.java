package org.university.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "temporary_users")
@SuperBuilder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TemporaryUser extends User {
    
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "desiredRole", referencedColumnName = "role_id")
    Role desiredRole;
    
    String desiredDegree;    
    Boolean confirm;    
    String confirmDescription;
}
