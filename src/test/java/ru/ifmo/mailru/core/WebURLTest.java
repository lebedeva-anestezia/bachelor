package ru.ifmo.mailru.core;

import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.util.Collections;
import java.util.TreeSet;

/**
 * @author Anastasia Lebedeva
 */
public class WebURLTest {

    @Test
    public void testCompareTo() throws Exception {
        TreeSet<WebURL> queue = new TreeSet<>(Collections.reverseOrder());
        int n = 10000;
        WebURL[] expectedValues = new WebURL[n];
        WebURL[] actualValues = new WebURL[n];
        for (int i = 0; i < n / 2; i++) {
            queue.add(new WebURL(new URI("ololo" + i), 0));
            expectedValues[i] = new WebURL(new URI("ololo" + (i + n/2 )), 1);
        }
        for (int i = n / 2; i < n; i++) {
            queue.add(new WebURL(new URI("ololo" + i), 1));
            expectedValues[i] = new WebURL(new URI("ololo" + (i - n/2 )), 0);
        }
        int i = 0;
        while (!queue.isEmpty()) {
            actualValues[i] = queue.pollFirst();
            i++;
        }
        //actualValues = queue.toArray(actualValues);
        Assert.assertArrayEquals(expectedValues, actualValues);
    }
}
