package priority;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Anastasia Lebedeva
 * Date: 4/10/13
 * Time: 1:07 AM
 * To change this template use File | Settings | File Templates.
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
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            s = reader.readLine();
        }
    }

}
