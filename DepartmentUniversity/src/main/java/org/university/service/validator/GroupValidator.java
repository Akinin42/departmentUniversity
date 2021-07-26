package org.university.service.validator;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import org.university.entity.Group;
import org.university.exceptions.InvalidGroupNameException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GroupValidator implements Validator<Group> {

    private static final Pattern GROUP_NAME_PATTERN = Pattern.compile("[A-Z]{2}-\\d{2}");

    @Override
    public void validate(Group group) {
        String groupName = group.getName();
        if (groupName == null || !GROUP_NAME_PATTERN.matcher(groupName).matches()) {
            log.error("Input group name isn't valid!");
            throw new InvalidGroupNameException("invalidname");
        }
    }
}
