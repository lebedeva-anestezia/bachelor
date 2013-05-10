package ru.ifmo.mailru.core;
import java.net.URI;
import java.net.URISyntaxException;


public class URLCanonizer {

	public static URI canonize(String url) throws URISyntaxException {
        url.trim();
        url = url.replaceAll("%7E", "~").replaceAll(" ", "%20");
        if (url.startsWith("%20")) {
            url = url.substring(3);
        }
        URI uri = new URI(url).normalize();
        return uri;
	}

    public static final URI canonizeHttpURI(URI uri) throws URISyntaxException {
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
        return new URI(uri.getScheme(), uri.getHost(), path, uri.getQuery(), uri.getFragment());
    }
	
	public static URI resolver(URI uri, URI parentURI) {
		URI res = parentURI.resolve(uri);
		return res;
	}

}
