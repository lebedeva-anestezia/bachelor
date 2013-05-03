package ru.ifmo.mailru;

import org.junit.Test;
import ru.ifmo.mailru.core.Spider;
import ru.ifmo.mailru.core.WebURL;
import ru.ifmo.mailru.google.pr.GettingPageRankExecutor;
import ru.ifmo.mailru.google.pr.PageRankGetter;
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
    private static final String pageRanksDir = resourceDir + "pageRanks/";
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

    public void constructPRGetter() {
        File file = new File(pageRanksDir + "pageRanks.pr");
        try {
            PageRankGetter getter = new PageRankGetter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Test
    public void getPageRanks() {
        constructPRGetter();
        File file = new File(resourceDir + "output1.txt");
        try {
            printRageRanks(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void printRageRanks(File file) throws FileNotFoundException {
        GettingPageRankExecutor executor = new GettingPageRankExecutor(file);
        PrintWriter pw = new PrintWriter(new File(pageRanksDir + file.getName() + "new" + ".pr"));
        executor.execute();
        try {
            PageRankGetter.printResults(pw);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pw.close();
        }

    }

    @Test
    public void testingRobotTxt() throws URISyntaxException {
        PolitenessModule politenessModule = null;
        try {
            politenessModule = new PolitenessModule("twitter.com");
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        System.out.println(politenessModule.isAllow("https://twitter.com/search?q=%23"));
    }

}
