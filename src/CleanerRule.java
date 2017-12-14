import java.util.regex.Pattern;

public class CleanerRule {
    public final Pattern regexPattern;
    public final CleanerRuleType cleanerRuleType;

    public CleanerRule(String cleanerRulePattern, CleanerRuleType cleanerRuleType){
        this.regexPattern = Pattern.compile(cleanerRulePattern);
        this.cleanerRuleType = cleanerRuleType;
    }
}
