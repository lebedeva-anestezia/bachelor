package ru.ifmo.mailru.features;

import ru.ifmo.mailru.core.WebURL;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * @author Anastasia Lebedeva
 */
public class TrainingController {
    public final Map<String, Double> positiveExamples;
    public final Map<String, Double> negativeExamples;
    private NeuralNetwork neuralNetwork;
    public static final String RANKS_FILE = "src/test/resources/pageRanks/pageRanks.pr";
    public static final String DICTIONARY_FILE = "src/main/resources/dictionary.txt";

    public TrainingController() throws FileNotFoundException {
        this(new File(RANKS_FILE));
    }

    public TrainingController(File file) throws FileNotFoundException {
        DictionaryModule.createDictionary(new File(DICTIONARY_FILE));
        positiveExamples = new LinkedHashMap<>();
        negativeExamples = new LinkedHashMap<>();
        Scanner scanner = new Scanner(file);
        while (scanner.hasNext()) {
            String string = scanner.nextLine();
            String[] s = string.split(" ");
            try {
                Double rank = Double.valueOf(s[1]);
                if (rank >= 0.0) {
                    positiveExamples.put(s[0], rank);
                } else {
                    negativeExamples.put(s[0], rank);
                }
            } catch (NullPointerException e) {
                System.err.println(e.getMessage() + " for string: " + string);
            }
        }
    }

    private List<String> getRandomList(List<String> input, int count) {
        Collections.shuffle(input);
        List<String> res = new LinkedList<>();
        int i = Math.min(input.size(), count);
        for (String item : input) {
            if (i == 0) break;
            res.add(item);
            i--;
        }
        return  res;
    }

    public void train() {
        train(positiveExamples.size(), negativeExamples.size());
    }

    private Double normalizeRank(Double rank) {
        return (rank + 1.0) / 11;
    }

    public void train(int positiveCount, int negativeCount) throws NumberFormatException{
        if (positiveCount > positiveExamples.size() || negativeCount > negativeExamples.size()) {
            throw new NumberFormatException("Numbers must belong to the range. Current numbers: " + positiveCount + " " + negativeCount);
        }
        List<String> trainingSet = getRandomList(new ArrayList<>(positiveExamples.keySet()), positiveCount);
        trainingSet.addAll(getRandomList(new ArrayList<>(negativeExamples.keySet()), negativeCount));
        List<Double[]> inputVectors = new ArrayList<>();
        List<Double> outputValues = new ArrayList<>();
        for (String item : trainingSet) {
            try {
                FeaturesExtractor extractor = new FeaturesExtractor(item);
                inputVectors.add(extractor.buildVector());
                Double rank = positiveExamples.get(item);
                if (rank == null) {
                    rank = negativeExamples.get(item);
                }
                outputValues.add(normalizeRank(rank));
            } catch (URISyntaxException e) {
                System.err.println("Illegal URI syntax for " + item);
            }
        }
        Double[][] out = new Double[outputValues.size()][1];
        Double[][] in = new Double[inputVectors.size()][inputVectors.get(0).length];
        int i = 0;
        for (Double value : outputValues) {
            out[i][0] = value;
            i++;
        }
        neuralNetwork = new NeuralNetwork(replaceToPrimitiveDouble2D(inputVectors.toArray(in)),
                replaceToPrimitiveDouble2D(out));
        neuralNetwork.train();
    }

    private double[][] replaceToPrimitiveDouble2D(Double[][] matrix) {
        double[][] primitives = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < primitives.length; i++) {
            primitives[i] = replaceToPrimitiveDouble1D(matrix[i]);
        }
        return primitives;
    }

    private double[] replaceToPrimitiveDouble1D(Double[] array) {
        double[] primitives = new double[array.length];
        for (int i = 0; i < primitives.length; i++) {
            primitives[i] = array[i];
        }
        return primitives;
    }

    public Double computeRank(String url) throws URISyntaxException {
        return computeRank(new WebURL(new URI(url)));
    }

    public Double computeRank(WebURL url) {
        FeaturesExtractor extractor = new FeaturesExtractor(url);
        Double[] vector = extractor.buildVector();
        return neuralNetwork.compute(replaceToPrimitiveDouble1D(vector))[0];
    }
}
