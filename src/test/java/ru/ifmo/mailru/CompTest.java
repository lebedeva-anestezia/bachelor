package ru.ifmo.mailru;

import org.junit.Ignore;
import org.junit.Test;
import ru.ifmo.mailru.core.Controller;
import ru.ifmo.mailru.core.Spider;
import ru.ifmo.mailru.google.pr.PageRankGetter;
import ru.ifmo.mailru.priority.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * @author Anastasia Lebedeva
 */
public class CompTest {
    private static final String resourceDir = "src/test/resources/";
    private static final String pageRanksDir = resourceDir + "pageRanks/";
    private static final String crawledPagesDir = resourceDir + "crawledPages/";
    private static final String failedPagesDir = resourceDir + "failedPages/";
    public static final String queuePagesDir = resourceDir + "queues/";
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
    public void neuralGraphSpiderRun() {
        try {
            spiderRun(new NeuralGraphPrioritization(), createNewController(), "neuralGraph", -1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Ignore
    @Test
    public void neuralSpiderRun() {
        try {
            spiderRun(new NeuralPrioritization(), createNewController(), "neural", -1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Ignore
    @Test
    public void cycleCompareSpiderRun() {
       /* try {
            spiderRun(new NeuralGraphPrioritization(), createNewController(), "neuralGraph", 20000);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/

        try {
            spiderRun(new NeuralPrioritization(), createNewController(), "neural", 20000);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
       /* try {
            spiderRun(new EmptyPrioritization(), createNewController(), "bfs", 20000);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
    }


    @Ignore
    @Test
    public void bfsSpiderRun() {
        try {
            spiderRun(new EmptyPrioritization(), createNewController(), "bfs", 20000);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Ignore
    @Test
    public void FICASpiderRun() {
        try {
            spiderRun(new FICAPrioritization(), createNewController(), "FICA", -1);
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
            spiderRun(new NeuralGraphPrioritization(), restoreController(queueFile, crawledPage), "neural", -1);
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


    private void spiderRun(ModulePrioritization prioritization, Controller controller, String teg, int max) throws FileNotFoundException {
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
        if (max != -1) {
            controller.setMaxPageCount(max);
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
        File file = new File(pageRanksDir + "neural201305301706.txtnew.pr");
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
        File file = new File(crawledPagesDir + "bfs/bfs201305310902.txt");
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
                String url = s.split(" ")[0];
                int res = getter.getPageRank(url);
                if (res != 22) {
                    if (res != -3) {
                        pw.println(url + " " + res);
                        pw.flush();
                    }
                    System.out.println(url + " " + res);
                    Thread.sleep(1000);
                } else {
                    System.out.println("exists PR for " + url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pw.close();
        }

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
        final Integer semaphore = new Integer(1);
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (semaphore) {
                    System.out.println("Th1 in critical section");
                    for (int i = 0; i < 10000000; i++) {

                    }
                    System.out.println("Th1 out");
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Th2 start");
                synchronized (semaphore) {
                    System.out.println("Th2 in critical section");
                    Integer o = null;
                    System.out.println(o.toString());
                    System.out.println("OLLO");
                }
            }
        });
        thread1.start();
        thread2.start();
    }

}
