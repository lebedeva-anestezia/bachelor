package ru.ifmo.mailru.util;

import java.util.concurrent.*;

/**
 * @author Anastasia Lebedeva
 */
public class TimeOutFixedThreadPullExecutor {
    private final ExecutorService exec;
    private final Semaphore semaphore;

    public TimeOutFixedThreadPullExecutor(int bound) {
        this.exec = Executors.newCachedThreadPool();
        this.semaphore = new Semaphore(bound);
    }

    public void submitTask(final Runnable command, long timeOut, TimeUnit timeUnit)
            throws InterruptedException, RejectedExecutionException, TimeoutException, ExecutionException {
        semaphore.acquire();
        try {
            Future future = exec.submit(new Runnable() {
                public void run() {
                    try {
                        command.run();
                    } finally {
                        semaphore.release();
                    }
                }
            });
            future.get(timeOut, timeUnit);
        } catch (RejectedExecutionException | ExecutionException | TimeoutException  e) {
            semaphore.release();
            throw e;
        }
    }
}
