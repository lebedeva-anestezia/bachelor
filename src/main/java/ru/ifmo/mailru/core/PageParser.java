package ru.ifmo.mailru.core;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageParser {

    public static final Pattern A_TAG = Pattern.compile("(?i)<a([^>]+)>(.+?)</a>");
    public static final Pattern LINK_TEG = Pattern.compile("\\s*(?i)href\\s*=\\s*(\\\"([^\"]+\\\")|'[^']+'|([^'\">\\s]+))");
    public static final Pattern NON_HTML = Pattern.compile("(?i).*(\\.(pdf|jpe?g|tar|rar|zip|gz|7z|css|avi|djvu|png|rtf|txt|exe))$");

	public static void extractLinks(Page page) {
        Matcher tagMatcher = A_TAG.matcher(page.getContent());
        Set<String> out = new HashSet<>();
        while (tagMatcher.find()) {
            String link;
            try {
                link = extractLink(tagMatcher.group(1));
                if (link == null) {
                    continue;
                }

                String uri = resolveAndFilterLink(page, link);
                if (uri == null) continue;
                out.add(uri);
            } catch (Exception e) {
              //  System.err.println(e.getMessage() + " " + link);
            }
        }
        page.setOutLinks(out);
	}

    private static String resolveAndFilterLink(Page page, String link) {
        try {
            URI uri = URLCanonizer.canonize(link);
            if (NON_HTML.matcher(uri.toString()).matches()) {
                return null;
            }
            uri = URLCanonizer.resolver(uri, page.getUrl().getUri());
            if (!(uri.toString().startsWith("http://") || uri.toString().startsWith("https://"))) {
                return null;
            }
            uri = URLCanonizer.canonizeHttpURI(uri);
            uri = new URI(uri.getScheme(), uri.getSchemeSpecificPart(), null);
            if (!isRuURL(uri)) {
                return null;
            }
            return uri.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String extractLink(String href) {
        Matcher linkMatcher = LINK_TEG.matcher(href);
        if (linkMatcher.find()) {
            String link = linkMatcher.group(1);
            if (link.length() < 3) {
                return null;
            }
            return link.substring(1, link.length() - 1);
        }
        return null;
    }

    public static boolean isRuURL(URI uri) {
        try {
            return uri.getHost().endsWith(".ru");
        } catch (Exception e) {
            System.err.println(uri + " " + e.getMessage());
            return false;
        }
    }
}
