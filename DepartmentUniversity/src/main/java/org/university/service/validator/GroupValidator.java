package org.university.service.validator;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import org.university.entity.Group;
import org.university.exceptions.InvalidGroupNameException;

@Component
public class GroupValidator implements Validator<Group> {
    
    private static final Pattern GROUP_NAME_PATTERN = Pattern.compile("[A-Z]{2}-\\d{2}");

    @Override
    public void validate(Group group) {        
        if (group == null) {
            throw new IllegalArgumentException();
        }
        String groupName = group.getName();
        if (!GROUP_NAME_PATTERN.matcher(groupName).matches()) {
            throw new InvalidGroupNameException();
        }        
    }
}
