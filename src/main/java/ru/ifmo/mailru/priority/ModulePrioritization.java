package ru.ifmo.mailru.priority;

import ru.ifmo.mailru.core.Page;
import ru.ifmo.mailru.core.WebURL;

/**
 * @author Anastasia Lebedeva
 */
public abstract class ModulePrioritization {
    public abstract void setQualityRanks(Page page);
    public abstract double computeUpdatingProbability(WebURL url);

    public double computeVisitRank(WebURL url) {
        return url.getQualityRank() - url.getQualityRank() * computeUpdatingProbability(url);
    }
}
