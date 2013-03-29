package core;

import java.net.URI;



public class WebURL {
	private URI uri;
	private String fragment;
	private HostController hostController;
	
	@Override
	public boolean equals(Object o) {
		if (!o.getClass().equals(this.getClass())) {
			return false;
		}
		return uri.equals(((WebURL) o).getUri());
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

	public String getFragment() {
		return fragment;
	}

	public void setFragment(String fragment) {
		this.fragment = fragment;
	}
	
	
}
