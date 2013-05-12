package ru.ifmo.mailru.core;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Anastasia Lebedeva
 */
public class PageProcessorTest {

    @Ignore
    @Test
    public void testProcessingWebPage() throws Exception {
       // WebURL url = new WebURL("https://www.aeroflot.ru/cms/time_table/search");
        WebURL url = new WebURL("http://www.guarantee.ru/services/users");
        System.out.println(url.getUri().toURL());
        PageProcessor processor = new PageProcessor(url, null, null);
        long t = System.currentTimeMillis();
        processor.processingWebPage();
        System.out.println(System.currentTimeMillis() - t);
    }
}
