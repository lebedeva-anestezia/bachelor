package ru.ifmo.mailru.core;

import ru.ifmo.mailru.priority.PrioritizationModule;
import ru.ifmo.mailru.util.ValueComparator;

import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Anastasia Lebedeva
 */
public class CollectionHandler {
    private ConcurrentHashMap<String, WebURL> urlMap = new ConcurrentHashMap<>();
    private Set<WebURL> collection = Collections.newSetFromMap(new ConcurrentHashMap<WebURL, Boolean>());
    //private Set<WebURL> failedPages = Collections.newSetFromMap(new ConcurrentHashMap<WebURL, Boolean>());
    //private Set<WebURL> crawledPages = Collections.newSetFromMap(new ConcurrentHashMap<WebURL, Boolean>());
    private ConcurrentHashMap<String, HostController> hostMap = new ConcurrentHashMap<>();
    public final PrioritizationModule prioritizationModule;
    public LogWriter logWriter;
    public final int MAX_COUNT_SITE = 5000;
    public final int MAX_COUNT = 250000;

    public CollectionHandler(PrioritizationModule prioritizationModule, Set<WebURL> startSet, LogWriter logWriter) {
        this(prioritizationModule, startSet);
        this.logWriter = logWriter;
    }

    public CollectionHandler(PrioritizationModule prioritizationModule, Set<WebURL> startSet) {
        this.prioritizationModule = prioritizationModule;
        for (WebURL webURL : startSet) {
            add(webURL, null);
        }
    }

    public void addCrawledPage(Page page) {
        page.getUrl().getHostController().incNumber();
        for (String url : page.getOutLinks()) {
            try {
                WebURL webURL = urlMap.get(url);
                if (webURL == null) {
                    webURL = new WebURL(url);
                    add(webURL, page);
                } else {
                    prioritizationModule.resetQualityRanks(webURL, page);
                }
            } catch (URISyntaxException e) {

            }
        }
        if (logWriter != null) {
            logWriter.setCrawledURL(page.getUrl());
        }
    }

    private void add(WebURL webURL, Page page) {
        if (collection.size() > 2 * MAX_COUNT) {
            return;
        }
        String host = webURL.getUri().getHost();
        if (hostMap.size() > MAX_COUNT_SITE && !hostMap.contains(host)) {
            return;
        }
        HostController hostController = new HostController(host);
        HostController hc = hostMap.putIfAbsent(host, hostController);
        if (hc == null) {
            hostController.addPolitenessModule();
        } else {
            hostController = hc;
        }
        webURL.setHostController(hostController);
        if (!hostController.canAdd(webURL.getUri())) {
            return;
        }
        WebURL webUrlFromMap = urlMap.putIfAbsent(webURL.getUri().toString(), webURL);
        if (webUrlFromMap == null) {
            collection.add(webURL);
            prioritizationModule.setQualityRanks(webURL, page);
        }
    }

    public void addFailedPage(Page page, String exception) {
        collection.remove(page.getUrl());
        if (logWriter != null) {
            logWriter.setFailedPage(page.getUrl(), exception);
        }
    }

    public Set<WebURL> getCollection() {
        return collection;
    }

    public List<WebURL> getNextPart(Scheduler scheduler) {
        Map<WebURL, Double> rangesMap = new HashMap<>();
        ValueComparator<WebURL, Double> comparator = new ValueComparator<>(rangesMap);
        TreeMap<WebURL, Double> orderedMap = new TreeMap<>(comparator);
        for (WebURL url : getCollection()) {
            rangesMap.put(url, prioritizationModule.computeVisitRank(url));
        }
        orderedMap.putAll(rangesMap);
        Map<HostController, Integer> counter = new HashMap<>();
        int count = 0;
        List<WebURL> part = new LinkedList<>();
        for (WebURL url : orderedMap.keySet()) {
            Integer curPageFromSiteObj = counter.get(url.getHostController());
            int curPageFromSite;
            if (curPageFromSiteObj == null) {
                curPageFromSite = 0;
            } else {
                curPageFromSite = curPageFromSiteObj;
            }
            if (curPageFromSite == scheduler.MAX_PAGE_COUNT_FROM_SITE) {
                continue;
            }
            part.add(url);
            counter.put(url.getHostController(), curPageFromSite + 1);
            count++;
            if (count == scheduler.PART_SIZE) {
                break;
            }
        }
        return part;
    }
}

