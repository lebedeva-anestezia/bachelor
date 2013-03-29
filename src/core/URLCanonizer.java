package core;
import java.net.URI;
import java.net.URISyntaxException;


public class URLCanonizer {

	public static URI canonize(String url) {
		try {
			url = url.replaceAll("%7E", "~").replaceAll(" ", "%20");
			URI uri = new URI(url).normalize();
			return uri;
		} catch (URISyntaxException e) {
		//	e.printStackTrace();
			System.err.println("Illegal URI syntax: " + url);
			return null;
		}
	}
	
	public static URI resolver(URI uri, URI parentURI) {
		URI res = parentURI.resolve(uri);
		return res;
	}

}
