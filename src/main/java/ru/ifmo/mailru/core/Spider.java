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
        int n = 0;
        while (curThread == thisThread) {
            WebURL next = controller.nextURL();
            if (next == null) continue;
            pool.execute(new PageProcessor(next, controller, modulePrioritization));
            pw.println(next.getUri().toString());
            n++;
            if (n % 1000 == 0) {
                System.out.println(n);
            }
           // System.out.println(next.getUri().toString());
        }
	}
}
