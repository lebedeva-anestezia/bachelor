package ru.ifmo.mailru.core;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author Anastasia Lebedeva
 */

public class Controller {
	private Map<String, HostController> hostMap = new ConcurrentHashMap<>();
    private Set<String> crawled = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	//private Set<WebURL> inQueue = Collections.newSetFromMap(new ConcurrentHashMap<WebURL, Boolean>());
    private Set<WebURL> inProcessing = Collections.newSetFromMap(new ConcurrentHashMap<WebURL, Boolean>());
    private ConcurrentHashMap<WebURL, Double> ranks = new ConcurrentHashMap<>();
    private PriorityBlockingQueue<WebURL> toCrawl = new PriorityBlockingQueue<>();

    public WebURL nextURL() {
        WebURL next = null;
        try {
            next = toCrawl.take();
            inProcessing.add(next);
            ranks.remove(next);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
        return next;
    }

	public void addAll(Set<WebURL> urls) {
		for (WebURL url: urls) {
            try {
                addHostController(url);
                add(url);
            } catch (URISyntaxException | IOException e ) {
                System.err.println(e.getMessage());
            }
        }
	}
	
	private boolean add(WebURL url) throws URISyntaxException, IOException {
		if (crawled.contains(url.getUri().toString()) || inProcessing.contains(url) || ranks.containsKey(url)) {
			return false;
		}
        if (!url.getHostController().canAdd(url.getUri())) {
            return false;
        }
        url.getHostController().incNumber();
        /*Double rank = ranks.get(url);
        if (rank != null) {
            toCrawl.remove(url);
            url.setRank(Math.min(url.getRank(), rank));
        }                              */
        ranks.put(url, url.getRank());
		toCrawl.add(url);
		return true;
	}

    private void addHostController(WebURL url) throws URISyntaxException {
        HostController hc;
        String curHost = url.getUri().getHost();
        if (hostMap.containsKey(curHost)) {
            hc = hostMap.get(curHost);
        } else {
            hc = new HostController(curHost);
            hostMap.put(curHost, hc);
        }
        url.setHostController(hc);
    }
	
	public void setCrawledURL(WebURL url) {
        //url.getHostController().incNumber();
		crawled.add(url.getUri().toString());
		inProcessing.remove(url);
	}
}
