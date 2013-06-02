package ru.ifmo.mailru.core;

import ru.ifmo.mailru.priority.ModulePrioritization;
import ru.ifmo.mailru.util.ValueComparator;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Anastasia Lebedeva
 */
public class Crawler implements Runnable {
    private static final int POOL_SIZE = 20;
    public final int maxPageCountFromSite = 50;
    public final int frontierSize = 1000;
    private Storage storage;
    private ModulePrioritization prioritization;
    private Thread curThread;

    public Crawler(Storage storage, ModulePrioritization prioritization) {
        this.storage = storage;
        this.prioritization = prioritization;
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
            for (WebURL url : storage.collection) {
                rangesMap.put(url, prioritization.computeUpdatingProbability(url));
            }
            Map<HostController, Integer> counter = new HashMap<>();
            ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);
            int count = 0;
            for (WebURL url : orderedMap.keySet()) {
                Integer curPageFromSite = counter.get(url.getHostController());
                if (curPageFromSite == maxPageCountFromSite) {
                    continue;
                }
                counter.put(url.getHostController(), curPageFromSite + 1);
                count++;
                executor.submit(new PageProcessor(new Page(url), this));
                if (count == frontierSize) {
                    break;
                }
            }
        }
    }

    public void submitResult(Page page) {
        if (page.isCompleted()) {
            storage.addCrawledPage(page);
        } else {
            storage.addFailedPage(page);
        }
    }
}
