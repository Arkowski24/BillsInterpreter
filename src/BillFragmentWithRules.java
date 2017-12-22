import java.util.List;

public class BillFragmentWithRules {
    public final BillFragment billFragment;
    public final List<ParserRule> parserRules;

    public BillFragmentWithRules(BillFragment billFragment, List<ParserRule> parserRules) {
        this.billFragment = billFragment;
        this.parserRules = parserRules;
    }
}
