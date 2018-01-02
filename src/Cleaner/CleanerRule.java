package Cleaner;

import java.util.regex.Pattern;

public class CleanerRule {
    public final Pattern regexPattern;
    public final CleanerRuleType cleanerRuleType;

    public CleanerRule(String cleanerRulePattern, CleanerRuleType cleanerRuleType){
        this.regexPattern = Pattern.compile(cleanerRulePattern);
        this.cleanerRuleType = cleanerRuleType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CleanerRule that = (CleanerRule) o;

        if (!regexPattern.equals(that.regexPattern)) return false;
        return cleanerRuleType == that.cleanerRuleType;
    }

    @Override
    public int hashCode() {
        int result = regexPattern.hashCode();
        result = 31 * result + cleanerRuleType.hashCode();
        return result;
    }
}
