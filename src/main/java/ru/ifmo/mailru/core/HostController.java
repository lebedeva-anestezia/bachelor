package ru.ifmo.mailru.core;

import ru.ifmo.mailru.robottxt.PolitenessModule;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HostController {
	private String host;
	private long lastRequest;
	private boolean canRequest;
	private long interval = 1000;
    private PolitenessModule politenessModule;
    private boolean tryAddedPolitenessModule;
    private int pageNumber;
    public static final int maxCount = 500;
	
	public HostController(String host) throws URISyntaxException {
        this.pageNumber = 0;
		this.host = host;
		lastRequest = 0;
		canRequest = true;
        tryAddedPolitenessModule = false;
    }

    public void addPolitenessModule() throws URISyntaxException {
        if (tryAddedPolitenessModule) {
            return;
        }
        try {
            this.politenessModule = new PolitenessModule(host);
        } catch (IOException e) {
        }
        tryAddedPolitenessModule = true;
    }
	
	public synchronized boolean canRequest() {
		canRequest = System.currentTimeMillis() - lastRequest > interval;
		return canRequest;
	}
	
	public synchronized void request() {
		canRequest = false;
		lastRequest = System.currentTimeMillis();
	}

    public boolean isItTooMuch() {
        return pageNumber >= maxCount;
    }

    public void incNumber() {
        pageNumber++;
    }

    public boolean canAdd(URI uri) {
        boolean ret = false;
        if (!isItTooMuch()) {
            ret = politenessModule == null || politenessModule.isAllow(uri);
        }
        return ret;
    }
}
