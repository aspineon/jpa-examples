package pl.training.performance.datasource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;

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

}
