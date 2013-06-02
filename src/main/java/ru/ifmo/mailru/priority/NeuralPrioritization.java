package ru.ifmo.mailru.priority;

import ru.ifmo.mailru.core.Page;
import ru.ifmo.mailru.core.WebURL;
import ru.ifmo.mailru.features.TrainingController;

import java.io.FileNotFoundException;

/**
 * @author Anastasia Lebedeva
 */
public class NeuralPrioritization implements ModulePrioritization {
    private TrainingController trainingController;

    public NeuralPrioritization() throws FileNotFoundException {
        trainingController = new TrainingController();
        int positiveSize = trainingController.positiveExamples.size();
        int negativeSize = trainingController.negativeExamples.size();
        trainingController.train(positiveSize, negativeSize);
    }

    @Override
    public void setPriorities(Page page) {
        for (WebURL url : page.getOutLinks()) {
            Double computedRank = trainingController.computeRank(url);
            url.setQualityRank(computedRank);
        }
    }
}
