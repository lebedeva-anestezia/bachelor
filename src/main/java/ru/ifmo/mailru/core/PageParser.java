package ru.ifmo.mailru.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class PageParser {
	
	public static void parse(Page page) {
        Pattern title = Pattern.compile("<title>(.*?)</title>");
        Matcher m = title.matcher(page.getContent());
        if (m.find()) {
            page.setTitle(m.group(1));
        }
        Pattern aTeg = Pattern.compile("(?i)<a\\s(?:.*?\\s)*?href=[\'\"](?![#\"\'{$])(.+?)[\'\"].*?>");
        m = aTeg.matcher(page.getContent());
        Set<WebURL> out = new HashSet<>();
        while (m.find()) {
            String link = m.group(1);
            URI uri = URLCanonizer.canonize(link);
            if (uri == null) {
                continue;
            }
            uri = URLCanonizer.resolver(uri, page.getUrl().getUri());
            if (!uri.getScheme().startsWith("http")) {
                continue;
            }
            uri = URLCanonizer.canonizeHttpURI(uri);
            if (uri == null) {
                continue;
            }
            if (isNotWebPage(uri)) continue;
            WebURL url = new WebURL();
            url.setFragment(uri.getFragment());
            try {
                uri = new URI(uri.getScheme(), uri.getSchemeSpecificPart(), null);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            url.setUri(uri);
            out.add(url);
        }
        page.setOutLinks(out);
	}

    static boolean isNotWebPage(URI uri) {
        String path = uri.getPath();
        if (path.contains(".") && !path.endsWith(".htm") && !path.endsWith(".html") &&
                !path.endsWith(".xml") && !path.endsWith(".php")) {
            return true;
        }
        return false;
    }
}
