package ru.ifmo.mailru.priority;

import ru.ifmo.mailru.core.Page;
import ru.ifmo.mailru.core.WebURL;
import ru.ifmo.mailru.features.TrainingController;

import java.io.FileNotFoundException;

/**
 * @author Anastasia Lebedeva
 */
public class NeuralPrioritization extends ModulePrioritization {
    private TrainingController trainingController;

    public NeuralPrioritization() throws FileNotFoundException {
        trainingController = new TrainingController();
        int positiveSize = trainingController.positiveExamples.size();
        int negativeSize = trainingController.negativeExamples.size();
        trainingController.train(positiveSize, negativeSize);
    }

    @Override
    public void setQualityRanks(WebURL url, Page parentPage) {
        Double computedRank = trainingController.computeRank(url);
        url.setQualityRank(computedRank);
    }

    @Override
    public void resetQualityRanks(WebURL url, Page parentPage) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
