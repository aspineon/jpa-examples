package pl.training.performance.datasource;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

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

}
