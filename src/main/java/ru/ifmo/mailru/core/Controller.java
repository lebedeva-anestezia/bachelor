package ru.ifmo.mailru.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Anastasia Lebedeva
 */

public class Controller {
	private static Map<String, HostController> hostMap = new ConcurrentHashMap<>();
    private Set<String> crawled = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    private Set<String> failed = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	private Set<WebURL> inQueue = Collections.newSetFromMap(new ConcurrentHashMap<WebURL, Boolean>());
    private Set<WebURL> inProcessing = Collections.newSetFromMap(new ConcurrentHashMap<WebURL, Boolean>());
    private TreeSet<WebURL> toCrawl = new TreeSet<>(Collections.reverseOrder());
    public final int MAX_PAGE = 1000000000;
    private PrintWriter failedPagePrintWriter;
    private PrintWriter crawledPrintWriter;
    private String queueLogFile;
    private AtomicInteger counter = new AtomicInteger(0);

    public Controller(File startFile) throws FileNotFoundException {
        Set<WebURL> set = new LinkedHashSet<>();
        Scanner scanner = new Scanner(startFile);
        while (scanner.hasNext()) {
            try {
                set.add(new WebURL(scanner.nextLine(), 1));
            } catch (URISyntaxException e) {
                System.err.println(e.getMessage());
            }
        }
        addAll(set);
    }

    public Controller(File queueFile, File crawledPages) throws FileNotFoundException {
        Scanner scanner = new Scanner(queueFile);
        while (scanner.hasNext()) {
            String s = null;
            try {
                s = scanner.nextLine();
                String[] arr = s.split(" ");
                WebURL url = new WebURL(new URI(arr[0]), Double.valueOf(arr[1]));
                addHostController(url);
                try {
                    add(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (URISyntaxException e) {
                System.err.println("Illegal URI syntax: " + s);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println(e.getMessage() + " in " + s);
            }
        }
        scanner = new Scanner(crawledPages);
        while (scanner.hasNext()) {
            String s = scanner.nextLine();
            try {
                WebURL url = new WebURL(s);
                addHostController(url);
                setCrawledURL(url);
            } catch (URISyntaxException e) {
                System.err.println("Illegal URI syntax: " + s);
            }
        }
    }

    public void setCrawledLogging(PrintWriter crawledPrintWriter) {
        this.crawledPrintWriter = crawledPrintWriter;
    }

    public void setQueueLogFile(String queueLogFile) {
        this.queueLogFile = queueLogFile;
    }

    public void setFailedLogging(PrintWriter failed) throws FileNotFoundException {
        this.failedPagePrintWriter = failed;
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
                     inProcessing.contains(next) || failed.contains(next.getUri().toString())
                                                 || next.getHostController().isItTooMuch());
        }
        inProcessing.add(next);
        inQueue.remove(next);
        return next;
    }


	public void addAll(Set<WebURL> urls) {
		for (WebURL url: urls) {
            try {
                addHostController(url);
                addPolitenessModule(url);
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
            toCrawl.add(url);
        }
        inQueue.add(url);
        if (counter.incrementAndGet() == 50000) {
            counter.set(0);
            reorder();
        }
		return true;
	}

    private void reorder() {
        synchronized (toCrawl) {
            toCrawl.clear();
            for (WebURL url : inQueue) {
                toCrawl.add(url);
            }
        }
        if (queueLogFile != null) {
            makeSnapshot();
        }
    }

    public void addPolitenessModule(WebURL url) throws URISyntaxException {
        url.getHostController().addPolitenessModule();
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

    public void setFailedPage(WebURL url, String exception) {
        failed.add(url.getUri().toString());
        inProcessing.remove(url);
        if (failedPagePrintWriter == null) {
            return;
        }
        synchronized (failedPagePrintWriter) {
            failedPagePrintWriter.println(url.getUri().toString() + " " + exception);
            failedPagePrintWriter.flush();
        }
    }
	
	public void setCrawledURL(WebURL url) {
        url.getHostController().incNumber();
		crawled.add(url.getUri().toString());
		inProcessing.remove(url);
        if (crawledPrintWriter == null) {
            return;
        }
        synchronized (crawledPrintWriter) {
            crawledPrintWriter.println(url.getUri());
            crawledPrintWriter.flush();
        }
	}

    void makeSnapshot() {
        try {
            File newQueue = new File(queueLogFile + "tmp");
            PrintWriter printWriter = new PrintWriter(newQueue);
            synchronized (toCrawl) {
                for (WebURL webURL : toCrawl) {
                    printWriter.println(webURL.getUri().toString() + " " + webURL.getRank());
                }
            }
            printWriter.close();
            File queueLog = new File(queueLogFile);
            queueLog.delete();
            newQueue.renameTo(queueLog);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean isAllowHost(URI uri) {
        return hostMap.size() < 500 || hostMap.containsKey(uri.getHost());
    }
}
