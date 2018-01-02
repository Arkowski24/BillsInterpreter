package Parser;

import java.util.regex.Matcher;

public class ParserMatcher {
    public final Matcher matcher;
    public final ParserRule rule;
    public boolean availability;
    public int timesMatched;

    public ParserMatcher(Matcher matcher, ParserRule rule) {
        this.matcher = matcher;
        this.rule = rule;
        this.availability = true;
        timesMatched = 0;
    }
}
