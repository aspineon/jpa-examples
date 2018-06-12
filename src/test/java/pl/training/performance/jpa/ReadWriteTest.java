package pl.training.performance.jpa;

import com.codahale.metrics.Timer;
import org.junit.Test;
import pl.training.performance.util.PerformanceTest;
import pl.training.performance.util.datasource.DataSourceAdapter;
import pl.training.performance.entity.Post;
import pl.training.performance.entity.PostComment;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReadWriteTest extends PerformanceTest {

    private static final int SAMPLE_SIZE = 10_000;

    public ReadWriteTest(DataSourceAdapter dataSourceAdapter) {
        super(dataSourceAdapter);
    }

    @Test
    public void testQuery() {
        Post post = new Post("Test title", "Test text");
        PostComment postComment = new PostComment();
        postComment.setText("Comment");
        post.getComments().add(postComment);
        postComment.setPost(post);
        withEntityManager(entityManager -> entityManager.persist(post));

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Timer timer = metricRegistry.timer(dataSourceAdapter.getClass().getSimpleName());
        long startTime = System.nanoTime();

        List<PostComment> postComments = entityManager
                .createQuery("select pc from PostComment pc join fetch pc.post p", PostComment.class)
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
