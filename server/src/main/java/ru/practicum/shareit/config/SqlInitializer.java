package ru.practicum.shareit.config;

import javax.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class SqlInitializer {
    private final DataSource dataSource;
    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY_MS = 2000;

    public SqlInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void init() throws Exception {
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            try (Connection conn = dataSource.getConnection()) {
                ScriptUtils.executeSqlScript(conn, new ClassPathResource("schema.sql"));
                break;
            } catch (Exception e) {
                attempt++;
                if (attempt == MAX_RETRIES) {
                    throw new RuntimeException("Failed to initialize SQL after " + MAX_RETRIES + " attempts", e);
                }
                Thread.sleep(RETRY_DELAY_MS);
            }
        }
    }
}