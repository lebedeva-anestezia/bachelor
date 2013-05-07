package ru.ifmo.mailru.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Anastasia Lebedeva
 */
public class PageParserTest {

    @Test
    public void hrefPatternTest() {
        Pattern aTeg = Pattern.compile("(?i)<a\\s(?:.*?\\s)*?href=[\'\"](?![#\"\'{$])+(.+?)[\'\"].*?>");
        String s = "<a href=\"\"style=\"\"><img width=\"148\" height=\"32\" src=\"/images/buttons/btn-www-survey.gif\" /></a>";
        String s1 = "<li><a href=\"/toolbar-creator\">Create a Custom Toolbar</a></li>";
        Matcher m = aTeg.matcher(s);
        Matcher m2 = aTeg.matcher(s1);
        Assert.assertFalse(m.find());
        Assert.assertTrue(m2.find());
        System.out.println(m2.group(1));
    }
}
