package ru.ifmo.mailru.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Anastasia Lebedeva
 */

public class Controller {
	private Map<String, HostController> hostMap = new ConcurrentHashMap<>();
    private Set<String> crawled = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    private Set<String> failed = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	private Set<WebURL> inQueue = Collections.newSetFromMap(new ConcurrentHashMap<WebURL, Boolean>());
    private Set<WebURL> inProcessing = Collections.newSetFromMap(new ConcurrentHashMap<WebURL, Boolean>());
    //private PriorityBlockingQueue<WebURL> toCrawl = new PriorityBlockingQueue<>();
    //private ConcurrentHashMap<URI, Double> ranks = new ConcurrentHashMap<>();
    private TreeSet<WebURL> toCrawl = new TreeSet<>(Collections.reverseOrder());
    public final int MAX_PAGE = 1000000000;
    private final PrintWriter failedPagePrintWriter;

    public Controller() throws FileNotFoundException {
        failedPagePrintWriter = new PrintWriter(new File("failed" + System.currentTimeMillis() + ".txt"));
    }

    public WebURL nextURL() {
        WebURL next;
        synchronized (toCrawl) {
            do {
                next = toCrawl.pollFirst();
                if (next == null) {
                    return null;
                }
            } while (crawled.contains(next.getUri().toString()) ||
                     inProcessing.contains(next) || failed.contains(next.getUri().toString()));
            //next = toCrawl.poll();
        }
        inProcessing.add(next);
        inQueue.remove(next);
        return next;
    }

	public void addAll(Set<WebURL> urls) {
		for (WebURL url: urls) {
            try {
                addHostController(url);
            } catch (URISyntaxException e) {
                System.err.println(e.getMessage());
            }
            try {
                add(url);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}

    private boolean tooMuch() {
        return inQueue.size() + crawled.size() > MAX_PAGE;
    }
	
	private boolean add(WebURL url) throws URISyntaxException, IOException {
        if (tooMuch()) {
            return false;
        }
		if (crawled.contains(url.getUri().toString()) || inProcessing.contains(url)) {
			return false;
		}
        /*synchronized (toCrawl) {
            if (ranks.get(url.getUri()) < url.getRank()) {
                ranks.put(url.getUri(), url.getRank());
            }
        } */
        if (!url.getHostController().canAdd(url.getUri())) {
            return false;
        }
        synchronized (toCrawl) {
            if (toCrawl.size() > MAX_PAGE) {
                if (url.compareTo(toCrawl.last()) > 0) {
                    toCrawl.pollLast();
                } else {
                    return false;
                }
            }
       //     url.setRank(ranks.get(url.getUri()))
            toCrawl.add(url);
        }
        inQueue.add(url);
        url.getHostController().incNumber();
		return true;
	}

    public void addHostController(WebURL url) throws URISyntaxException {
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

    public void setFailedPage(WebURL url) {
        failed.add(url.getUri().toString());
        inProcessing.remove(url);
        synchronized (failedPagePrintWriter) {
            failedPagePrintWriter.println(url.getUri().toString());
            failedPagePrintWriter.flush();
        }
    }
	
	public void setCrawledURL(WebURL url) {
        //url.getHostController().incNumber();
		crawled.add(url.getUri().toString());
		inProcessing.remove(url);
	}
}
