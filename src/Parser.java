import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class Parser {
    private List<ParserRule> billParserRules;

    public Parser(){
        billParserRules = new ArrayList<>();
    }

    public void addParserRule(ParserRule parserRule){
        billParserRules.add(parserRule);
    }

    public void parseDocument(BillDocument billDocument){
        List<String> lines = billDocument.getBillDocumentLines();
        BillFragment billFragment = new BillFragment();

        String content = "";
        for (String billLine : lines){
            content += billLine + "\n";
        }

        billFragment.setParent(null);
        billFragment.setContent(content);

        parseFragment(new BillFragmentWithRules(billFragment, billParserRules));

        billDocument.setBillFragment(billFragment);
    }

    private void parseFragment(BillFragmentWithRules parent){
        if (parent == null || parent.billFragment == null
                || parent.parserRules == null){
            return;
        }

        List<BillFragmentWithRules> children = getFragments(parent);
        if (children == null)
            return;

        for (BillFragmentWithRules child : children){
            child.billFragment.setParent(parent.billFragment);
            parent.billFragment.addChild(child.billFragment);
            parseFragment(child);
        }
    }

    private List<BillFragmentWithRules> getFragments(BillFragmentWithRules parent){
        BillFragment billFragment = parent.billFragment;
        List<ParserRule> parserRules = parent.parserRules;

        String content = billFragment.getContent();
        List<ParserMatcher> matchers = new ArrayList<>();

        for (ParserRule parserRule : parserRules){
            Matcher matcher = parserRule.pattern.matcher(content);
            matchers.add(new ParserMatcher(matcher, parserRule, true));
        }

        for (ParserMatcher matcher : matchers){
            matcher.availability = matcher.matcher.find();
        }

        BillFragmentWithRules nextBillFragment = getNextFragment(content, matchers);
        if (nextBillFragment == null){
            return null;
        }

        List<BillFragmentWithRules> fragmentList = new ArrayList<>();
        int contentEndPosition = getContentEndPosition(content, nextBillFragment.billFragment);

        while (nextBillFragment != null){
            fragmentList.add(nextBillFragment);
            nextBillFragment = getNextFragment(content, matchers);
        }

        String newContent = content.substring(0, contentEndPosition);
        billFragment.setContent(newContent);

        return fragmentList;
    }

    private BillFragmentWithRules getNextFragment(String content, List<ParserMatcher> matchers){
        int identifierStartPosition = Integer.MAX_VALUE;
        ParserMatcher choosenMatcher = null;

        for (ParserMatcher parserMatcher : matchers){
            if (parserMatcher.availability) {
                int parserEndPosition = parserMatcher.matcher.start();
                if (parserEndPosition < identifierStartPosition) {
                    identifierStartPosition = parserEndPosition;
                    choosenMatcher = parserMatcher;
                }
            }
        }

        if (choosenMatcher == null){
            return null;
        }

        int identifierEndPosition = choosenMatcher.matcher.end();
        String newBillFragmentIdentifier = content.substring(identifierStartPosition, identifierEndPosition);
        String newBillFragmentContent = content.substring(identifierEndPosition + 1, content.length());

        BillFragment newBillFragment = new BillFragment();
        newBillFragment.setIdentifier(newBillFragmentIdentifier);
        newBillFragment.setContent(newBillFragmentContent);
        List<ParserRule> newBillRules = choosenMatcher.rule.subRules;

        choosenMatcher.availability = choosenMatcher.matcher.find();

        return new BillFragmentWithRules(newBillFragment, newBillRules);
    }

    private int getContentEndPosition(String content, BillFragment firstBillFragment){
        return content.indexOf(firstBillFragment.getIdentifier());
    }
}
