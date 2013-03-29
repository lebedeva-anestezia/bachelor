package core;
import java.util.Set;

public class Page {
	
	private String content;
	private String title;
	private WebURL url;
	private Set<WebURL> outLinks;
	
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
	public Set<WebURL> getOutLinks() {
		return outLinks;
	}
	public void setOutLinks(Set<WebURL> outLinks) {
		this.outLinks = outLinks;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
