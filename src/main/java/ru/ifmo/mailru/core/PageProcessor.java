package ru.ifmo.mailru.core;

import ru.ifmo.mailru.priority.ModulePrioritization;

import java.io.IOException;
import java.io.PrintWriter;

public class PageProcessor implements Runnable {

    private Page page;
    private Controller controller;
    private ModulePrioritization prioritization;
    private PrintWriter printWriter;

    public PageProcessor(WebURL url, Controller controller, ModulePrioritization prioritization, PrintWriter printWriter) {
        this(url, controller, prioritization);
        this.printWriter = printWriter;
    }


    public PageProcessor(WebURL url, Controller controller, ModulePrioritization prioritization) {
        this.page = new Page(url);
        this.controller = controller;
        this.prioritization = prioritization;
    }

    private boolean load() throws IOException {
		/*while (!page.getUrl().getHostController().canRequest()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}  */
//            page.getUrl().getHostController().request();
            ContentLoader loader = new ContentLoader(page.getUrl().getUri(), 5);
            String content = loader.loadWebPage();
       //     page.getUrl().getHostController().request();
            page.setContent(content);
            return true;
    }

    @Override
    public void run() {
        try {
            processingWebPage();
            prioritization.setPriorities(page);
            controller.addAll(page.getOutLinks());
            controller.setCrawledURL(page.getUrl());
            synchronized (printWriter) {
                printWriter.println(page.getUrl().getUri());
                printWriter.flush();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            //e.printStackTrace();
            controller.setFailedPage(page.getUrl(), e.getMessage());
        }
    }

    void processingWebPage() throws IOException {
        load();
        PageParser.parse(page);
    }
}

