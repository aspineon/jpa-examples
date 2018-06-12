package pl.training.performance.jpa;

import javax.persistence.EntityManager;
import java.util.concurrent.CountDownLatch;

public interface UserTask extends Runnable {

    void setEntityManager(EntityManager entityManager);

    void setCountDownLatch(CountDownLatch countDownLeach);

}
