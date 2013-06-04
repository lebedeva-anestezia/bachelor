package ru.ifmo.mailru.priority;

import ru.ifmo.mailru.core.Page;
import ru.ifmo.mailru.core.WebURL;
import ru.ifmo.mailru.features.NeuralPrioritizationController;

import java.io.FileNotFoundException;

/**
 * @author Anastasia Lebedeva
 */
public class NeuralPrioritizationModule extends PrioritizationModule {
    private NeuralPrioritizationController neuralPrioritizationController;

    public NeuralPrioritizationModule() throws FileNotFoundException {
        neuralPrioritizationController = new NeuralPrioritizationController();
        int positiveSize = neuralPrioritizationController.positiveExamples.size();
        int negativeSize = neuralPrioritizationController.negativeExamples.size();
        neuralPrioritizationController.train(positiveSize, negativeSize);
    }

    @Override
    public void setQualityRanks(WebURL url, Page parentPage) {
        Double computedRank = neuralPrioritizationController.computeRank(url);
        url.setQualityRank(computedRank);
    }

    @Override
    public void resetQualityRanks(WebURL url, Page parentPage) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
