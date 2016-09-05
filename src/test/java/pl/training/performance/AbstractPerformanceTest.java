package pl.training.performance;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.training.performance.datasource.DataSourceAdapter;
import pl.training.performance.datasource.MySqlDataSourceAdapter;
import pl.training.performance.datasource.PostgreSqlDataSourceAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public abstract class AbstractPerformanceTest {

    protected int maxPollSize = 50;
    protected long idleTimeout = 5000;
    protected DataSourceAdapter dataSourceAdapter;
    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected MetricRegistry metricRegistry = new MetricRegistry();
    protected Slf4jReporter reporter = Slf4jReporter
            .forRegistry(metricRegistry)
            .outputTo(logger)
            .build();

    @Parameters
    public static Collection<DataSourceAdapter[]> dataSourceProviders() {
        List<DataSourceAdapter[]> dataSourceAdapters = new ArrayList<>();
        dataSourceAdapters.add(new DataSourceAdapter[] { new MySqlDataSourceAdapter("root", "admin", "jdbc:mysql://localhost/test")});
        dataSourceAdapters.add(new DataSourceAdapter[] { new PostgreSqlDataSourceAdapter("postgres", "admin", "jdbc:postgresql://localhost/postgres")});
        return dataSourceAdapters;
    }

    public AbstractPerformanceTest(DataSourceAdapter dataSourceAdapter) {
        this.dataSourceAdapter = dataSourceAdapter;
    }

    public HikariDataSource getHikariDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(dataSourceAdapter.getUrl());
        hikariConfig.setUsername(dataSourceAdapter.getUsername());
        hikariConfig.setPassword(dataSourceAdapter.getPassword());
        hikariConfig.setMaximumPoolSize(maxPollSize);
        hikariConfig.setIdleTimeout(idleTimeout);
        return new HikariDataSource(hikariConfig);
    }

}
