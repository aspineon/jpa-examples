package pl.training.performance.jdbc;

import com.codahale.metrics.Timer;
import org.junit.Test;
import pl.training.performance.AbstractPerformanceTest;
import pl.training.performance.datasource.DataSourceAdapter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

public class ResultSetTest extends AbstractPerformanceTest {

    public ResultSetTest(DataSourceAdapter dataSourceAdapter) {
        super(dataSourceAdapter);
    }

    @Test
    public void testGetAllData() throws SQLException {
        try (Connection connection = dataSourceAdapter.getDataSource().getConnection()) {
            Statement statement = connection.createStatement();
            connection.setReadOnly(true);
            ResultSet resultSet = statement.executeQuery("select id from post");
            Timer timer = metricRegistry.timer(dataSourceAdapter.getClass().getSimpleName());
            while (resultSet.next()) {
                long startTime = System.nanoTime();
                System.out.print(resultSet.getString(1));
                timer.update(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
            }
            reporter.report();
        }
    }

}
