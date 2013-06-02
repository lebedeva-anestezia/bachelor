package ru.ifmo.mailru.priority;

import ru.ifmo.mailru.core.Page;
import ru.ifmo.mailru.core.WebURL;

/**
 * @author Anastasia Lebedeva
 */
public class FICAPrioritization implements ModulePrioritization {
    private static double beta = 0.2, gamma = 0.6;
    private static double time, K = 100, size;

    @Override
    public void setPriorities(Page page) {
        size++;
        time = size / K;
        double alpha = Math.exp(-beta * time);
        double absRank = -page.getUrl().getQualityRank();
        double dist = Math.log(page.getOutLinks().size()) + gamma * absRank;
        for (WebURL webURL : page.getOutLinks()) {
            double curRank = -((1 - alpha) * absRank + alpha * dist);
            webURL.setQualityRank(curRank);
           // System.out.println(webURL.getUri().toString() + " " + webURL.getQualityRank());
        }
    }
}
