package ru.ifmo.mailru;

import org.junit.Ignore;
import org.junit.Test;
import ru.ifmo.mailru.core.Controller;
import ru.ifmo.mailru.core.Spider;
import ru.ifmo.mailru.google.pr.PageRankGetter;
import ru.ifmo.mailru.priority.EmptyPrioritization;
import ru.ifmo.mailru.priority.FICAPrioritization;
import ru.ifmo.mailru.priority.ModulePrioritization;
import ru.ifmo.mailru.priority.NeuralPrioritization;
import ru.ifmo.mailru.robottxt.PolitenessModule;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;

/**
 * @author Anastasia Lebedeva
 */
public class CompTest {
    private static final String resourceDir = "src/test/resources/";
    private static final String pageRanksDir = resourceDir + "pageRanks/";
    private static final String crawledPagesDir = resourceDir + "crawledPages/";
    private static final String failedPagesDir = resourceDir + "failedPages/";
    public static final String queuePagesDir = resourceDir + "queue/";
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
    private static final Date date = new Date();
    private static final long MINUTE = 1000 * 60;
    private static final long HOUR = 60 * MINUTE;

    @Ignore
    @Test
    public void comparedRun() {
        bfsSpiderRun();
        FICASpiderRun();
    }

    @Ignore
    @Test
    public void neuralSpiderRun() {
        try {
            spiderRun(new NeuralPrioritization(), createNewController(), "neural");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Ignore
    @Test
    public void bfsSpiderRun() {
        try {
            spiderRun(new EmptyPrioritization(), createNewController(), "bfs");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Ignore
    @Test
    public void FICASpiderRun() {
        try {
            spiderRun(new FICAPrioritization(), createNewController(), "FICA");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Ignore
    @Test
    public void neuralSpiderRestore() {
        File queueFile = new File(resourceDir + "queueNeural1368492618101.txt");
        File crawledPage = new File(crawledPagesDir + "neural/" + "neural201305132350.txt");
        try {
            spiderRun(new NeuralPrioritization(), restoreController(queueFile, crawledPage), "neural");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Controller createNewController() throws FileNotFoundException {
        File startSet = new File(resourceDir + "domens.txt");
        return new Controller(startSet);
    }

    public Controller restoreController(File queueFile, File crawledPages) throws FileNotFoundException {
        return new Controller(queueFile, crawledPages);
    }


    private void spiderRun(ModulePrioritization prioritization, Controller controller, String teg) throws FileNotFoundException {
        String date = dateFormat.format(CompTest.date);
        File crawledFile = new File(crawledPagesDir + teg + "/" + teg + date + ".txt");
        File failedFile = new File(failedPagesDir + teg + "/failed" + teg + date + ".txt");
        String queueFileName = queuePagesDir + teg + "/" + date + ".txt";
        PrintWriter pwCrawled = null;
        PrintWriter pwFailed = null;
        try {
            pwCrawled = new PrintWriter(crawledFile);
            pwFailed = new PrintWriter(failedFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        controller.setFailedLogging(pwFailed);
        controller.setCrawledLogging(pwCrawled);
        controller.setQueueLogFile(queueFileName);
        Spider spider = new Spider(controller, prioritization);
        try {
            spider.start();
            Thread.sleep(Integer.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pwCrawled.close();
            pwFailed.close();
        }
    }

    @Ignore
    @Test
    public void printMeanPR() {
        File file = new File(pageRanksDir + "bfs201305230231.txtnew.pr");
        try {
            System.out.println(meanPR(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public double meanPR(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        long sum = 0;
        int count = 0;
        while (sc.hasNext()) {
            String s = sc.nextLine();
            String[] arr = s.split(" ");
            sum += Integer.valueOf(arr[1]);
            count++;
        }
        return (double) sum / count;
    }

    public void constructPRGetter() {
        File file = new File(pageRanksDir + "pageRanks.pr2");
        try {
            new PageRankGetter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Ignore
    @Test
    public void getPageRanks() {
        constructPRGetter();
        File file = new File(crawledPagesDir + "neural/neural201305262223.txt");
        try {
            printRageRanks(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void printRageRanks(File file) throws FileNotFoundException {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(pageRanksDir + file.getName() + "new" + ".pr"));
            PageRankGetter getter = new PageRankGetter();
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String s = scanner.nextLine();
                int res = getter.getExistPageRank(s);
                if (res != 22) {
                    if (res != -3) {
                        pw.println(s + " " + res);
                        pw.flush();
                    }
                    System.out.println(s + " " + res);
                    //Thread.sleep(1000);
                } else {
                    System.out.println("exists PR for " + s);
                }
            }
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
        assertTrue(politenessModule.isAllow("https://twitter.com/search?q=%23"));
    }

    @Test
    public void testingRobotTxt1() throws URISyntaxException {
        PolitenessModule politenessModule = null;
        try {
            politenessModule = new PolitenessModule("mail.ru");
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        assertTrue(politenessModule.isAllow("http://mail.ru/"));
    }

    @Ignore
    @Test
    public void testPageRankTool() {
        PageRankGetter getter = new PageRankGetter();
        System.out.println(getter.getPageRank("http://google.com/"));
    }

    @Ignore
    @Test
    public void testSomething() {
        String s = "http://mamba.ru?ar=1&amp;mmbsid=21720dfe5a1978c9ec227ccfc28d718b&amp;force_web=1&amp;mmbsid=21720dfe5a1978c9ec227ccfc28d718b";
        try {
            URI uri = new URI(s);
            System.out.println(uri.getPath());
            System.out.println(uri.getQuery());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
