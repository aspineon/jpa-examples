package pl.training.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int users = 10;
        CountDownLatch countDownLatch = new CountDownLatch(users);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(users);
        List<Future> results = new ArrayList<>();
        Task task = new Task(countDownLatch);
        for (int user = 1; user <= users; user++) {
            results.add(executor.submit(task));
        }
        countDownLatch.await();
        for (int user = 0; user <= users - 1; user++) {
            System.out.format("No: %d, result: %d\n", user, results.get(user).get());
        }
        executor.shutdown();
    }

}
