package ru.ifmo.mailru.core;

import ru.ifmo.mailru.priority.ModulePrioritization;

import java.io.IOException;

public class PageProcessor extends CrawlModule {

    public PageProcessor(WebURL url, Controller controller, ModulePrioritization prioritization) {
        super(url, controller, prioritization);
    }

    @Override
    public void run() {
        try {
            processingWebPage();
            prioritization.setPriorities(page);
            controller.addAll(page.getOutLinks());
            controller.setCrawledURL(page);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            controller.setFailedPage(page.getUrl(), e.getMessage());
        }
    }

    void processingWebPage() throws IOException  {
        load();
        PageParser.parse(page);
    }
}

