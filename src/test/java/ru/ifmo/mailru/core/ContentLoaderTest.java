package ru.ifmo.mailru.core;

import org.junit.Assert;
import org.junit.Test;

import java.net.URI;

/**
 * @author Anastasia Lebedeva
 */
public class ContentLoaderTest {

    @Test
    public void testIsWebPage() throws Exception {
        URI[] webPages = {
            new URI("http://www.alexa.com/topsites/global"),
            new URI("http://www.medpulse.ru/news.xml"),
            new URI("http://mointerier.ru")};
        URI[] notWebPages = {
            new URI("http://im.kommersant.ru/ISSUES.PHOTO/CORP/2013/02/21/dsryu4.gif"),
        };
        for (int i = 0; i < webPages.length; i++) {
            Assert.assertTrue((new ContentLoader(webPages[i], 2)).isWebPage());
        }

        for (int i = 0; i < notWebPages.length; i++) {
            Assert.assertFalse((new ContentLoader(notWebPages[i], 2)).isWebPage());
        }
    }
}
