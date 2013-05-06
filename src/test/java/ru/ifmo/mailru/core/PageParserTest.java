package ru.ifmo.mailru.core;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Anastasia Lebedeva
 */
public class PageParserTest {

    @Test
    public void testIsWebPage() throws Exception {
        String[] urls = new String[] {
                "http://www.archive.org/download/pm004/09-Kill_You_Dead.php",
                "http://www.archive.org/download/pm004/09-Kill_You_Dead.html"
        };
        for (String url : urls) {
            assertFalse(PageParser.isNotWebPage(new URI(url)));
        }
    }

    @Test
    public void testIsNotWebPage() throws Exception {
        String[] urls = new String[] {
                "http://www.archive.org/download/pm004/09-Kill_You_Dead.mp3"
        };
        for (String url : urls) {
            assertTrue(PageParser.isNotWebPage(new URI(url)));
        }
    }
}
