package ru.ifmo.mailru.core;

import ru.ifmo.mailru.priority.ModulePrioritization;

import java.io.IOException;


public class PageProcessor implements Runnable {

	private Page page;
	private Controller controller;
    private ModulePrioritization prioritization;


    public PageProcessor(WebURL url, Controller controller, ModulePrioritization prioritization) {
		this.page = new Page(url);
		this.controller = controller;
        this.prioritization = prioritization;
	}

	private boolean load() {
		/*while (!page.getUrl().getHostController().canRequest()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}  */
		try {
			page.getUrl().getHostController().request();
            String content = ContentLoader.loadContent(page.getUrl().getUri(), false, 5);
			page.getUrl().getHostController().request();
			page.setContent(content);
			return true;
		} catch (IOException e) {
            System.err.println("Load exception for page: " + page.getUrl().getUri());
			return false;
		}
	}

	@Override
	public void run() {
		if (!load()) {
			return;
		}
		PageParser.parse(page);
        prioritization.setPriorities(page);
		controller.addAll(page.getOutLinks());
		controller.setCrawledURL(page.getUrl());
	}
}
