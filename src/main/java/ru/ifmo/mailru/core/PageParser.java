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

	public static void parse(Page page) {
        Matcher tagMatcher = A_TAG.matcher(page.getContent());
        Set<WebURL> out = new HashSet<>();
        while (tagMatcher.find()) {
            String link = null;
            try {
                link = extractLink(tagMatcher.group(1));
                if (link == null) {
                    continue;
                }
                URI uri = URLCanonizer.canonize(link);
                if (NON_HTML.matcher(uri.toString()).matches()) {
                    continue;
                }
                uri = URLCanonizer.resolver(uri, page.getUrl().getUri());
                if (!(uri.toString().startsWith("http://") || uri.toString().startsWith("https://"))) {
                    continue;
                }
                uri = URLCanonizer.canonizeHttpURI(uri);
                WebURL url = new WebURL();
                url.setFragment(uri.getFragment());
                uri = new URI(uri.getScheme(), uri.getSchemeSpecificPart(), null);
                if (isRuURL(uri)) {
                    if (Controller.isAllowHost(uri)) {
                        url.setUri(uri);
                        out.add(url);
                    }
                }
            } catch (Exception e) {
              //  System.err.println(e.getMessage() + " " + link);
            }
        }
        page.setOutLinks(out);
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
