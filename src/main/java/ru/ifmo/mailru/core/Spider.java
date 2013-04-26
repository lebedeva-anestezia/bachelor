package ru.ifmo.mailru.core;

import ru.ifmo.mailru.priority.EmptyPrioritization;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Spider implements Runnable {

    private PrintWriter pw;
	private Controller controller = new Controller();
	private ExecutorService pool;
	private final int POOL_SIZE = 10;
	
	public Spider(Set<WebURL> URLSet) {
		controller.addAll(URLSet);
		pool = Executors.newFixedThreadPool(POOL_SIZE);
	}

	@Override
	public void run() {
        try {
            pw = new PrintWriter("output2.txt");
            while (true) {
                if (!controller.hasNext()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    WebURL url = controller.nextURL();
                    pool.execute(new PageProcessor(url, controller, new EmptyPrioritization()));
                    pw.println(url.getUri().toString());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            pw.close();
        }
	}
}
