package pl.training.performance.datasource;

import org.hibernate.dialect.PostgreSQL9Dialect;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PostgreSqlDataSourceAdapter extends DataSourceAdapter {

    public PostgreSqlDataSourceAdapter(String username, String password, String url) {
        super(username, password, url);
    }

    @Override
    public DataSource getDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Override
    public void createSchema(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("drop table if exists post cascade");
        statement.executeUpdate("create table post (id bigint not null, title varchar(255), version integer not null, primary key (id))");
    }

    @Override
    public String getDialect() {
        return PostgreSQL9Dialect.class.getName();
    }

}
