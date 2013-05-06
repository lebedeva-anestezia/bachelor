package ru.ifmo.mailru.google.pr;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * @author Anastasia Lebedeva
 */
public class GettingPageRankExecutor {

    private final ExecutorService executor;
    private final List<Runnable> urls;

    public GettingPageRankExecutor(File file) throws FileNotFoundException {
        urls = new ArrayList<>();
        Scanner sc = new Scanner(file);
        while (sc.hasNext()) {
            final String s = sc.nextLine();
            urls.add(new Runnable() {
                PageRankGetter getter = new PageRankGetter();

                @Override
                public void run() {
                    try {
                        int res = 0;
                        int t = 0;
                        do {
                            if (res == -3) {
                                t++;
                                Thread.sleep(3000);
                            }
                            res = getter.getPageRank(s);
                        } while (res == -3 && t < 10);
                    } catch (InterruptedException e) {
                        System.err.println(e.getMessage());
                    }
                }
            });
        }
        executor = Executors.newFixedThreadPool(10);
    }

    public void execute() {
        for (Runnable url : urls) {
            executor.execute(url);
        }
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
