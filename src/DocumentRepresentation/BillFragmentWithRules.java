package DocumentRepresentation;

import Parser.ParserRule;

import java.util.List;

public class BillFragmentWithRules {
    public final BillFragment billFragment;
    public final List<ParserRule> parserRules;
    public final int startPosition; //Required to separate actual content from things to parse

    public BillFragmentWithRules(BillFragment billFragment, List<ParserRule> parserRules, int startPosition) {
        this.billFragment = billFragment;
        this.parserRules = parserRules;
        this.startPosition = startPosition;
    }
}
