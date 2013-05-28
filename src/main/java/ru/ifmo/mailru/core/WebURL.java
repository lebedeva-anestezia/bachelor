package ru.ifmo.mailru.core;

import java.net.URI;
import java.net.URISyntaxException;


public class WebURL implements Comparable<WebURL> {

    private URI uri;
    private HostController hostController;
    private String fragment;
    private double rank;

    public WebURL(){}

    public WebURL(URI uri, double rank) {
        this.uri = uri;
        this.rank = rank;
    }

    public WebURL(String uri, double rank) throws URISyntaxException {
        this(new URI(uri), rank);
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
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
    public int compareTo(WebURL o) {
        //if (new Double(rank).compareTo(o.rank) == 0)
          //  return uri.compareTo(o.uri);
        //return new Double(rank).compareTo(o.rank);
        if (new Double(rank).compareTo(o.rank) >= 0) {
            return 1;
        }
        return -1;
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
