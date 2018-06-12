package pl.training.performance.jdbc;

import com.codahale.metrics.Timer;
import org.junit.Test;
import pl.training.performance.util.PerformanceTest;
import pl.training.performance.util.datasource.DataSourceAdapter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

public class BatchTest extends PerformanceTest {

    private static final int SAMPLE_SIZE = 10_000;
    private static final int BATCH_SIZE = 50;

    public BatchTest(DataSourceAdapter dataSourceAdapter) {
        super(dataSourceAdapter);
    }

    @Test
    public void testInsertStatement() throws SQLException {
        withTransaction(connection -> {
            try {
                dataSourceAdapter.createSchema(connection);
                Statement statement = connection.createStatement();
                Timer timer = metricRegistry.timer(dataSourceAdapter.getClass().getSimpleName());
                int currentStatementNo = 0;
                long startTime = System.nanoTime();
                for (int sampleNo = 0; sampleNo < SAMPLE_SIZE; sampleNo++) {
                    String sql = String.format("insert into post (id, title, version) values (%1$d, 'Post %1$d', 0)", sampleNo);
                    statement.addBatch(sql);
                    if (++currentStatementNo == BATCH_SIZE || SAMPLE_SIZE == sampleNo + 1) {
                        currentStatementNo = 0;
                        statement.executeBatch();
                    }
                    //statement.execute(sql);
                }
                timer.update(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
                reporter.report();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Test
    public void testInsertPreparedStatement() throws SQLException {
        withTransaction(connection -> {
            try {
                dataSourceAdapter.createSchema(connection);
                PreparedStatement statement = connection.prepareStatement("insert into post (id, title, version) values (?, ?, 0)");
                Timer timer = metricRegistry.timer(dataSourceAdapter.getClass().getSimpleName());
                int currentStatementNo = 0;
                long startTime = System.nanoTime();
                for (int sampleNo = 0; sampleNo < SAMPLE_SIZE; sampleNo++) {
                    statement.setLong(1, sampleNo);
                    statement.setString(2, "Post " + sampleNo);
                    statement.addBatch();
                    if (++currentStatementNo == BATCH_SIZE || SAMPLE_SIZE == sampleNo + 1) {
                        currentStatementNo = 0;
                        statement.executeBatch();
                    }
                    //statement.execute();
                }
                timer.update(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
                reporter.report();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

}
