package ru.ifmo.mailru.core;

import ru.ifmo.mailru.priority.ModulePrioritization;

import java.io.IOException;

public class PageProcessor implements Runnable {

    private Page page;
    private Controller controller;
    private ModulePrioritization prioritization;
    private static int count = 0;

    public PageProcessor(WebURL url, Controller controller, ModulePrioritization prioritization) {
        this.page = new Page(url);
        this.controller = controller;
        this.prioritization = prioritization;
    }

    private void load() throws IOException {
    /*	while (!page.getUrl().getHostController().canRequest()) {
            try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}       */
//            page.getUrl().getHostController().request();
        page.getUrl().getHostController().lock.lock();
        String content;
        try {
            ContentLoader loader = new ContentLoader(page.getUrl().getUri(), 5);
            content = loader.loadWebPage();
        } catch (Exception e) {
            throw e;
        } finally {
            page.getUrl().getHostController().lock.unlock();
        }
        page.setContent(content);
        //     page.getUrl().getHostController().request();
    }

    @Override
    public void run() {
       // System.out.println(++count + " " + page.getUrl().getUri().toString());
        try {
            processingWebPage();
            prioritization.setPriorities(page);
            controller.addAll(page.getOutLinks());
            controller.setCrawledURL(page.getUrl());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            controller.setFailedPage(page.getUrl(), e.getMessage());
        } finally {
         //   System.out.println(--count + " " + page.getUrl().getUri().toString());
        }
    }

    void processingWebPage() throws IOException  {
        load();
        PageParser.parse(page);
    }
}

