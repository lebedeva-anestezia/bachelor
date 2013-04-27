package ru.ifmo.mailru.core;
import java.net.URI;
import java.net.URISyntaxException;


public class URLCanonizer {

	public static URI canonize(String url) {
		try {
            url.trim();
			url = url.replaceAll("%7E", "~").replaceAll(" ", "%20");
            if (url.startsWith("%20")) {
                url = url.substring(3);
            }
			URI uri = new URI(url).normalize();
            return uri;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			System.err.println("Illegal URI syntax: " + url);
			return null;
		}
	}

    public static final URI canonizeHttpURI(URI uri) {
        String path = uri.getPath();
        if (path != null) {
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            if (path.equals("")) {
                path = null;
            } else {
                if (!path.startsWith("/")) {
                    path = "/" + path;
                }
            }
        }
        try {
            return new URI(uri.getScheme(), uri.getHost(), path, uri.getQuery(), uri.getFragment());
        } catch (URISyntaxException e) {
            System.err.println("Illegal http URI index: " + uri);
            e.printStackTrace();
            return null;
        }
    }
	
	public static URI resolver(URI uri, URI parentURI) {
		URI res = parentURI.resolve(uri);
		return res;
	}

}
