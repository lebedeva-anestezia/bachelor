package core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;



public class Controller {
	private Map<String, HostController> hostMap = new ConcurrentHashMap<String, HostController>();
	private Set<String> crawled = Collections.synchronizedSet(new HashSet<String>());
	private Set<String> inProcessing = Collections.synchronizedSet(new HashSet<String>());
	private Queue<WebURL> toCrawl = new LinkedBlockingQueue<>();
	
	public void addAll(Set<WebURL> urls) {
		for (WebURL url: urls) {
			add(url);
		}
	}
	
	private synchronized boolean add(WebURL url) {
		if (crawled.contains(url.getUri().toString()) || inProcessing.contains(url.getUri().toString())) {
			return false;
		}
		toCrawl.add(url);
		inProcessing.add(url.getUri().toString());
		HostController hc;
		String curHost = url.getUri().getHost();
		if (hostMap.containsKey(curHost)) {
			hc = hostMap.get(curHost);
		} else {
			hc = new HostController(curHost);
			hostMap.put(curHost, hc);
		}
		url.setHostController(hc);
		return false;
	}
	
	public void setCrawledURL(WebURL url) {
		crawled.add(url.getUri().toString());
		inProcessing.remove(url.getUri().toString());
		System.out.println(url.getUri().toString());
	}

	public Queue<WebURL> getToCrawl() {
		return toCrawl;
	}
}
