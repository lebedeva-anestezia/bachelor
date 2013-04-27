package ru.ifmo.mailru.features;

import ru.ifmo.mailru.core.WebURL;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Extractor {
	private List<Double> features;
	private String[] components;
	private int COMPONENT_NUMBER = 6;
	/*private String uri;
	private String scheme;
	private String host;
	private String path;
	private String query;
	private String fragment;*/
	
	public Extractor(WebURL url) {
		components = new String[COMPONENT_NUMBER];
		components[0] = url.getUri().toString();
		components[1] = url.getUri().getScheme();
		components[2] = url.getUri().getHost();
		components[3] = url.getUri().getPath();
		components[4] = url.getUri().getQuery();
		components[5] = url.getFragment();
		features = new ArrayList<>();
	}
	
	private void extractLengthFeatures() {
		for (int i = 0; i < COMPONENT_NUMBER; i++) {
			features.add((double)components[i].length());
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
			features.add(count);
		}
		Pattern capitalCase = Pattern.compile("[A-Z]+");
		for (int i = 3; i < COMPONENT_NUMBER; i++) {
			Matcher m = capitalCase.matcher(components[i]);
			double count = 0;
			while (m.find()) {
				count += m.group().length();
			}
			features.add(count);
		}
	}
	
	
	public Double[] buildVector() {
		extractLengthFeatures();
		extractOrthographicFeatures();
		Double[] res = new Double[features.size()];
		return features.toArray(res);
	}
}
