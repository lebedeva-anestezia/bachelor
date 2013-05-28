package ru.ifmo.mailru.features;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author Anastasia Lebedeva
 */
public class DictionaryModuleTest {

    @Ignore
    @Test
    public void testSplitIntoTokens() throws Exception {
        String s = "http://sports.ru/tribuna/blogs/novotalents/463745.html?comments=1&s=ace1a56b7b6254dc";
        System.out.println(Arrays.toString(DictionaryModule.splitIntoTokens(s)));
    }
}
