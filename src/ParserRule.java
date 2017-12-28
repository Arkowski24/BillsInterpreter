import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ParserRule {
    public final Pattern pattern;
    public final ParserRuleType parserRuleType;
    public final List<ParserRule> subRules;
    public final Integer matchLimit;

    public ParserRule(String pattern, ParserRuleType parserRuleType, int matchLimit) {
        if (matchLimit < 0) {
            throw new IllegalArgumentException("Match limit cannot be negative.");
        }
        this.pattern = Pattern.compile(pattern);
        this.parserRuleType = parserRuleType;
        this.subRules = new ArrayList<>();
        this.matchLimit = matchLimit;
    }

    public ParserRule(String pattern, ParserRuleType parserRuleType) {
        if (parserRuleType == ParserRuleType.Limited){
            throw new IllegalArgumentException("Match limit required.");
        }
        this.pattern = Pattern.compile(pattern);
        this.parserRuleType = parserRuleType;
        this.subRules = new ArrayList<>();
        this.matchLimit = null;
    }

    public void addSubRule (ParserRule parserRule){
        subRules.add(parserRule);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParserRule that = (ParserRule) o;
        return matchLimit == that.matchLimit &&
                Objects.equals(pattern, that.pattern) &&
                parserRuleType == that.parserRuleType &&
                Objects.equals(subRules, that.subRules);
    }

    @Override
    public int hashCode() {

        return Objects.hash(pattern, parserRuleType, subRules, matchLimit);
    }
}
