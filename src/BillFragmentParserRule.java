import java.util.List;
import java.util.regex.Pattern;

public class BillFragmentParserRule {
    public final Pattern pattern;
    public final BillFragmentParserRule billFragmentParserRule;
    public List<BillFragmentParserRule> subRulees;

    public BillFragmentParserRule(Pattern pattern, BillFragmentParserRule billFragmentParserRule) {
        this.pattern = pattern;
        this.billFragmentParserRule = billFragmentParserRule;
    }
}
