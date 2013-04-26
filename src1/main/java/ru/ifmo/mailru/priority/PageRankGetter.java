package priority;

import com.temesoft.google.pr.PageRankService;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Anastasia Lebedeva
 * Date: 4/10/13
 * Time: 12:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class PageRankGetter {
    private PageRankService pageRankService;
    private Map<String, Integer> pageRanks;

    public PageRankGetter() {
        pageRankService = new PageRankService();
        pageRanks = new HashMap<>();
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
