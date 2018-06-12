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
        return new HikariDataSource(hikariConfig);
    }

}
