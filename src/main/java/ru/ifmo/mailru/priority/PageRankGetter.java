package ru.ifmo.mailru.priority;

import com.temesoft.google.pr.PageRankService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Anastasia Lebedeva
 */
public class PageRankGetter {
    private PageRankService pageRankService;
    private ConcurrentMap<String, Integer> pageRanks;

    public PageRankGetter() {
        pageRankService = new PageRankService();
        pageRanks = new ConcurrentHashMap<>();
    }

    public int getPageRank(String s) throws URISyntaxException {
        return getPageRank(new URI(s));
    }

    public int getPageRank(URI uri) {
        String domen = uri.getHost();
        if (pageRanks.containsKey(domen)) {
            return pageRanks.get(domen);
        }
        int rank = pageRankService.getPR(domen);
        pageRanks.put(domen, rank);
        return rank;
    }
}
