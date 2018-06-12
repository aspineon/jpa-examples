package pl.training.performance.jpa;

import com.codahale.metrics.Timer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pl.training.performance.util.PerformanceTest;
import pl.training.performance.util.datasource.DataSourceAdapter;
import pl.training.performance.entity.Post;
import pl.training.performance.entity.PostComment;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ReadWriteTest extends PerformanceTest {

    private static final int SAMPLE_SIZE = 10_000;
    private static final int SIMULATED_USERS = 2;

    public ReadWriteTest(DataSourceAdapter dataSourceAdapter) {
        super(dataSourceAdapter);
    }

    @Before
    public void setup() {
        Post post = new Post("Test title", "Test text");
        PostComment postComment = new PostComment();
        postComment.setText("Comment");
        post.getComments().add(postComment);
        postComment.setPost(post);
        withEntityManager(entityManager -> entityManager.persist(post));
    }

    @After
    public void onClose() {
        entityManagerFactory.close();
    }

    @Test
    public void testCriteria() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = criteriaBuilder.createQuery(Tuple.class);
        Root<Post> posts = query.from(Post.class);

        Join<Post, PostComment> comments = posts.join("comments", JoinType.LEFT);

        query.multiselect(posts.get("id"), posts.get("text").alias("text"), comments.get("text"))
                .where(criteriaBuilder.equal(posts.get("id"), 1L));

        List<Tuple> results = entityManager.createQuery(query).getResultList();
        results.forEach(tuple -> System.out.println(tuple.get(0) + " " + tuple.get("text") + " " + tuple.get(2)));

        transaction.commit();
        entityManager.close();
    }

    @Test
    public void testLocks() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(SIMULATED_USERS);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(SIMULATED_USERS);

        for (int i = 0; i < SIMULATED_USERS; i++) {
            Task task = new Task(countDownLatch, entityManagerFactory.createEntityManager());
            executor.submit(task);
        }

        countDownLatch.await();
        System.out.println("All users finished");
    }

    @Test
    public void testQuery() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Timer timer = metricRegistry.timer(dataSourceAdapter.getClass().getSimpleName());
        long startTime = System.nanoTime();

        List<PostComment> postComments = entityManager
                .createQuery("select pc from PostComment pc join pc.post p", PostComment.class)
                .getResultList();

        postComments.forEach(comment -> System.out.println(comment.getPost().getTitle()));

        timer.update(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        reporter.report();
        entityManager.close();

//        // left outer join
//        PostComment pc = entityManager.find(PostComment.class, postComment.getId());
//        pc.getPost().getText();
//
//        // select
//        entityManager.createQuery("select pc from PostComment pc where pc.id = :id")
//                .setParameter("id", postComment.getId())
//                .getSingleResult();
//
//        // join
//        entityManager.createQuery("select pc from PostComment pc join fetch pc.post p where pc.id = :id")
//                .setParameter("id", postComment.getId())
//                .getSingleResult();
    }

    @Test
    public void testPersist() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
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

}
