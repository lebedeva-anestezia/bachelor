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
        //Pattern aTeg = Pattern.compile("(?i)<a\\s(?:.*?\\s)*?href=[\'\"](?!#)([^\">]+)[\'\"][^>]*?>");
        Pattern aTeg = Pattern.compile("(?i)<a([^>]+)>(.+?)</a>");
        Pattern href = Pattern.compile("\\s*(?i)href\\s*=\\s*(\\\"([^\"]+\\\")|'[^']+'|([^'\">\\s]+))");
        String[] validLinkTegs = {"<li><a href=\"/toolbar-creator\">Create a Custom Toolbar</a></li>",
                "<a href=\"http://stlpublicradio.org/programs/slota/archivedetail.php?date='2012-02-29'\" title=\"\" class=\"menu_icon menu-17197 \" >Грузовые перевозки</a></li>",
                "<li><a href='http://www.tipsntracks.com/date/2012/03' title='March 2012'>March 2012</a></li>"};
        String[] invalidLinkTags = {"<a href=\"\"style=\"\"><img width=\"148\" height=\"32\" src=\"/images/buttons/btn-www-survey.gif\" /></a>"};
        String[] validLinks = new String[validLinkTegs.length];
        String[] invalidLinks = new String[invalidLinkTags.length];
        for (int i = 0; i < validLinkTegs.length; i++) {
            Matcher matcher = aTeg.matcher(validLinkTegs[i]);
            Assert.assertTrue(matcher.find());
            validLinks[i] = matcher.group(1);
        }
        for (int i = 0; i < invalidLinkTags.length; i++) {
            Matcher matcher = aTeg.matcher(invalidLinkTags[i]);
            Assert.assertTrue(matcher.find());
            invalidLinks[i] = matcher.group(1);
        }
        for (int i = 0; i < validLinkTegs.length; i++) {
            Matcher matcher = href.matcher(validLinks[i]);
            Assert.assertTrue(matcher.find());
            String s = matcher.group(1);
            System.out.println(s.substring(1, s.length() - 1));
        }
        for (int i = 0; i < invalidLinkTags.length; i++) {
            Matcher matcher = href.matcher(invalidLinks[i]);
            Assert.assertFalse(matcher.find());
        }
    }
}
