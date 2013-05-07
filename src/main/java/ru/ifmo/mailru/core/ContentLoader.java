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
        char[] firstSymbols = new char[9];
        reader.read(firstSymbols);
        String firstLine = new String(firstSymbols);
        //reader.close();
        return firstLine.startsWith("<?xml") || firstLine.startsWith("<!DOCTYPE");
    }

    public String loadRobotsTxt() throws IOException {
        return loadContent("\n");
    }

    private String loadContent(String separator) throws IOException {
        if (!isWebPage()) {
            reader.close();
            return null;
        }
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
        return loadContent("");
    }

    private String nextLine() throws IOException {
        return reader.readLine();
    }

   /* public String loadContent(URI uri, boolean includeEnters, int attemptsNumber) throws IOException {
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
    } */
}
