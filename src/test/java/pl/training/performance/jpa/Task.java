package pl.training.performance.jpa;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import pl.training.performance.entity.Post;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

@RequiredArgsConstructor
public class Task implements Runnable {

    @NonNull
    private CountDownLatch countDownLatch;
    @NonNull
    private EntityManager entityManager;
    private Random random = new Random();

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        System.out.printf("User: %s started\n", name);
        try {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            Post post = entityManager.find(Post.class, 1L);
            System.out.printf("User: %s before lock\n", name);
            entityManager.lock(post, LockModeType.PESSIMISTIC_WRITE);
            System.out.printf("User: %s after lock\n", name);
            Thread.sleep(random.nextInt(5_000));
            post.setText("New test" + random.nextInt(1_000));
            System.out.printf("User: %s before commit\n", name);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        System.out.printf("User: %s finished\n", name);
        countDownLatch.countDown();
    }

}
