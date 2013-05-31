package ru.ifmo.mailru.core;

import ru.ifmo.mailru.priority.ModulePrioritization;

import java.io.IOException;

/**
 * @author Anastasia Lebedeva
 */
public abstract class CrawlModule implements Runnable {

    protected Page page;
    protected Controller controller;
    protected ModulePrioritization prioritization;

    public CrawlModule(WebURL url, Controller controller, ModulePrioritization prioritization) {
        this.page = new Page(url);
        this.controller = controller;
        this.prioritization = prioritization;
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

    abstract void processingWebPage() throws IOException;
}
