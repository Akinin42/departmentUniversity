package org.university.service.validator;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import org.university.entity.Classroom;
import org.university.exceptions.InvalidAddressException;
import org.university.exceptions.InvalidClassroomCapacityException;
import org.university.exceptions.InvalidClassroomNumberException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ClassroomValidator implements Validator<Classroom> {

    private static final Pattern ADDRESS_PATTERN = Pattern.compile(".{5,100}");

    @Override
    public void validate(Classroom classroom) {
        if (classroom.getNumber() <= 0) {
            log.error("Input classroom has invalid number: " + classroom.getNumber());
            throw new InvalidClassroomNumberException("invalidnumber");
        }        
        if (!ADDRESS_PATTERN.matcher(classroom.getAddress()).matches()) {
            log.error("Input classroom has invalid address: " + classroom.getAddress());
            throw new InvalidAddressException("invalidaddress");
        }
        if (classroom.getCapacity() <= 0) {
            log.error("Input classroom has invalid capacity: " + classroom.getCapacity());
            throw new InvalidClassroomCapacityException("invalidcapacity");
        }        
    }
}
