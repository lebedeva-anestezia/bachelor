package ru.ifmo.mailru.robottxt;

import ru.ifmo.mailru.core.ContentLoader;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Anastasia Lebedeva
 */
public class PolitenessModule {
    private final Pattern allowPattern = Pattern.compile("Allow: (.*)");
    private final Pattern disallowPattern = Pattern.compile("Disallow: (.*)");
    private final String ROBOTS_TXT = "/robots.txt";
    private TreeSet<String> allows, disallows;
    private String host;

    public TreeSet<String> getDisallows() {
        return disallows;
    }

    public TreeSet<String> getAllows() {
        return allows;
    }

    public PolitenessModule(String host) throws URISyntaxException, IOException {
        this.host = host;
        String content = ContentLoader.loadContent(new URI(host + ROBOTS_TXT));
        allows = new TreeSet<>();
        disallows = new TreeSet<>();
        extractRules(content);
    }

    private void extractRules(String content) {
        String[] lines = content.split("\n");
        int i = -1;
        while (++i < lines.length) {
            if (lines[i].matches("User-agent: \\*")) {
                break;
            }
        }
        for (int j = i + 1; j < lines.length; j++) {
            if (Pattern.matches(String.valueOf(allowPattern), lines[i])) {
                Matcher m = allowPattern.matcher(lines[i]);
                allows.add(m.group());
                continue;
            }
            if (Pattern.matches(String.valueOf(disallowPattern), lines[i])) {
                Matcher m = disallowPattern.matcher(lines[i]);
                disallows.add(m.group());
                continue;
            }
            if (!(lines[i].equals("") || lines[i].startsWith("#"))) {
                break;
            }
        }
    }


    public boolean checkAllow(String uri) {
        String locate = uri.replaceFirst(host, "");
        NavigableSet<String> head = disallows.headSet(locate, true);
        String[] locateArr = locate.split("/");
        for (String disallowLocate : head) {
            //if (disallowLocate.startsWith(locateArr[0]))
        }
        return false;
    }

}
