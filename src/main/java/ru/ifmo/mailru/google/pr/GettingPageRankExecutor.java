package ru.ifmo.mailru.google.pr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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

    public GettingPageRankExecutor(File file, final PrintWriter out) throws FileNotFoundException {
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
                            if (res == -3) {
                                t++;
                                Thread.sleep(10);
                            }
                            res = getter.getPageRank(s);
                        if (res != 22) {
                            synchronized (out) {
                                out.println(s + " " + res);
                                out.flush();
                            }
                        }
                    } catch (InterruptedException e) {
                        System.err.println(e.getMessage());
                    }
                }
            });
        }
        executor = Executors.newSingleThreadExecutor();
    }

    public void execute() {
        for (Runnable url : urls) {
            executor.execute(url);
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            executor.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
