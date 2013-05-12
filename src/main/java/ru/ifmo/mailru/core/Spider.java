package ru.ifmo.mailru.core;

import ru.ifmo.mailru.priority.ModulePrioritization;
import ru.ifmo.mailru.util.TimeOutFixedThreadPullExecutor;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class Spider implements Runnable {

    private PrintWriter pw;
	private Controller controller;
    private ModulePrioritization modulePrioritization;
	private final int POOL_SIZE = 20;
    private Thread curThread;
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
        try {
            controller = new Controller();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
        controller.addAll(URLSet);
		//pool = Executors.newFixedThreadPool(10);
	}

    public void start() {
        curThread = new Thread(this);
     /*   Thread lever = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (System.in.read() == '\n') {
                            stop();
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        lever.start();   */
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
                controller.setFailedPage(next, "TimeOut");
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
	}
}
