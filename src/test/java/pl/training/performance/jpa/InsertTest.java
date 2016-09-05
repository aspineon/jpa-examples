package pl.training.performance.jpa;

import com.codahale.metrics.Timer;
import org.junit.Test;
import pl.training.performance.AbstractPerformanceTest;
import pl.training.performance.datasource.DataSourceAdapter;
import pl.training.performance.entity.Post;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class InsertTest extends AbstractPerformanceTest {

    private static final int BATCH_SIZE = 10;
    private static final int SAMPLE_SIZE = 10_000;

    public InsertTest(DataSourceAdapter dataSourceAdapter) {
        super(dataSourceAdapter);
    }

    @Test
    public void testInsert() {
        EntityManager entityManager = createEntityManagerFactory().createEntityManager();
        Timer timer = metricRegistry.timer(dataSourceAdapter.getClass().getSimpleName());
        EntityTransaction transaction = entityManager.getTransaction();
        long startTime = System.nanoTime();
        transaction.begin();
        for (int sampleNo = 0; sampleNo < SAMPLE_SIZE; sampleNo++) {
            Post post = new Post("Title" + sampleNo, "Text" + sampleNo);
            post.setId(sampleNo + 1L);
            entityManager.persist(post);
        }
        transaction.commit();
        timer.update(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        reporter.report();
        entityManager.close();
    }

    @Test
    public void testInsertPreparedStatement() throws SQLException {
        EntityManager entityManager = createEntityManagerFactory().createEntityManager();
        entityManager.close();
        Timer timer = metricRegistry.timer(dataSourceAdapter.getClass().getSimpleName());
        long startTime = System.nanoTime();
        inTransaction(connection -> {
            try {
                PreparedStatement statement = connection.prepareStatement("insert into post (id, title, text) values (?, ?, ?)");
                int currentStatementNo = 0;
                for (int sampleNo = 0; sampleNo < SAMPLE_SIZE; sampleNo++) {
                    statement.setLong(1, sampleNo + 1);
                    statement.setString(2, "Title" + sampleNo);
                    statement.setString(3, "Text " + sampleNo);
                    statement.addBatch();
                    if (++currentStatementNo == BATCH_SIZE || SAMPLE_SIZE == sampleNo + 1) {
                        currentStatementNo = 0;
                        statement.executeBatch();
                    }
                    //statement.execute();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        timer.update(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        reporter.report();
    }

    public void inTransaction(Consumer<Connection> callback) throws SQLException {
        Connection aquiredConnection = null;
        try (Connection connection = dataSourceAdapter.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            aquiredConnection = connection;
            callback.accept(connection);
            connection.commit();
        } catch (Exception ex) {
            aquiredConnection.rollback();
        }
    }

}