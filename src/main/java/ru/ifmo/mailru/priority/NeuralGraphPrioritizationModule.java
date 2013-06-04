package ru.ifmo.mailru.priority;

import ru.ifmo.mailru.core.Page;
import ru.ifmo.mailru.core.WebURL;
import ru.ifmo.mailru.features.NeuralPrioritizationController;

import java.io.FileNotFoundException;

/**
 * @author Anastasia Lebedeva
 */
public class NeuralGraphPrioritizationModule extends PrioritizationModule {
    private NeuralPrioritizationController neuralPrioritizationController;

    public NeuralGraphPrioritizationModule() throws FileNotFoundException {
        neuralPrioritizationController = new NeuralPrioritizationController();
        int positiveSize = neuralPrioritizationController.positiveExamples.size();
        int negativeSize = neuralPrioritizationController.negativeExamples.size();
        neuralPrioritizationController.train(positiveSize, negativeSize);
    }


    @Override
    public void setQualityRanks(WebURL url, Page parentPage) {
        double addition = parentPage.getUrl().getQualityRank() / parentPage.getOutLinks().size();
        Double computedRank = neuralPrioritizationController.computeRank(url);
        url.incrementQualityRank(computedRank + addition);
    }

    @Override
    public void resetQualityRanks(WebURL url, Page parentPage) {
        double addition = parentPage.getUrl().getQualityRank() / parentPage.getOutLinks().size();
        url.incrementQualityRank(addition);
    }
}
