package ru.ifmo.mailru.priority;

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
                    getter.getPageRank(s);
                }
            });
        }
        executor = Executors.newCachedThreadPool();
    }

    public void execute() {
        for (Runnable url : urls) {
            executor.execute(url);
        }
    }
}
