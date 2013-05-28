package ru.ifmo.mailru.features;

import org.junit.Test;

import java.util.Arrays;

/**
 * @author Anastasia Lebedeva
 */
public class FeaturesExtractorTest {

    @Test
    public void testBuildVector() throws Exception {
        String uri1 = "http://smartresponder.ru/l_ru/catalog?toLevelUp=/l_ru/catalog/&catId=134&opened=1";
        FeaturesExtractor extractor = new FeaturesExtractor(uri1);
        Double[] res = extractor.buildVector();
        System.out.println(Arrays.toString(res));
    }
}
