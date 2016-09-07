package pl.training.performance;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class Task implements Callable<Integer> {

    private CountDownLatch countDownLatch;

    public Task(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public Integer call() throws Exception {
        Random random = new Random();
        Thread.sleep(random.nextInt(2000));
        countDownLatch.countDown();
        return random.nextInt();
    }

}
