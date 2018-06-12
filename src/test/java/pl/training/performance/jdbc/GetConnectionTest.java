package pl.training.performance.jdbc;

import com.codahale.metrics.Timer;
import org.junit.Test;
import pl.training.performance.util.PerformanceTest;
import pl.training.performance.util.datasource.DataSourceAdapter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class GetConnectionTest extends PerformanceTest {

    private static final int SAMPLE_SIZE = 1_000;

    public GetConnectionTest(DataSourceAdapter dataSourceAdapter) {
        super(dataSourceAdapter);
    }

    @Test
    public void testGetConnectionWithoutPolling() throws SQLException {
        testGetConnection(dataSourceAdapter.getDataSource());
    }

    @Test
    public void testGetConnectionWithPolling() throws SQLException {
        testGetConnection(dataSourceAdapter.getPolledDataSource());
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
