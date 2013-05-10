package ru.ifmo.mailru;

import org.junit.Ignore;
import org.junit.Test;
import ru.ifmo.mailru.core.PageParser;
import ru.ifmo.mailru.core.Spider;
import ru.ifmo.mailru.core.WebURL;
import ru.ifmo.mailru.features.DictionaryModule;
import ru.ifmo.mailru.google.pr.GettingPageRankExecutor;
import ru.ifmo.mailru.google.pr.PageRankGetter;
import ru.ifmo.mailru.priority.EmptyPrioritization;
import ru.ifmo.mailru.priority.FICAPrioritization;
import ru.ifmo.mailru.priority.ModulePrioritization;
import ru.ifmo.mailru.robottxt.PolitenessModule;
import ru.ifmo.mailru.util.ValueComparator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.Assert.assertTrue;

/**
 * @author Anastasia Lebedeva
 */
public class CompTest {
    private static final String resourceDir = "src/test/resources/";
    private static final String pageRanksDir = resourceDir + "pageRanks/";
    private static final String crawledPagesDir = resourceDir + "crawledPages/";
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
    private static final Date date = new Date();
    private static final long MINUTE = 1000 * 60;
    private static final long HOUR = 60 * MINUTE;

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

    private Set<WebURL> readStartSet(File file) throws FileNotFoundException {
        Set<WebURL> set = new HashSet<>();
        Scanner scanner = new Scanner(file);
        while (scanner.hasNext()) {
            try {
                set.add(new WebURL(scanner.nextLine()));
            } catch (URISyntaxException e) {
                System.err.println(e.getMessage());
            }
        }
        return set;
    }

    private void spiderRun(ModulePrioritization prioritization, String teg) throws FileNotFoundException {
        Set<WebURL> startSet;
        try {
            startSet = readStartSet(new File(crawledPagesDir + "start.txt"));
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            return;
        }
        //String fileName = crawledPagesDir + teg + dateFormat.format(date) + ".txt";
        String fileName = crawledPagesDir + teg + dateFormat.format(date) + ".txt";
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(new File(crawledPagesDir + "rus.txt"));
        Spider spider = new Spider(prioritization, startSet, pw);
        scanner.close();
        try {
            spider.start();
            Thread.sleep(4 * HOUR);
            spider.stop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pw.close();
        }
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
        File file = new File(crawledPagesDir + "russian_urls.txt");
        try {
            printRageRanks(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Ignore
    @Test
    public void getRu() throws FileNotFoundException {
        HashSet<String> urls = new HashSet<>();
        String[] list = new File(crawledPagesDir).list();
        for (String s : list) {
            File file = new File(crawledPagesDir + s);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String urlStr = scanner.nextLine();
                try {
                    if (PageParser.isRuURL(new URI(urlStr))) {
                        urls.add(urlStr);
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
        PrintWriter printWriter = new PrintWriter(crawledPagesDir + "russian_urls.txt");
        for (String url : urls) {
            printWriter.println(url);
        }
        printWriter.close();
    }

    public void printRageRanks(File file) throws FileNotFoundException {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(pageRanksDir + file.getName() + "new" + ".pr"));
            GettingPageRankExecutor executor = new GettingPageRankExecutor(file, pw);
            executor.execute();
            //PageRankGetter.printResults(pw);
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
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<?> f = service.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("GEGE");
            }
        });
        Future<String> future = service.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(4000);
                System.out.println("GEGE");
                return "OPA";
            }
        });
        try {
            System.out.println("START");
            f.get(3, TimeUnit.SECONDS);
            System.out.println("Finished");
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

}
