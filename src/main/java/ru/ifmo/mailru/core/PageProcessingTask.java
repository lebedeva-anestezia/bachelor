package ru.ifmo.mailru.core;

import java.io.IOException;

public class PageProcessingTask implements Runnable {

    private CollectionHandler collectionHandler;
    private Page page;

    public PageProcessingTask(Page page, CollectionHandler collectionHandler) {
        this.page = page;
        this.collectionHandler = collectionHandler;
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
            collectionHandler.addCrawledPage(page);
        } catch (Exception e) {
            collectionHandler.addFailedPage(page, e.getMessage());
        }
    }
}

