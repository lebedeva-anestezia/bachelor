package ru.ifmo.mailru.priority;

import ru.ifmo.mailru.core.Page;
import ru.ifmo.mailru.core.WebURL;

/**
 * @author Anastasia Lebedeva
 */
public class FICAPrioritizationModule extends PrioritizationModule {
    private static double beta = 0.2, gamma = 0.6;
    private static double time, K = 100, size;

    @Override
    public void setQualityRanks(WebURL url, Page parentPage) {
        size++;
        time = size / K;
        double alpha = Math.exp(-beta * time);
        double absRank = -parentPage.getUrl().getQualityRank();
        double dist = Math.log(parentPage.getOutLinks().size()) + gamma * absRank;
        double curRank = -((1 - alpha) * absRank + alpha * dist);
        url.setQualityRank(Math.max(url.getQualityRank(), curRank));
    }

    @Override
    public void resetQualityRanks(WebURL url, Page parentPage) {
        setQualityRanks(url, parentPage);
    }
}
