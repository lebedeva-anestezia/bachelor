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
        WebURL url = new WebURL("http://www.domodedovo.ru/");
        //WebURL url = new WebURL("https://mail.rambler.ru");
        PageProcessor processor = new PageProcessor(url, null, null);
        long t = System.currentTimeMillis();
        processor.processingWebPage();
        System.out.println(System.currentTimeMillis() - t);
    }
    //TODO: выяснить насчет контента страниц (начинается с \r\n<html>)
}
