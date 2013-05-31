package ru.ifmo.mailru.priority;

import ru.ifmo.mailru.core.Page;
import ru.ifmo.mailru.core.WebURL;
import ru.ifmo.mailru.features.TrainingController;

import java.io.FileNotFoundException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Anastasia Lebedeva
 */
public class NeuralGraphPrioritization implements ModulePrioritization {
    private TrainingController trainingController;
    private static ConcurrentHashMap<WebURL, Double> cache = new ConcurrentHashMap<>();

    public NeuralGraphPrioritization() throws FileNotFoundException {
        trainingController = new TrainingController();
        int positiveSize = trainingController.positiveExamples.size();
        int negativeSize = trainingController.negativeExamples.size();
        trainingController.train(positiveSize, negativeSize);
    }

    @Override
    public void setPriorities(Page page) {
        double addition = page.getUrl().getRank() / page.getOutLinks().size();
        for (WebURL url : page.getOutLinks()) {
            Double computedRank;
            Double rank = cache.get(url);
            if (rank == null) {
                computedRank = trainingController.computeRank(url) + addition;
            } else {
                computedRank = rank + addition;
            }
            url.setRank(computedRank);
            cache.put(url, computedRank);
        }
    }
}
