package ru.ifmo.mailru.core;

import java.io.IOException;

public class PageProcessingTask implements Runnable {

    private QueueHandler queueHandler;
    private Page page;

    public PageProcessingTask(Page page, QueueHandler queueHandler) {
        this.page = page;
        this.queueHandler = queueHandler;
    }


    protected void load() throws IOException {
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
    }

    @Override
    public void run() {
        try {
            load();
            if (page.isModified()) {
                page.getUrl().decreasePeriod();
            } else {
                page.getUrl().increasePeriod();
            }
            page.getUrl().setLastVisitTime(System.currentTimeMillis());
            PageParser.extractLinks(page);
            queueHandler.addCrawledPage(page);
        } catch (Exception e) {
            queueHandler.addFailedPage(page, e.getMessage());
        }
    }
}

