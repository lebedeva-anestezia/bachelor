package ru.ifmo.mailru.priority;

import ru.ifmo.mailru.core.Page;
import ru.ifmo.mailru.core.WebURL;

/**
 * @author Anastasia Lebedeva
 */
public abstract class PrioritizationModule {
    public static final long START_TIME = System.currentTimeMillis();

    public abstract void setQualityRanks(WebURL url, Page parentPage);
    public abstract void resetQualityRanks(WebURL url, Page parentPage);

    public double computeReVisitRank(WebURL url) {
        if (url.getLastVisitTime() == 0) {
            return 1;
        } else {
            return 0;
        }
        //return ((double)System.currentTimeMillis() - url.getLastVisitTime()) / url.getUpdatePeriod();
        //TODO: написать правду
    }

    public double computeVisitRank(WebURL url) {
        return url.getQualityRank() * computeReVisitRank(url);
    }
}
