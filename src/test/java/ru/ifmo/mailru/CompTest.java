package ru.ifmo.mailru;

import org.junit.Ignore;
import org.junit.Test;
import ru.ifmo.mailru.core.Controller;
import ru.ifmo.mailru.core.Spider;
import ru.ifmo.mailru.features.DictionaryModule;
import ru.ifmo.mailru.google.pr.PageRankGetter;
import ru.ifmo.mailru.priority.EmptyPrioritization;
import ru.ifmo.mailru.priority.FICAPrioritization;
import ru.ifmo.mailru.priority.ModulePrioritization;
import ru.ifmo.mailru.priority.NeuralPrioritization;
import ru.ifmo.mailru.robottxt.PolitenessModule;
import ru.ifmo.mailru.util.ValueComparator;

import java.io.*;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import static org.junit.Assert.assertTrue;

/**
 * @author Anastasia Lebedeva
 */
public class CompTest {
    private static final String resourceDir = "src/test/resources/";
    private static final String pageRanksDir = resourceDir + "pageRanks/";
    private static final String crawledPagesDir = resourceDir + "crawledPages/";
    private static final String failedPagesDir = resourceDir + "failedPages/";
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
            spiderRun(new NeuralPrioritization(), "neural");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Ignore
    @Test
    public void bfsSpiderRun() {
        try {
            spiderRun(new EmptyPrioritization(), "bfs");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Ignore
    @Test
    public void FICASpiderRun() {
        try {
            spiderRun(new FICAPrioritization(), "FICA");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void spiderRun(ModulePrioritization prioritization, String teg) throws FileNotFoundException {
        File startSet = new File(resourceDir + "start.txt");
        File crawledFile = new File(crawledPagesDir + teg + "/" + teg + dateFormat.format(date) + ".txt");
        File failedFile = new File(failedPagesDir + teg + "/failed" + teg + dateFormat.format(date) + ".txt");
        PrintWriter pwCrawled = null;
        PrintWriter pwFailed = null;
        try {
            pwCrawled = new PrintWriter(crawledFile);
            pwFailed = new PrintWriter(pwFailed);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Controller controller = new Controller(startSet);
        controller.setFailedLogging(pwFailed);
        controller.setCrawledLogging(pwCrawled);
        Spider spider = new Spider(controller, prioritization);
        try {
            spider.start();
            Thread.sleep(5 * HOUR);
            spider.stop();
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
        File file = new File(pageRanksDir + "neural201305132350.txtnew.pr");
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
        File file = new File(pageRanksDir + "pageRanks.pr");
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
        File file = new File(crawledPagesDir + "neural201305132350.txt");
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
                int res = getter.getPageRank(s);
                if (res != 22) {
                    if (res != -3) {
                        pw.println(s + " " + res);
                        pw.flush();
                    }
                    System.out.println(s + " " + res);
                    Thread.sleep(1000);
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
        System.out.println(getter.getPageRank("http://ololonyashechki.livejournal.com/"));
    }


    @Ignore
    @Test
    public void testTokenizer() {
        File input = new File(crawledPagesDir + "russian_urls.txt");
        DictionaryModule m = new DictionaryModule();
        Map<String, Integer> map = m.getFrequencyTerm();
        try {
            m.separateFrequencyTerms(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        ValueComparator<String, Integer> comparator = new ValueComparator<>(map);
        TreeMap<String, Integer> treeMap = new TreeMap<>(comparator);
        treeMap.putAll(map);
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(resourceDir + "terms_" + input.getName()));
            for (String s : treeMap.keySet()) {
                pw.println(s + " " + map.get(s));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            pw.close();
        }
    }

    @Ignore
    @Test
    public void testSomething() {
        String s = "http://m.ya.ru/operamini/download.xml?platform=4&amp;branch=";
        try {
            String out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
            System.out.println(out);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
