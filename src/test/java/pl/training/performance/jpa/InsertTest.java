package pl.training.performance.jpa;

import com.codahale.metrics.Timer;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.QueryHints;
import org.hibernate.stat.Statistics;
import org.junit.Test;
import pl.training.performance.AbstractPerformanceTest;
import pl.training.performance.datasource.DataSourceAdapter;
import pl.training.performance.entity.Post;
import pl.training.performance.entity.PostComment;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.QueryHint;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class InsertTest extends AbstractPerformanceTest {

    private static final int BATCH_SIZE = 10;
    private static final int SAMPLE_SIZE = 10_000;

    public InsertTest(DataSourceAdapter dataSourceAdapter) {
        super(dataSourceAdapter);
    }

    @Test
    public void testCache() {
        EntityManagerFactory entityManagerFactory = createEntityManagerFactory();

        EntityManager entityManager = entityManagerFactory .createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        printStats(statistics);
        Post post = new Post("aa", "aa");
        entityManager.persist(post);
        printStats(statistics);
        transaction.commit();
        entityManager.close();

        entityManager = entityManagerFactory .createEntityManager();
        Post post2 = entityManager.find(Post.class, post.getId());
        System.out.println(post.getId());
        printStats(statistics);
        entityManager.close();

    }

    public void printStats(Statistics statistics) {
        System.out.println("Fetch count: " + statistics.getEntityFetchCount());
        System.out.println("2 level cache hit count: " + statistics.getSecondLevelCacheHitCount());
        System.out.println("2 level cache miss count: " + statistics.getSecondLevelCacheMissCount());
        System.out.println("2 level cache put count: " + statistics.getSecondLevelCachePutCount());
    }



    @Test
    public void testRelations() {
        Post post = new Post("Test title", "Test text");
        PostComment postComment = new PostComment();
        postComment.setText("Comment");
        post.getComments().add(postComment);
        postComment.setPost(post);
        EntityManager entityManager = createEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(post);
        transaction.commit();
        entityManager.close();

        entityManager = createEntityManagerFactory().createEntityManager();
        List<PostComment> postComments = entityManager
                .createQuery("select pc from PostComment pc join fetch pc.post p", PostComment.class)
                .getResultList();
        postComments.forEach(comment -> System.out.println(comment.getPost().getTitle()));

        entityManager.close();

//        // left outer join
//        entityManager = createEntityManagerFactory().createEntityManager();
//        PostComment pc = entityManager.find(PostComment.class, postComment.getId());
//        System.out.println("#####################################");
//        pc.getPost().getText();
//        entityManager.close();
//
//        // select
//        entityManager = createEntityManagerFactory().createEntityManager();
//        entityManager.createQuery("select pc from PostComment pc where pc.id = :id")
//                .setParameter("id", postComment.getId())
//                .getSingleResult();
//        entityManager.close();
//
//        // join
//        entityManager = createEntityManagerFactory().createEntityManager();
//        entityManager.createQuery("select pc from PostComment pc join fetch pc.post p where pc.id = :id")
//                .setParameter("id", postComment.getId())
//                .getSingleResult();
//        entityManager.close();
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
