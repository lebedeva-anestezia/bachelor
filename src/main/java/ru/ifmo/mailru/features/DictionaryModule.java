package ru.ifmo.mailru.features;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author Anastasia Lebedeva
 */
public class DictionaryModule {
    private Map<String, Integer> frequencyTerm = new HashMap<>();
    private static Set<String> dictionary = new TreeSet<>();

    public static void createDictionary(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        while (scanner.hasNext()) {
            dictionary.add(scanner.nextLine());
        }
    }

    public static boolean isWord(String s) {
        return dictionary.contains(s.toLowerCase());
    }

    public static String[] splitIntoTokens(String s) {
        s = s.replaceAll("%20", " ");
        String[] tokens = s.split("[\\W_]");
        return removeEmptyStrings(tokens);
    }

    public static String[] splitIntoWordTokens(String s) {
        String[] tokens = splitIntoTokens(s);
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].matches("\\d+") || tokens[i].length() == 1) {
                tokens[i] = "";
            }
        }
        return removeEmptyStrings(tokens);
    }

    private static String[] removeEmptyStrings(String[] tokens) {
        int length = tokens.length;
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equals("")) length--;
        }
        String[] ret = new String[length];
        int last = 0;
        for (String token : tokens) {
            if (!token.equals("")) {
                ret[last++] = token;
            }
        }
        return ret;
    }

    public void separateFrequencyTerms(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        while (sc.hasNext()) {
            String s = sc.nextLine();
            String[] tokens = splitIntoWordTokens(s);
            for (String token : tokens) {
                Integer count = frequencyTerm.get(token);
                if (count == null) {
                    count = Integer.valueOf(0);
                }
                frequencyTerm.put(token, ++count);
            }
        }
        sc.close();
    }
}
