package ru.ifmo.mailru.priority;

import ru.ifmo.mailru.core.Page;
import ru.ifmo.mailru.core.WebURL;
import ru.ifmo.mailru.features.TrainingController;

import java.io.FileNotFoundException;

/**
 * @author Anastasia Lebedeva
 */
public class NeuralGraphPrioritization extends ModulePrioritization {
    private TrainingController trainingController;

    public NeuralGraphPrioritization() throws FileNotFoundException {
        trainingController = new TrainingController();
        int positiveSize = trainingController.positiveExamples.size();
        int negativeSize = trainingController.negativeExamples.size();
        trainingController.train(positiveSize, negativeSize);
    }


    @Override
    public void setQualityRanks(WebURL url, Page parentPage) {
        double addition = parentPage.getUrl().getQualityRank() / parentPage.getOutLinks().size();
        Double computedRank = trainingController.computeRank(url);
        url.incrementQualityRank(computedRank + addition);
    }

    @Override
    public void resetQualityRanks(WebURL url, Page parentPage) {
        double addition = parentPage.getUrl().getQualityRank() / parentPage.getOutLinks().size();
        url.incrementQualityRank(addition);
    }
}
