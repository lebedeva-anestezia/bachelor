package ru.ifmo.mailru.priority;

import ru.ifmo.mailru.core.Page;
import ru.ifmo.mailru.core.WebURL;

/**
 * @author Anastasia Lebedeva
 */
public abstract class ModulePrioritization {
    public static final long START_TIME = System.currentTimeMillis();

    public abstract void setQualityRanks(WebURL url, Page parentPage);
    public abstract void resetQualityRanks(WebURL url, Page parentPage);

    public double computeFreshnessProbability(WebURL url) {
        if (url.getLastVisitTime() == 0) {
            return 0;
        } else {
            return 1;
        }
        //return ((double)System.currentTimeMillis() - url.getLastVisitTime()) / url.getUpdatePeriod();
        //TODO: написать правду
    }

    public double computeVisitRank(WebURL url) {
        return url.getQualityRank() - url.getQualityRank() * computeFreshnessProbability(url);
    }
}
