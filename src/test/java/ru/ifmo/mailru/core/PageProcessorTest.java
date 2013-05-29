package ru.ifmo.mailru.core;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Anastasia Lebedeva
 */
public class PageProcessorTest {

    @Ignore
    @Test
    public void testProcessingWebPage() {
        try {
           // WebURL url = new WebURL("https://www.aeroflot.ru/cms/time_table/search");
            WebURL url = new WebURL("http://worldoftanks.ru/registration/ru");
            System.out.println(url.getUri().toURL());
            PageProcessor processor = new PageProcessor(url, null, null);
            long t = System.currentTimeMillis();
            processor.processingWebPage();
            System.out.println(System.currentTimeMillis() - t);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
