package ru.ifmo.mailru.core;

import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.util.Collections;
import java.util.TreeSet;

/**
 * @author Anastasia Lebedeva
 */
public class WebURLTest {

    @Ignore
    @Test
    public void testCompareTo() throws Exception {
        TreeSet<WebURL> queue = new TreeSet<>(Collections.reverseOrder());
        queue.add(new WebURL(new URI("ololo1"), 1));
        queue.add(new WebURL(new URI("ololo2"), 2));
        queue.add(new WebURL(new URI("ololo3"), 0));
        queue.add(new WebURL(new URI("ololo4"), 1));
        queue.add(new WebURL(new URI("ololo5"), 0));
        queue.add(new WebURL(new URI("ololo6"), 0));
        queue.add(new WebURL(new URI("ololo6"), 0));
        queue.add(new WebURL(new URI("ololo6"), 0));
        queue.add(new WebURL(new URI("ololo6"), 0));
        queue.add(new WebURL(new URI("ololo6"), 0));
        queue.add(new WebURL(new URI("ololo6"), 0));
        queue.add(new WebURL(new URI("ololo6"), 0));
        queue.add(new WebURL(new URI("ololo6"), 0));
        while (!queue.isEmpty()) {
            WebURL url = queue.pollFirst();
            System.out.println(url.getUri() + " " + url.getRank());
        }
    }
}
