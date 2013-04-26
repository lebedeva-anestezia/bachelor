package ru.ifmo.mailru.priority;

import ru.ifmo.mailru.core.Page;
import ru.ifmo.mailru.core.WebURL;

/**
 * @author Anastasia Lebedeva
 */
public class FICAPrioritization implements ModulePrioritization {
    private static double beta = 0.1, gamma = 0.5;
    private static double time, K = 250000, size;

    @Override
    public void setPriorities(Page page) {
        size++;
        time = size / K;
        double alpha = Math.exp(-beta * time);
        double dist = Math.log(page.getOutLinks().size()) + gamma * page.getUrl().getRank();
        for (WebURL webURL : page.getOutLinks()) {
            webURL.setRank((1 - alpha) * page.getUrl().getRank() + alpha * dist);
        }
    }
}
