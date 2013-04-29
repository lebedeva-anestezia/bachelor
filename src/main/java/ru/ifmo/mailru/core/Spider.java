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

    public Spider(ModulePrioritization modulePrioritization, Set<WebURL> URLSet, PrintWriter pw) {
        this(URLSet);
        this.pw = pw;
        this.modulePrioritization = modulePrioritization;
    }

    public Spider(Set<WebURL> URLSet) {
		controller.addAll(URLSet);
		pool = Executors.newCachedThreadPool();
	}


	@Override
	public void run() {
        while (true) {
            if (!controller.hasNext()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                WebURL url = controller.nextURL();
                pool.execute(new PageProcessor(url, controller, modulePrioritization));
                pw.println(url.getUri().toString());
            }
        }
	}
}
