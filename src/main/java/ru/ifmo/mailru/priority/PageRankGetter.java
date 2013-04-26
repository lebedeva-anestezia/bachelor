package ru.ifmo.mailru.priority;

import google.pagerank.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Anastasia Lebedeva
 */

public class PageRankGetter {
    private Map<String, Integer> pageRanks;

    public PageRankGetter() {
        pageRanks = new HashMap<>();
    }

    public int getPageRank(URI uri) {
        String domen = uri.getHost();
        if (pageRanks.containsKey(domen)) {
            return pageRanks.get(domen);
        }
        int rank = PageRank.get(domen);
        pageRanks.put(domen, rank);
        return rank;
    }
}
