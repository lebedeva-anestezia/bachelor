package core;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Spider implements Runnable {
	
	private Controller controller = new Controller();
	private ExecutorService pool;
	private final int POOL_SIZE = 10;
	
	public Spider(Set<WebURL> URLSet) {
		controller.addAll(URLSet);
		pool = Executors.newFixedThreadPool(POOL_SIZE);
	}

	@Override
	public void run() {
		Queue<WebURL> queue = controller.getToCrawl();
		while (true) {
			if (queue.isEmpty()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				pool.execute(new PageProcessor(queue.poll(), controller));
			}
		}
	}

}
