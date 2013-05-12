package ru.ifmo.mailru.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * @author Anastasia Lebedeva
 */
public class ContentLoader {
    private BufferedReader reader;

    public ContentLoader(URI uri, int attemptsNumber) throws IOException {
        int n = 0;
        boolean done = false;
        while (!done)
        {
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
        }
    }

    public boolean isWebPage() throws IOException {
        char[] firstSymbols = new char[40];
        reader.read(firstSymbols);
        String firstLine = new String(firstSymbols);
        firstLine = firstLine.toLowerCase().trim();
        return firstLine.startsWith("<?xml") || firstLine.startsWith("<!doctype") || firstLine.startsWith("<html");
    }

    public String loadRobotsTxt() throws IOException {
        return loadContent("\n");
    }

    private String loadContent(String separator) throws IOException {
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = nextLine()) != null) {
            sb.append(s);
            sb.append(separator);
        }
        reader.close();
        return sb.toString();
    }

    public String loadWebPage() throws IOException {
        if (!isWebPage()) {
            reader.close();
            throw new IOException("Is not web page");
        }
        return loadContent("");
    }

    private String nextLine() throws IOException {
        return reader.readLine();
    }
}
