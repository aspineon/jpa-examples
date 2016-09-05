package pl.training.performance.datasource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.hibernate.dialect.MySQL5Dialect;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MySqlDataSourceAdapter extends DataSourceAdapter {

    public MySqlDataSourceAdapter(String username, String password, String url) {
        super(username, password, url);
    }

    @Override
    public DataSource getDataSource() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(url);
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
        return MySQL5Dialect.class.getName();
    }

}
