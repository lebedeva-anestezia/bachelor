package ru.ifmo.mailru.core;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Anastasia Lebedeva
 */
public class Storage {

    Set<WebURL> collection = Collections.newSetFromMap(new ConcurrentHashMap<WebURL, Boolean>());
    private Set<WebURL> failedPages = Collections.newSetFromMap(new ConcurrentHashMap<WebURL, Boolean>());
    private Set<WebURL> crawledPages = Collections.newSetFromMap(new ConcurrentHashMap<WebURL, Boolean>());

    public void addCrawledPage(Page page) {
        //
    }

    public void addFailedPage(Page page) {
        //To change body of created methods use File | Settings | File Templates.
    }
}
