package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



public class PageProcessor implements Runnable {

	private Page page;
	private Controller controller;

	public PageProcessor(WebURL url, Controller controller) {
		this.page = new Page(url);
		this.controller = controller;
	}

	private boolean load() {
		while (!page.getUrl().getHostController().canRequest()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			page.getUrl().getHostController().request();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					page.getUrl().getUri().toURL().openStream()));
			StringBuilder sb = new StringBuilder();
			String s;
			while ((s = reader.readLine()) != null) {
				sb.append(s);
			}
			page.getUrl().getHostController().request();
			page.setContent(sb.toString());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void run() {
		if (!load()) {
			return;
		}
		PageParser.parse(page);
		controller.addAll(page.getOutLinks());
		controller.setCrawledURL(page.getUrl());
	}

}