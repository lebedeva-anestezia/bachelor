package ru.ifmo.mailru.core;

import ru.ifmo.mailru.priority.ModulePrioritization;
import ru.ifmo.mailru.util.TimeOutFixedThreadPullExecutor;

import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;


public class Spider implements Runnable {

    private PrintWriter pw;
	private Controller controller = new Controller();
	private ExecutorService pool;
    private ModulePrioritization modulePrioritization;
	private final int POOL_SIZE = 10;
    private Thread curThread;
    AtomicLong n = new AtomicLong(0);
    private TimeOutFixedThreadPullExecutor executor = new TimeOutFixedThreadPullExecutor(POOL_SIZE);

    public Spider(ModulePrioritization modulePrioritization, Set<WebURL> URLSet, PrintWriter pw, Scanner sc) {
        this(modulePrioritization, URLSet, pw);
        while (sc.hasNext()) {
            try {
                String uri = sc.nextLine();
                WebURL url = new WebURL(uri);
                controller.setCrawledURL(url);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        System.out.println("DONE");
    }

    public Spider(ModulePrioritization modulePrioritization, Set<WebURL> URLSet, PrintWriter pw) {
        this(URLSet);
        this.pw = pw;
        this.modulePrioritization = modulePrioritization;
    }


    public Spider(Set<WebURL> URLSet) {
		controller.addAll(URLSet);
		pool = Executors.newFixedThreadPool(10);
	}

    public void start() {
        curThread = new Thread(this);
        curThread.start();
    }

    public void stop() {
        curThread = null;
    }

    class SpiderRunner implements Runnable {
        Future future;
        String uri;

        SpiderRunner(Future future, String uri) {
            this.future = future;
            this.uri = uri;
        }

        @Override
        public void run() {
            try {
                future.get(1, TimeUnit.MINUTES);
                synchronized (pw) {
                    pw.println(uri);
                    pw.flush();
                }
                System.out.println(n.incrementAndGet());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                System.err.println("TimeOut: " + uri);
            }
        }
    }

	@Override
	public void run() {
        Thread thisThread = Thread.currentThread();
        int n = 0;
        while (curThread == thisThread) {
            WebURL next = controller.nextURL();
            if (next == null) continue;
            try {
                executor.submitTask(new PageProcessor(next, controller, modulePrioritization, pw), 1, TimeUnit.MINUTES);
                n++;
                if (n % 5000 == 0) {
                    System.out.println(n);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                System.err.println("TimeOut: " + next.getUri().toString());
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            //Future f = pool.submit(new PageProcessor(next, controller, modulePrioritization));
            //new Thread(new SpiderRunner(f, next.getUri().toString())).start();
        }
	}
}
