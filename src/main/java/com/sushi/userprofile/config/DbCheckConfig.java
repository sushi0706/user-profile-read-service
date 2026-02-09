package com.sushi.userprofile.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
public class DbCheckConfig {
    private static final Logger logger = LoggerFactory.getLogger(DbCheckConfig.class);

    @Bean
    CommandLineRunner dbCheck(DataSource dataSource) {
        return args -> {
            try (Connection conn = dataSource.getConnection()) {
                logger.info("PostgreSQL connected: {}", conn.getMetaData().getURL());
            }
        };
    }
}
