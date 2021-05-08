package org.university.dao;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.university.io.FileReader;

@Component
public class ScriptExecutor {
    
    private final JdbcTemplate jdbcTemplate;
    private final FileReader reader;

    public ScriptExecutor(DataSource dataSource, FileReader reader) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        this.reader = reader;
    }
    
    public void executeScript(String scriptPath) {
        String queryCreateDB = createSqlQuery(reader.read(scriptPath));
        jdbcTemplate.execute(queryCreateDB);
    }
    
    private String createSqlQuery(List<String> scriptFileContent) {
        return scriptFileContent.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
