package ru.ifmo.mailru.test;

import core.Spider;
import core.WebURL;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

public class SimpleTest {
	public static void main(String[] args) {
		WebURL url = new WebURL();
		try {
			url.setUri(new URI("http://pogoda.yandex.ru/saint-petersburg/"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		Set<WebURL> list = new HashSet<WebURL>();
		list.add(url);
		Spider spider = new Spider(list);
		spider.run();
	}
}
