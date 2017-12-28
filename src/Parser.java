import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class Parser {
    private List<ParserRule> billParserRules;

    public Parser() {
        billParserRules = new ArrayList<>();
    }

    public void addParserRule(ParserRule parserRule) {
        billParserRules.add(parserRule);
    }

    public void parseDocument(BillDocument billDocument) {
        List<String> lines = billDocument.getBillDocumentLines();
        BillFragment billFragment = new BillFragment();

        String content = "";
        for (String billLine : lines) {
            content += billLine + "\n";
        }

        billFragment.setParent(null);
        billFragment.setContent(content);

        parseFragment(new BillFragmentWithRules(billFragment, billParserRules, 0));

        billDocument.setBillFragment(billFragment);
    }

    private void parseFragment(BillFragmentWithRules parent) {
        if (parent == null || parent.billFragment == null
                || parent.parserRules == null) {
            return;
        }

        List<BillFragmentWithRules> children = getFragments(parent);
        if (children == null)
            return;

        for (BillFragmentWithRules child : children) {
            child.billFragment.setParent(parent.billFragment);
            parent.billFragment.addChild(child.billFragment);
            parseFragment(child);
        }
    }

    private List<BillFragmentWithRules> getFragments(BillFragmentWithRules parent) {
        //Create matcher for given content using patterns from rules
        String content = parent.billFragment.getContent();
        List<ParserMatcher> matchers = new ArrayList<>();
        for (ParserRule parserRule : parent.parserRules) {
            Matcher matcher = parserRule.pattern.matcher(content);
            matchers.add(new ParserMatcher(matcher, parserRule));
        }

        //Find first pattern match
        for (ParserMatcher matcher : matchers) {
            matcher.availability = matcher.matcher.find();
        }

        //Find first fragment and separate parent content from parsable children, no match - no content to parse
        BillFragmentWithRules nextBillFragment = getNextFragment(content, matchers);
        if (nextBillFragment == null) {
            return null;
        }
        int fragmentContentEnd = nextBillFragment.startPosition - 1;
        if (fragmentContentEnd < 0) {
            parent.billFragment.setContent(""); //All is parsable
        } else {
            parent.billFragment.setContent(content.substring(0, fragmentContentEnd));
        }

        //Parse all children according to rules
        List<BillFragmentWithRules> fragmentList = new ArrayList<>();
        while (nextBillFragment != null) {
            fragmentList.add(nextBillFragment);
            nextBillFragment = getNextFragment(content, matchers);
        }

        return fragmentList;
    }

    private BillFragmentWithRules getNextFragment(String content, List<ParserMatcher> matchers) {
        int identifierStartPosition = Integer.MAX_VALUE;
        ParserMatcher chosenMatcher = null;

        //Find the lowest starting position of the new fragment
        for (ParserMatcher parserMatcher : matchers) {
            if (parserMatcher.availability) {
                int parserEndPosition = parserMatcher.matcher.start();
                if (parserEndPosition < identifierStartPosition) {
                    identifierStartPosition = parserEndPosition;
                    chosenMatcher = parserMatcher;
                }
            }
        }

        //No match - no new fragment, if found increase the counter
        if (chosenMatcher == null) {
            return null;
        }
        chosenMatcher.timesMatched++;

        int identifierEndPosition = chosenMatcher.matcher.end();
        //Check whether the rule still applies
        chosenMatcher.availability = chosenMatcher.matcher.find(); //No next match
        if(chosenMatcher.rule.parserRuleType == ParserRuleType.Limited
                && chosenMatcher.timesMatched == chosenMatcher.rule.matchLimit){
            chosenMatcher.availability = false; //Limit exhausted
        }

        int contentEndPosition = content.length();
        //Find the next starting position - end of the current fragment
        for (ParserMatcher parserMatcher : matchers) {
            if (parserMatcher.availability) {
                int parserEndPosition = parserMatcher.matcher.start();
                if (parserEndPosition < contentEndPosition) {
                    contentEndPosition = parserEndPosition;
                }
            }
        }

        //Separate fragment identifier and content from the rest
        String newBillFragmentIdentifier = content.substring(identifierStartPosition, identifierEndPosition);
        String newBillFragmentContent = content.substring(identifierEndPosition + 1, contentEndPosition);

        //Creating new fragment and binding parsing rules to it
        BillFragment newBillFragment = new BillFragment();
        List<ParserRule> newBillRules = chosenMatcher.rule.subRules;
        newBillFragment.setIdentifier(newBillFragmentIdentifier);
        newBillFragment.setContent(newBillFragmentContent);

        return new BillFragmentWithRules(newBillFragment, newBillRules, identifierStartPosition);
    }
}