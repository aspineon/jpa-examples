package pl.training.performance.datasource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

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

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

}
