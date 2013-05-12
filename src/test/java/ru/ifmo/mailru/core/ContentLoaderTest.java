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
            new URI("http://mointerier.ru"),
            new URI("http://www.auditor-it.ru"),
           // new URI("http://101dizain.ru"),
            new URI("http://www.azard.ru/"),
            new URI("http://www.allventure.ru/lib")
            };
        URI[] notWebPages = {
            new URI("http://im.kommersant.ru/ISSUES.PHOTO/CORP/2013/02/21/dsryu4.gif"),
            new URI("http://www.avtomaslo.ru/price_BP_indust.xls"),
            new URI("http://www.linopt.ru/price.doc")
        };
        for (int i = 0; i < webPages.length; i++) {
            Assert.assertTrue((new ContentLoader(webPages[i], 2)).isWebPage());
            System.out.println(webPages[i]);
        }

        for (int i = 0; i < notWebPages.length; i++) {
            Assert.assertFalse((new ContentLoader(notWebPages[i], 2)).isWebPage());
        }
    }

}
