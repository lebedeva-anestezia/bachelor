package ru.ifmo.mailru.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * @author Anastasia Lebedeva
 */
public class ContentLoader {

    public static String loadContent(URI uri, boolean includeEnters) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                uri.toURL().openStream()));
        StringBuilder sb = new StringBuilder();
        String separator = "";
        if (includeEnters) {
            separator = "\n";
        }
        String s;
        while ((s = reader.readLine()) != null) {
            sb.append(s);
            sb.append(separator);
        }
        return sb.toString();
    }
}
