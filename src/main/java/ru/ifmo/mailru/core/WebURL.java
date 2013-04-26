package core;

import java.net.URI;



public class WebURL implements Comparable<WebURL> {
	private URI uri;

    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    private String fragment;
	private HostController hostController;
    private double rank;

    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }

	public URI getUri() {
		return uri;
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
        return new Double(rank).compareTo(o.rank);
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