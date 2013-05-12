package ru.ifmo.mailru.core;

import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;

/**
 * @author Anastasia Lebedeva
 */
public class URLCanonizerTest {

    @Ignore
    @Test
    public void testCanonizeHttpURI() throws Exception {
       // URI uri = new URI("http://stlpublicradio.org/programs/slota/archivedetail.php?date='2012-02-29'");
        URI uri = new URI("http://stlpublicradio.org/");
        System.out.println(uri.getScheme());
        System.out.println(uri.getHost());
        System.out.println(uri.getPath());
        System.out.println(URLCanonizer.canonizeHttpURI(uri).toString());
    }
}
