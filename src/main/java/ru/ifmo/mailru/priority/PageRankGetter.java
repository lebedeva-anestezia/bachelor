package ru.ifmo.mailru.priority;

import com.temesoft.google.pr.PageRankService;

import java.io.PrintWriter;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Anastasia Lebedeva
 */
public class PageRankGetter {
    private PageRankService pageRankService;
    private PrintWriter pw = null;

    public static ConcurrentMap<String, Integer> getPageRanks() {
        return pageRanks;
    }

    private static ConcurrentMap<String, Integer> pageRanks = new ConcurrentHashMap<>();

    public PageRankGetter() {
        pageRankService = new PageRankService();
    }

    public int getPageRank(URI uri) {
        return getPageRank(uri.toString());
    }

    public int getPageRank(String url) {
        if (pageRanks.containsKey(url)) {
            return pageRanks.get(url);
        }
        int rank = pageRankService.getPR(url);
        pageRanks.put(url, rank);
        return rank;
    }

    public static void printResults(PrintWriter pw) {
        for (Map.Entry<String, Integer> stringIntegerEntry : pageRanks.entrySet()) {
            pw.println(stringIntegerEntry.getKey() + " " + stringIntegerEntry.getValue());
        }
    }
}
