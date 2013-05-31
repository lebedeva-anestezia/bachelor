package ru.ifmo.mailru.core;

import ru.ifmo.mailru.priority.ModulePrioritization;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class Spider implements Runnable {

	private Controller controller;
    private ModulePrioritization modulePrioritization;
	private final int POOL_SIZE = 20;
    private Thread curThread;
//    private TimeOutFixedThreadPullExecutor executor = new TimeOutFixedThreadPullExecutor(POOL_SIZE);
    private Executor standardExecutor = Executors.newFixedThreadPool(POOL_SIZE);

    public Spider(Controller controller, ModulePrioritization modulePrioritization) {
        this.controller = controller;
        this.modulePrioritization = modulePrioritization;
    }

    public void start() {
        curThread = new Thread(this);
        curThread.start();
    }

    public void stop() {
        controller.makeSnapshot();
        curThread = null;
    }

    @Override
    public void run() {
        Thread thisThread = Thread.currentThread();
        int n = 0;
        while (curThread == thisThread) {
            WebURL next = controller.nextURL();
            if (next == null) continue;
            standardExecutor.execute(new PageProcessor(next, controller, modulePrioritization));
            n++;
            if (controller.getIndexSize() > controller.getMaxPageCount()) {
                stop();
            }
        }
    }
}
