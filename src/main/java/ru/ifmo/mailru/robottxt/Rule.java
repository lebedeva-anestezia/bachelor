package ru.ifmo.mailru.robottxt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author Anastasia Lebedeva
*/
public class Rule implements Comparable<Rule> {
    private String locate;
    private RuleType type;
    private Pattern pattern;

    RuleType getType() {
        return type;
    }

    Pattern getPattern() {
        return pattern;
    }

    public Rule(String s) {
       try {
           Matcher matcher = PolitenessModule.rulePattern.matcher(s);
           matcher.find();
           this.type = RuleType.valueOf(matcher.group(1));
           this.locate = matcher.group(2);
           String regex = locate.replaceAll("\\*", ".*").replaceAll("\\?", "\\\\?");
           if (!regex.endsWith("$")) {
               regex = regex + ".*";
           }
           this.pattern = Pattern.compile(regex);
       } catch (Exception e) {
           System.err.println(e.getMessage() + " in string: " + s);
       }
    }

    public Rule(String locate, RuleType type, Pattern pattern) {
        this.locate = locate;
        this.type = type;
        this.pattern = pattern;
    }

    @Override
    public int compareTo(Rule o) {
        return this.locate.compareTo(o.locate);
    }
}
