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
        //Pattern aTeg = Pattern.compile("(?i)<a\\s(?:.*?\\s)*?href=[\'\"](?![#\"\'{$])(.+?)(#.*)?[\'\"].*?>");
        Pattern aTeg = Pattern.compile("(?i)<a\\s(?:.*?\\s)*?href=[\'\"](?![#\"\'{$])(.+?)[\'\"].*?>");
        Pattern nonHTML = Pattern.compile(".*(\\.(pdf|jpe?g|tar|rar|zip|gz|7z|css|avi|djvu|png|rtf|txt))$");
        m = aTeg.matcher(page.getContent());
        Set<WebURL> out = new HashSet<>();
        while (m.find()) {
            try {
                String link = m.group(1);
                if (link.equals("http:/velospace.org/node")) {
                    link = "http://velospace.org/node";   //TODO: убрать заглушку
                }
                URI uri = URLCanonizer.canonize(link);
                if (nonHTML.matcher(uri.toString()).matches()) {
                    continue;
                }
                uri = URLCanonizer.resolver(uri, page.getUrl().getUri());
                if (!uri.getScheme().startsWith("http")) {
                    continue;
                }
                uri = URLCanonizer.canonizeHttpURI(uri);
                WebURL url = new WebURL();
                url.setFragment(uri.getFragment());
                uri = new URI(uri.getScheme(), uri.getSchemeSpecificPart(), null);
                if (isRuURL(uri)) {
                    url.setUri(uri);
                    out.add(url);
                }
            } catch (URISyntaxException e) {
                //so sad =(
            }
        }
        page.setOutLinks(out);
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
