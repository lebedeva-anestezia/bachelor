package ru.ifmo.mailru.core;

import java.net.URI;
import java.net.URISyntaxException;


public class WebURL {

    private URI uri;
    private HostController hostController;
    private double qualityRank;
    private long lastVisitTime;

    public long getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(long modificationTime) {
        this.modificationTime = modificationTime;
    }

    private long modificationTime;

    public WebURL(){}

    public WebURL(URI uri, double qualityRank) {
        this.uri = uri;
        this.qualityRank = qualityRank;
    }

    public WebURL(String uri, double qualityRank) throws URISyntaxException {
        this(new URI(uri), qualityRank);
    }

    public void setLastVisitTime(long lastVisitTime) {
        this.lastVisitTime = lastVisitTime;
    }

    public long getLastVisitTime() {
        return lastVisitTime;
    }

    public double getQualityRank() {
        return qualityRank;
    }

    public void setQualityRank(double qualityRank) {
        this.qualityRank = qualityRank;
    }

	public URI getUri() {
		return uri;
	}

    public WebURL(String uri) throws URISyntaxException {
        this(new URI(uri));
    }

    public WebURL(URI uri) {
        this.uri = uri;
    }

    public void setUri(URI uri) {
		this.uri = uri;
	}

	public HostController getHostController() {
		return hostController;
	}

	public void setHostController(HostController hostController) {
		this.hostController = hostController;
	}

    @Override
    public int hashCode() {
        return uri.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WebURL)) return false;

        WebURL url = (WebURL) o;

        if (!uri.toString().equals(url.uri.toString())) return false;

        return true;
    }
}
