package pl.training.performance.datasource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class DataSourceAdapter {

    protected String username;
    protected String password;
    protected String url;

    public DataSourceAdapter(String username, String password, String url) {
        this.username = username;
        this.password = password;
        this.url = url;
    }

    public abstract DataSource getDataSource();

    public abstract void createSchema(Connection connection) throws SQLException;

    public abstract String getDialect();

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getJpaProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("javax.persistence.jdbc.url", url);
        properties.put("javax.persistence.jdbc.user", username);
        properties.put("javax.persistence.jdbc.password", password);
        properties.put("hibernate.dialect", getDialect());
        return properties;
    }

}
