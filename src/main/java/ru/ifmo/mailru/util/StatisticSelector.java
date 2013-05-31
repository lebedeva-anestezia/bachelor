package ru.ifmo.mailru.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Anastasia Lebedeva
 */
public class StatisticSelector {
    public final List<Double> ranks = new ArrayList<>();

    public StatisticSelector(File file, int size) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        int count = 0;
        while (scanner.hasNext()) {
            String[] arr = scanner.nextLine().split(" ");
            ranks.add((Double.valueOf(arr[1]) + 1) / 11);
            count++;
            if (count == size) {
                break;
            }
        }
    }

    public void averagePageRank(File file, int intervals) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(file);
        int period = ranks.size() / intervals;
        int count = 0;
        double sum = 0;
        for (Double rank : ranks) {
            sum += rank;
            count++;
            if (count % period == 0) {
                out.println(sum / period);
                sum = 0;
            }
        }
        out.close();
    }

    public void averagePageRankFixedInterval(File file, int period) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(file);
        int count = 0;
        double sum = 0;
        for (Double rank : ranks) {
            sum += rank;
            count++;
            if (count % period == 0) {
                out.println(sum / period);
                sum = 0;
            }
        }
        out.println(sum / (count % period));
        out.close();
    }

    public void cumulativePageRank(File file, int intervals) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(file);
        int period = ranks.size() / intervals;
        double sum = 0;
        for (Double rank : ranks) {
            sum += rank;
        }
        out.println(period);
        double curSum = 0;
        int count = 0;
        for (Double rank : ranks) {
            curSum += rank;
            count++;
            if (count % period == 0) {
                out.println(curSum / sum);
            }
        }
        out.close();
    }

    public void computeRankFraction() {
        double real = 0;
        double expected = 0;
        for (int i = 0; i < ranks.size() - 1; i++) {
            double curRank = ranks.get(i);
            for (int j = i + 1; j < ranks.size(); j++) {
                if (ranks.get(j) < curRank) {
                    real++;
                }
                expected++;
            }
        }
        System.out.println(real / expected);
    }

    public static void main(String[] args) {
        File input1 = new File("src/test/resources/pageRanks/neural201305230718.txtnew.pr");
        File input2 = new File("src/test/resources/pageRanks/neural201305262223.txtnew.pr");
        File input3 = new File("src/test/resources/pageRanks/bfs201305230231.txtnew.pr");
        File input4 = new File("src/test/resources/pageRanks/neural201305220457.txtnew.pr");
        File input5 = new File("src/test/resources/pageRanks/neural201305301706.txtnew.pr");
        File input6 = new File("src/test/resources/pageRanks/neuralGraph201305300304.txtnew.pr");
        File output = new File("src/test/resources/statistic.txt");
        try {
            StatisticSelector selector = new StatisticSelector(input3, 17700);
            selector.averagePageRankFixedInterval(output, 1770);
           // selector.computeRankFraction();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
