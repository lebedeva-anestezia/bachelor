package ru.ifmo.mailru.robottxt;

import ru.ifmo.mailru.core.ContentLoader;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Anastasia Lebedeva
 */
public class PolitenessModule {
    private final Pattern allowPattern    = Pattern.compile("Allow: (\\S*).*"),
                          disallowPattern = Pattern.compile("Disallow: (\\S*).*");
    private final String ROBOTS_TXT = "/robots.txt";
    private TreeMap<String, Pattern> allows    = new TreeMap<>(),
                                     disallows = new TreeMap<>();
    private String host;
    private int delay = 1;

    public PolitenessModule(String host) throws URISyntaxException, IOException {
        this.host = host;
        String content = ContentLoader.loadContent(new URI(host + ROBOTS_TXT), true);
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
            String rule = null;
            if (Pattern.matches(String.valueOf(allowPattern), lines[j])) {
                Matcher m = allowPattern.matcher(lines[j]);
                m.matches();
                rule = m.group(1);
                allows.put(rule, generateRulePattern(rule));
            } else {
                if (Pattern.matches(String.valueOf(disallowPattern), lines[j])) {
                    Matcher m = disallowPattern.matcher(lines[j]);
                    m.matches();
                    rule = m.group(1);
                    allows.put(rule, generateRulePattern(rule));
                } else {
                    if (!(lines[j].equals("") || lines[j].startsWith("#"))) {
                        break;
                    }
                }
            }
        }
    }

    private Pattern generateRulePattern(String s) {
        return Pattern.compile(s.replaceAll("\\*", ".*"));
    }

    public boolean isAllow(String uri) throws URISyntaxException {
        return isAllow(new URI(uri));
    }

    public boolean isAllow(URI uri) {
        String locate = uri.getPath();
        if (locate.equals("")) {
            locate = "/";
        }
        String query = uri.getQuery();
        if (query != null) {
            locate += "?" + query;
        }
        NavigableMap<String, Pattern> pertinentDisallowRules = disallows.headMap(locate, true);
        for (String rule : pertinentDisallowRules.keySet()) {

        }
        return false;  //To change body of created methods use File | Settings | File Templates.
    }

    private class Rule {
        private String locate;
        private RuleType type;
        private Pattern pattern;

       private Rule(String s) {

       }
    }

    private enum RuleType {
        allow, disallow;
    }
}
