package ru.ifmo.mailru.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * @author Anastasia Lebedeva
 */
public class ContentLoader {

    public static String loadContent(URI uri, boolean includeEnters, int attemptsNumber) throws IOException {
        int n = 0;
        boolean done = false;
        BufferedReader reader = null;
        while (!done)
            try {
                reader = new BufferedReader(new InputStreamReader(
                        uri.toURL().openStream()));
                done = true;
            } catch (IOException e) {
                if (++n >= attemptsNumber) {
                    throw e;
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e1) {
                    System.err.println(e1.getMessage());
                }
            }
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
