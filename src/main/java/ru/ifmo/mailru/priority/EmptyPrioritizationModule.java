package ru.ifmo.mailru.priority;

import ru.ifmo.mailru.core.Page;
import ru.ifmo.mailru.core.WebURL;

/**
 * @author Anastasia Lebedeva
 */
public class EmptyPrioritizationModule extends PrioritizationModule {

    @Override
    public void setQualityRanks(WebURL url, Page parentPage) {
        url.setQualityRank(1.0 - ((double)System.currentTimeMillis() - START_TIME) / START_TIME);
    }

    @Override
    public void resetQualityRanks(WebURL url, Page parentPage) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
