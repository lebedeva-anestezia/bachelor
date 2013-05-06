package ru.ifmo.mailru.core;

import ru.ifmo.mailru.priority.ModulePrioritization;

import java.io.PrintWriter;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Spider implements Runnable {

    private PrintWriter pw;
	private Controller controller = new Controller();
	private ExecutorService pool;
    private ModulePrioritization modulePrioritization;
	//private final int POOL_SIZE = 10;
    private Thread curThread;

    public Spider(ModulePrioritization modulePrioritization, Set<WebURL> URLSet, PrintWriter pw) {
        this(URLSet);
        this.pw = pw;
        this.modulePrioritization = modulePrioritization;
    }

    public Spider(Set<WebURL> URLSet) {
		controller.addAll(URLSet);
		pool = Executors.newFixedThreadPool(50);
	}

    public void start() {
        curThread = new Thread(this);
        curThread.start();
    }

    public void stop() {
        curThread = null;
    }

	@Override
	public void run() {
        Thread thisThread = Thread.currentThread();
        try {
            while (curThread == thisThread) {
                WebURL next = controller.nextURL();
                while (next == null) {
                    Thread.sleep(1000);
                    next = controller.nextURL();
                }
                pool.execute(new PageProcessor(next, controller, modulePrioritization));
                pw.println(next.getUri().toString());
                System.out.println(next.getUri().toString());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
}
