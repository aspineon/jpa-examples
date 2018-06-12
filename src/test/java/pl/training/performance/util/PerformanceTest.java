package pl.training.performance.util;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.LoggerFactory;
import pl.training.performance.util.datasource.DataSourceAdapter;
import pl.training.performance.util.datasource.PostgreSqlDataSourceAdapter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

@RunWith(Parameterized.class)
public abstract class PerformanceTest {

    protected DataSourceAdapter dataSourceAdapter;
    protected MetricRegistry metricRegistry = new MetricRegistry();
    protected Slf4jReporter reporter = Slf4jReporter
            .forRegistry(metricRegistry)
            .outputTo(LoggerFactory.getLogger(getClass()))
            .build();

    protected EntityManagerFactory entityManagerFactory = createEntityManagerFactory();

    @Parameters
    public static Collection<DataSourceAdapter[]> dataSourceProviders() {
        List<DataSourceAdapter[]> dataSourceAdapters = new ArrayList<>();
        dataSourceAdapters.add(new DataSourceAdapter[] { new PostgreSqlDataSourceAdapter("postgres", "postgres", "jdbc:postgresql://localhost/training")});
        return dataSourceAdapters;
    }

    public PerformanceTest(DataSourceAdapter dataSourceAdapter) {
        this.dataSourceAdapter = dataSourceAdapter;
    }

    protected EntityManagerFactory createEntityManagerFactory() {
        return Persistence.createEntityManagerFactory("training", dataSourceAdapter.getJpaProperties());
    }

    protected void withTransaction(Consumer<Connection> task) throws SQLException {
        Connection acquiredConnection = null;
        try (Connection connection = dataSourceAdapter.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            acquiredConnection = connection;
            task.accept(connection);
            connection.commit();
        } catch (Exception ex) {
            acquiredConnection.rollback();
        }
    }

    protected void withEntityManager(Consumer<EntityManager> task) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        task.accept(entityManager);
        transaction.commit();
        entityManager.close();
    }

}
