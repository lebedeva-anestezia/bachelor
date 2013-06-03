package ru.ifmo.mailru.core;

import java.util.Set;

public class Page {
	
	private String content;
	private WebURL url;
	private Set<String> outLinks;
	
	public Page(WebURL url) {
		this.url = url;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String textContent) {
		this.content = textContent;
	}
	public WebURL getUrl() {
		return url;
	}
	public Set<String> getOutLinks() {
		return outLinks;
	}
	public void setOutLinks(Set<String> outLinks) {
		this.outLinks = outLinks;
	}

    public boolean isModified() {
        int hashCode = content.hashCode();
        return hashCode != url.getContentHashCode();
    }
}
