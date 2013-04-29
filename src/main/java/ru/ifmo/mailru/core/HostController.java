package ru.ifmo.mailru.core;

import ru.ifmo.mailru.robottxt.PolitenessModule;

import java.net.URI;

public class HostController {
	private String host;
	private long lastRequest;
	private boolean canRequest;
	private long interval = 1000;
    private PolitenessModule politenessModule;
	
	public HostController(String host) {
		this.host = host;
		lastRequest = 0;
		canRequest = true;
    }
	
	public synchronized boolean canRequest() {
		canRequest = System.currentTimeMillis() - lastRequest > interval;
		return canRequest;
	}
	
	public synchronized void request() {
		canRequest = false;
		lastRequest = System.currentTimeMillis();
	}

    public boolean checkAllow(URI uri) {
        return politenessModule.isAllow(uri);
    }
}
