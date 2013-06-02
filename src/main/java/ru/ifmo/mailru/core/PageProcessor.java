package ru.ifmo.mailru.core;

import java.io.IOException;

public class PageProcessor implements Runnable {

    private Page page;
    private Crawler crawler;

    public PageProcessor(Page page, Crawler crawler) {
        this.page = page;
        this.crawler = crawler;
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
            processingWebPage();
            page.setCompleted(true);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            page.setCompleted(false);
        } finally {
            page.getUrl().setLastVisitTime(System.currentTimeMillis());
            crawler.submitResult(page);
        }
    }

    void processingWebPage() throws IOException  {
        load();
        PageParser.parse(page);
    }
}

