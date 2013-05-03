package ru.ifmo.mailru.priority;

import ru.ifmo.mailru.google.pr.PageRankGetter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * @author Anastasia Lebedeva
 */
public class TrainingController {
    private ArrayList<URI> urls;
    private PageRankGetter pageRankGetter;

    public TrainingController() {
        urls = new ArrayList<>();
        pageRankGetter = new PageRankGetter();
    }

    public TrainingController(String file) throws IOException {
        this();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String s = reader.readLine();
        while (s != null) {
            try {
                urls.add(new URI(s));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            s = reader.readLine();
        }
    }

}
