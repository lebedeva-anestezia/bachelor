package ru.ifmo.mailru.features;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.quick.QuickPropagation;

/**
 * @author Anastasia Lebedeva
 */
public class NeuralNetwork {


    private final BasicNetwork network;
    private final NeuralDataSet set;
    private final int OUTPUT_COUNT;

    public NeuralNetwork(double[][] input, double[][] output) {
        network = new BasicNetwork();
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, input[0].length));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 3*input[0].length));
       // network.addLayer(new BasicLayer(new ActivationSigmoid(), true, input[0].length));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, output[0].length));
        network.getStructure().finalizeStructure();
        network.reset();
        set = new BasicNeuralDataSet(input, output);
        OUTPUT_COUNT = output[0].length;
    }

    public void train() {
        Train train = new QuickPropagation(network, set);
        int epoch = 1;
        do {
            train.iteration();
            epoch++;
            System.out.println("epoch: " + epoch + " Error: " + train.getError());
        } while(train.getError() > 0.035);
    }

    public double[] compute(double[] input) {
        double[] output = new double[OUTPUT_COUNT];
        network.compute(input, output);
        return output;
    }

    public static void main(String[] args) {
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(2));
        network.addLayer(new BasicLayer(2));
        network.addLayer(new BasicLayer(1));
        network.getStructure().finalizeStructure();
        network.reset();
        double[][] XOR_INPUT = {
            { 0.0, 0.0 },
            { 1.0, 0.0 },
            { 0.0, 1.0 },
            { 1.0, 1.0 } };
        double XOR_IDEAL[][] = {
            { 0.0 },
            { 1.0 },
            { 1.0 },
            { 0.0 } };
        NeuralDataSet set = new BasicNeuralDataSet(XOR_INPUT, XOR_IDEAL);
        Train train = new QuickPropagation(network, set);
        int epoch = 1;
        do {
            train.iteration();
            epoch++;
        } while(train.getError() > 0.01);
        for (MLDataPair pair : set) {
            MLData out = network.compute(pair.getInput());
            System.out.println(pair.getInput().getData(0)
                    + "," + pair.getInput().getData(1)
                    + ", actual=" + out.getData(0) + ",ideal=" +
                    pair.getIdeal().getData(0));
        }
    }
}
