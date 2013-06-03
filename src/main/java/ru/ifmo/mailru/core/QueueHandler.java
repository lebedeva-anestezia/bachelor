package ru.ifmo.mailru.core;

import ru.ifmo.mailru.priority.ModulePrioritization;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Anastasia Lebedeva
 */
public class QueueHandler {
    private ConcurrentHashMap<String, WebURL> urlMap = new ConcurrentHashMap<>();
    private Set<WebURL> collection = Collections.newSetFromMap(new ConcurrentHashMap<WebURL, Boolean>());
    //private Set<WebURL> failedPages = Collections.newSetFromMap(new ConcurrentHashMap<WebURL, Boolean>());
    //private Set<WebURL> crawledPages = Collections.newSetFromMap(new ConcurrentHashMap<WebURL, Boolean>());
    private ConcurrentHashMap<String, HostController> hostMap = new ConcurrentHashMap<>();
    public final ModulePrioritization prioritization;
    public LogWriter logWriter;
    public final int MAX_COUNT_SITE = 10000;

    public QueueHandler(ModulePrioritization prioritization, Set<WebURL> startSet, LogWriter logWriter) {
        this(prioritization, startSet);
        this.logWriter = logWriter;
    }

    public QueueHandler(ModulePrioritization prioritization, Set<WebURL> startSet) {
        this.prioritization = prioritization;
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
                    prioritization.resetQualityRanks(webURL, page);
                }
            } catch (URISyntaxException e) {

            }
        }
        if (logWriter != null) {
            logWriter.setCrawledURL(page.getUrl());
        }
    }

    private void add(WebURL webURL, Page page) {
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
            prioritization.setQualityRanks(webURL, page);
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
}

