package ru.ifmo.mailru.features;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Scanner;

/**
 * @author Anastasia Lebedeva
 */
public class TrainingControllerTest {

    @Test
    public void mainTest() {
        try {
            TrainingController controller = new TrainingController();
            int pos = controller.positiveExamples.size();
            int neg = controller.negativeExamples.size();
            controller.train(pos, neg);
            Scanner scanner = new Scanner(new File(TrainingController.RANKS_FILE));
            int n = 0;
            double mean = 0;
            while (scanner.hasNext()) {
                String s = scanner.nextLine();
                String[] arr = s.split(" ");
                try {
                    double real = controller.computeRank(arr[0]) * 11 - 1;
                    double expected = Double.valueOf(arr[1]);
                    if (expected > -0.5) {
                        mean += (real - expected) * (real - expected);
                        n++;
                    }
                    System.out.println("real: " + real + " expected: " + expected);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Math.sqrt(mean) / n);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
