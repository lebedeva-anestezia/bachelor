package ru.ifmo.mailru.core;

import ru.ifmo.mailru.util.ValueComparator;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Anastasia Lebedeva
 */
public class Scheduler implements Runnable {
    private static final int POOL_SIZE = 20;
    public final int maxPageCountFromSite = 50;
    public final int frontierSize = 1000;
    private QueueHandler queueHandler;
    private Thread curThread;

    public Scheduler(QueueHandler queueHandler) {
        this.queueHandler = queueHandler;
    }

    public void start() {
        curThread = new Thread(this);
        curThread.start();
    }

    public void stop() {
        //controller.makeSnapshot();
        curThread = null;
    }

    @Override
    public void run() {
        Thread thisThread = Thread.currentThread();
        while (curThread == thisThread) {
            Map<WebURL, Double> rangesMap = new HashMap<>();
            ValueComparator<WebURL, Double> comparator = new ValueComparator<>(rangesMap);
            TreeMap<WebURL, Double> orderedMap = new TreeMap<>(comparator);
            for (WebURL url : queueHandler.getCollection()) {
                rangesMap.put(url, queueHandler.prioritization.computeVisitRank(url));
            }
            orderedMap.putAll(rangesMap);
            Map<HostController, Integer> counter = new HashMap<>();
            ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);
            int count = 0;
            for (WebURL url : orderedMap.keySet()) {
                Integer curPageFromSiteObj = counter.get(url.getHostController());
                int curPageFromSite;
                if (curPageFromSiteObj == null) {
                    curPageFromSite = 0;
                } else {
                    curPageFromSite = curPageFromSiteObj;
                }
                if (curPageFromSite == maxPageCountFromSite) {
                    continue;
                }
                counter.put(url.getHostController(), curPageFromSite + 1);
                count++;
                executor.submit(new PageProcessingTask(new Page(url), queueHandler));
                if (count == frontierSize) {
                    break;
                }
            }
            try {
                executor.shutdown();
                executor.awaitTermination(20, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                stop();
                e.printStackTrace();
            }
        }
    }
}
