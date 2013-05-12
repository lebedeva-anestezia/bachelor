package ru.ifmo.mailru.google.pr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Anastasia Lebedeva
 */
public class PageRankGetter {
    private PageRankService pageRankService;

    private static ConcurrentMap<String, Integer> pageRanks = new ConcurrentHashMap<>();

    public PageRankGetter() {
        pageRankService = new PageRankService();
    }

    public PageRankGetter(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        while (sc.hasNext()) {
            try {
                addURL(sc.nextLine());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        sc.close();
    }

    public void addURL(String url) {
        String[] tmp = url.split(" ");
        pageRanks.put(tmp[0], Integer.valueOf(tmp[1]));
    }

    public int getPageRank(URI uri) {
        return getPageRank(uri.toString());
    }

    public int getExistPageRank(String url) {
        if (pageRanks.containsKey(url)) {
            return pageRanks.get(url);
        }
        return -1;
    }

    public int getPageRank(String url) {
        if (pageRanks.containsKey(url)) {
            return 22;
        }
        int rank = 0;
        try {
            rank = pageRankService.getPR(url);
            pageRanks.put(url, rank);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return -3;
        }
        return rank;
    }

    public static void printResults(PrintWriter pw) throws Exception {
        for (Map.Entry<String, Integer> stringIntegerEntry : pageRanks.entrySet()) {
            pw.println(stringIntegerEntry.getKey() + " " + stringIntegerEntry.getValue());
        }
    }
}
