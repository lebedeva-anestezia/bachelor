package priority;

import core.Page;
import core.WebURL;

/**
 * Created with IntelliJ IDEA.
 * User: Anastasia Lebedeva
 * Date: 4/23/13
 * Time: 11:38 PM
 * To change this template use File | Settings | File Templates.
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
