package pl.training.performance.jdbc;

import com.codahale.metrics.Timer;
import org.junit.Test;
import pl.training.performance.AbstractPerformanceTest;
import pl.training.performance.datasource.DataSourceAdapter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/*
    - Czas odpowiedzi znacząco spada przy zastosowaniu puli połączęń
                  Postgresql              MySql
    bez puli      38.405  ns              4.048 ns
    z pulą (5)    0.048   ns              0.036 ns
    z pulą (50)   0.065   ns              0.048 ns
 */
public class GetConnectionTest extends AbstractPerformanceTest {

    private static final int SAMPLE_SIZE = 1000;

    public GetConnectionTest(DataSourceAdapter dataSourceAdapter) {
        super(dataSourceAdapter);
    }

    @Test
    public void testGetConnectionWithoutPolling() throws SQLException {
        testGetConnection(dataSourceAdapter.getDataSource());
    }

    @Test
    public void testGetConnectionWithPolling() throws SQLException {
        testGetConnection(getHikariDataSource());
    }

    private void testGetConnection(DataSource dataSource) throws SQLException {
        Timer time = metricRegistry.timer(dataSourceAdapter.getClass().getSimpleName());
        for (int sampleNo = 0; sampleNo < SAMPLE_SIZE; sampleNo++) {
            long startTime = System.nanoTime();
            try (Connection connection = dataSource.getConnection()) {
            }
            time.update(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        }
        reporter.report();
    }

}
