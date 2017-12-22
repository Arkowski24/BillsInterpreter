import java.util.regex.Matcher;

public class ParserMatcher {
    public final Matcher matcher;
    public final ParserRule rule;
    public boolean availability;

    public ParserMatcher(Matcher matcher, ParserRule rule, boolean availability) {
        this.matcher = matcher;
        this.rule = rule;
        this.availability = availability;
    }
}
