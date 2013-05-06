package ru.ifmo.mailru.features;

import ru.ifmo.mailru.core.WebURL;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Extractor {
	private List<Double> features;
	private String[] components;
	private final int COMPONENT_NUMBER = 5;
    private static double[][] MAXIMUMS;

    {
        MAXIMUMS = new double[5][4];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                MAXIMUMS[i][j] = 10; // TODO: replace to trust values
            }
        }
    }
	
	public Extractor(WebURL url) {
		components = new String[COMPONENT_NUMBER];
        URI uri = url.getUri();
        components[0] = uri.toString();
		components[1] = uri.getScheme();
		components[2] = uri.getHost();
		components[3] = uri.getPath();
		components[4] = uri.getQuery();
        for (int i = 1; i < COMPONENT_NUMBER; i++) {
            if (components[i] == null) {
                components[i] = "";
            }
        }
        //components[5] = url.getFragment();
		features = new ArrayList<>();
	}
	
	private void extractLengthFeatures() {
		for (int i = 0; i < COMPONENT_NUMBER; i++) {
			features.add(components[i].length() / MAXIMUMS[i][0]);
		}
	}

	private void extractOrthographicFeatures() {
		Pattern digit = Pattern.compile("\\d+");
		for (int i = 0; i < COMPONENT_NUMBER; i++) {
			Matcher m = digit.matcher(components[i]);
			double count = 0;
			while (m.find()) {
				count += m.group().length();
			}
			features.add(count / MAXIMUMS[i][1]);
		}
		Pattern capitalCase = Pattern.compile("[A-Z]+");
		for (int i = 3; i < COMPONENT_NUMBER; i++) {
			Matcher m = capitalCase.matcher(components[i]);
			double count = 0;
			while (m.find()) {
				count += m.group().length();
			}
			features.add(count / MAXIMUMS[i][2]);
		}
	}

    private void extractCountTerm() {
        List<String[]> terms = extractTerms();
        for (int i = 1; i < COMPONENT_NUMBER; i++) {
            features.add(terms.get(i).length / MAXIMUMS[i][3]);
        }
    }

    private List<String[]> extractTerms() {
        List<String[]> terms = new ArrayList<>(COMPONENT_NUMBER - 1);
        for (int i = 1; i < COMPONENT_NUMBER; i++) {
            terms.add(DictionaryModule.splitIntoTokens(components[i]));
        }
        return terms;
    }

	
	public Double[] buildVector() {
		extractLengthFeatures();
		extractOrthographicFeatures();
        extractCountTerm();
		Double[] res = new Double[features.size()];
		return features.toArray(res);
	}
}
