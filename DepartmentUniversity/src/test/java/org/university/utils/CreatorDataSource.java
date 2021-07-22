package org.university.utils;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class CreatorDataSource {
    
    public static DataSource createTestDataSource() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setUrl("jdbc:h2:~/test");
        driverManagerDataSource.setUsername("sa");
        driverManagerDataSource.setPassword("");
        driverManagerDataSource.setDriverClassName("org.h2.Driver");
        return driverManagerDataSource;        
    }
}
