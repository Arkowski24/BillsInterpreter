import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BillFragmentParserRule {
    public final Pattern pattern;
    public final BillFragmentParserRuleType billFragmentParserRuleType;
    public List<BillFragmentParserRule> subRules;

    public BillFragmentParserRule(Pattern pattern, BillFragmentParserRuleType billFragmentParserRuleType) {
        this.pattern = pattern;
        this.billFragmentParserRuleType = billFragmentParserRuleType;
        subRules = new ArrayList<>();
    }

    public void addSubRule (BillFragmentParserRule billFragmentParserRule){
        subRules.add(billFragmentParserRule);
    }

}
