package org.university.email;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public abstract class AbstractEmailContext<T> {

    private String from;
    private String to;
    private String subject;
    private String email;
    private String attachment;
    private String fromDisplayName;
    private Locale emailLanguage;
    private String displayName;
    private String templateLocation;
    private Map<String, Object> context;

    protected AbstractEmailContext() {
        this.context = new HashMap<>();
    }

    public abstract void init(T context);

    public Object put(String key, Object value) {
        return key == null ? null : this.context.put(key.intern(), value);
    }
}
