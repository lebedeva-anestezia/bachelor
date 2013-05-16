package ru.ifmo.mailru.core;

import ru.ifmo.mailru.priority.ModulePrioritization;
import ru.ifmo.mailru.util.TimeOutFixedThreadPullExecutor;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class Spider implements Runnable {

	private Controller controller;
    private ModulePrioritization modulePrioritization;
	private final int POOL_SIZE = 20;
    private Thread curThread;
    private TimeOutFixedThreadPullExecutor executor = new TimeOutFixedThreadPullExecutor(POOL_SIZE);

    public Spider(Controller controller, ModulePrioritization modulePrioritization) {
        this.controller = controller;
        this.modulePrioritization = modulePrioritization;
    }

    public void start() {
        curThread = new Thread(this);
        Thread lever = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (System.in.read() == 10) {
                            stop();
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        lever.start();
        curThread.start();
    }

    public void stop() {
        controller.stop();
        curThread = null;
    }

    @Override
	public void run() {
        Thread thisThread = Thread.currentThread();
        int n = 0;
        while (curThread == thisThread) {
            WebURL next = controller.nextURL();
            if (next == null) continue;
            try {
                executor.submitTask(new PageProcessor(next, controller, modulePrioritization), 1, TimeUnit.MINUTES);
                n++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                System.err.println("TimeOut: " + next.getUri().toString());
                controller.setFailedPage(next, "TimeOut");
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
	}
}
