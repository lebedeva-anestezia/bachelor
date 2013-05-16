package ru.ifmo.mailru.features;

import ru.ifmo.mailru.core.WebURL;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeaturesExtractor {
	private List<Double> features;
	private String[] components;
	private final int COMPONENT_NUMBER = 5;
    private static double[][] MAXIMUMS;

    /**
     * row 0: length features
     * row 1: orthographic features (digits)
     * row 2: orthographic features (capital case)
     * row 3: number of term
     */

    {
        MAXIMUMS = new double[COMPONENT_NUMBER][4];
        for (int j = 0; j < 3; j++) {
            for (int i = 1; i < COMPONENT_NUMBER; i++) {
                MAXIMUMS[i][j] = 300; // TODO: replace to trust values
            }
        }
        for (int i = 0; i < 3; i++) {
            MAXIMUMS[0][i] = 900;
        }
        for (int i = 1; i < COMPONENT_NUMBER; i++) {
            MAXIMUMS[i][3] = 100;
        }
        MAXIMUMS[0][3] = 300;
    }

    public FeaturesExtractor(String url) throws URISyntaxException {
        this(new WebURL(new URI(url)));
    }
	
	public FeaturesExtractor(WebURL url) {
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
        for (int i = 0; i < COMPONENT_NUMBER - 1; i++) {
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
