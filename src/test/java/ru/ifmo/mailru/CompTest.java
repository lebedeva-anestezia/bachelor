package ru.ifmo.mailru;

import org.junit.Test;
import ru.ifmo.mailru.core.Spider;
import ru.ifmo.mailru.core.WebURL;
import ru.ifmo.mailru.priority.*;
import ru.ifmo.mailru.robottxt.PolitenessModule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Anastasia Lebedeva
 */
public class CompTest {
    private static final String resourceDir = "src/test/resources/";
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final Date date = new Date();

    @Test
    public void bfsSpiderRun() {
       spiderRun(new EmptyPrioritization(), "bfs");
    }

    private void spiderRun(ModulePrioritization prioritization, String teg) {
        WebURL url = new WebURL();
        try {
            url.setUri(new URI("http://pogoda.yandex.ru/saint-petersburg/"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        String fileName = resourceDir + teg + dateFormat.format(date);
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Set<WebURL> list1 = new HashSet<>();
        list1.add(url);
        Spider spider = new Spider(prioritization, list1, pw);
        Thread thread = new Thread(spider);
        thread.start();
        try {
            Thread.sleep(36000);
            //Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.interrupt();
        pw.close();
    }

    @Test
    public void spiderRun() {
        WebURL url = new WebURL();
        try {
            url.setUri(new URI("http://pogoda.yandex.ru/saint-petersburg/"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        PrintWriter pw1 = null;
        PrintWriter pw2 = null;
        try {
            pw1 = new PrintWriter(new File("output1.txt"));
            pw2 = new PrintWriter(new File("output2.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Set<WebURL> list1 = new HashSet<>();
        list1.add(url);

        Spider spiderSimple = new Spider(new EmptyPrioritization(), list1, pw1);
        Spider spiderFICA = new Spider(new FICAPrioritization(), list1, pw2);
        Thread thread1 = new Thread(spiderSimple);
        Thread thread2 = new Thread(spiderFICA);
        thread1.start();
        thread2.start();
        try {
            Thread.sleep(3600000);
            //Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread1.interrupt();
        thread2.interrupt();
        pw1.close();
        pw2.close();
    }

    @Test
    public void compareResults() {
        PrintWriter pw1 = null;
        PrintWriter pw2 = null;
        try {
            GettingPageRankExecutor executor1 = new GettingPageRankExecutor(new File("output1.txt"));
            GettingPageRankExecutor executor2 = new GettingPageRankExecutor(new File("output2.txt"));
            pw1 = new PrintWriter(new File("pagerank1.txt"));
            pw2 = new PrintWriter(new File("pagerank2.txt"));
            executor1.execute();
            PageRankGetter.printResults(pw1);
            pw1.close();
            executor2.execute();
            PageRankGetter.printResults(pw2);
            pw2.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            pw1.close();
            pw2.close();
        }
    }

    @Test
    public void testingRobotTxt() throws URISyntaxException {
        PolitenessModule politenessModule = null;
        try {
            politenessModule = new PolitenessModule("http://stackoverflow.com");
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        System.out.println(politenessModule.isAllow("http://stackoverflow.com/users/hkdhj/a?njjs?tab=accountssss"));
    }

}
