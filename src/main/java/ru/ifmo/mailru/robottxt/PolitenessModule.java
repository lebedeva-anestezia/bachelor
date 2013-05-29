package ru.ifmo.mailru.robottxt;

import ru.ifmo.mailru.core.ContentLoader;
import ru.ifmo.mailru.core.HostController;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Anastasia Lebedeva
 */
public class PolitenessModule {

    public static final Pattern rulePattern = Pattern.compile("(Allow|Disallow): (\\S+).*");
    private final String ROBOTS_TXT = "/robots.txt";
    private final String USER_AGENT = "(?i)User-agent: \\*";
    private TreeSet<Rule> rules = new TreeSet<>();

    public PolitenessModule(HostController hostController) throws URISyntaxException, IOException {
        String content;
        hostController.lock.lock();
        try {
            ContentLoader loader = new ContentLoader(new URI("http://" + hostController.host + ROBOTS_TXT), 3);
            content = loader.loadRobotsTxt();
        } catch (Exception e) {
            throw e;
        } finally {
            hostController.lock.unlock();
        }
        if (content == null) return;
        extractRules(content);
    }

    private void extractRules(String content) {
        String[] lines = content.split("\n");
        int i = -1;
        while (++i < lines.length) {
            if (lines[i].matches(USER_AGENT)) {
                break;
            }
        }
        for (int j = i + 1; j < lines.length; j++) {
            if (Pattern.matches(String.valueOf(rulePattern), lines[j])) {
                rules.add(new Rule(lines[j]));
            }  else {
                if (!(lines[j].equals("") || lines[j].startsWith("#"))) {
                    break;
                }
            }
        }
    }

    public boolean isAllow(String uri) throws URISyntaxException {
        return isAllow(new URI(uri));
    }

    public boolean isAllow(URI uri) {
        String locate = uri.getPath();
        if (locate.equals("")) {
            locate = "/";
        }
        String query = uri.getRawQuery();
        if (query != null) {
            locate += "?" + query;
        }
        NavigableSet<Rule> pertinentDisallowRules = rules.headSet(new Rule(locate + "$", null, null), true);
        RuleType ruleType = RuleType.Allow;
        for (Rule rule : pertinentDisallowRules) {
            if (locate.matches(String.valueOf(rule.getPattern()))) {
                ruleType = rule.getType();
            }
        }
        return ruleType.equals(RuleType.Allow);
    }

}
