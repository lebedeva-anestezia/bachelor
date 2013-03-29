package test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import core.Spider;
import core.WebURL;

public class SimpleTest {
	public static void main(String[] args) {
		WebURL url = new WebURL();
		try {
			url.setUri(new URI("http://docs.oracle.com/javase/1.4.2/docs/api/java/net/URI.html"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		Set<WebURL> list = new HashSet<WebURL>();
		list.add(url);
		Spider spider = new Spider(list);
		spider.run();
	}
}
