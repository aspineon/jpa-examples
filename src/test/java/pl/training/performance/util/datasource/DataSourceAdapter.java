package pl.training.performance.util.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public abstract class DataSourceAdapter {

    private static final int MAX_POOL_SIZE = 50;
    private static final long IDLE_TIMEOUT = 5000;

    String username;
    String password;
    String url;

    public abstract void createSchema(Connection connection) throws SQLException;

    public abstract String getDialect();

    public Map<String, String> getJpaProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("javax.persistence.jdbc.url", url);
        properties.put("javax.persistence.jdbc.user", username);
        properties.put("javax.persistence.jdbc.password", password);
        properties.put("hibernate.dialect", getDialect());
        return properties;
    }

    public abstract DataSource getDataSource();

    public DataSource getPolledDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(MAX_POOL_SIZE);
        hikariConfig.setIdleTimeout(IDLE_TIMEOUT);
        return new HikariDataSource(hikariConfig);
    }

}
