import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        DocumentSystem documentSystem = new DocumentSystem();
        BillDocument billDocument;
        try {
            billDocument = documentSystem.readDocument("konstytucja.txt");
        }
        catch (IOException e){
            System.out.println("Cannot open file.");
            return;
        }
        Cleaner cleaner = new Cleaner();
        cleaner.addNewCleanRule(new CleanerRule("^[0-9]{4}-[0-9]{2}-[0-9]{2}$", CleanerRuleType.DeleteLineWithPhrase));
        cleaner.addNewCleanRule(new CleanerRule("©Kancelaria Sejmu", CleanerRuleType.DeleteLineWithPhrase));
        cleaner.addNewCleanRule(new CleanerRule("^[a-zA-Z]$", CleanerRuleType.DeleteLineWithPhrase));
        cleaner.addNewCleanRule(new CleanerRule("^[0-9]$", CleanerRuleType.DeleteLineWithPhrase));

        cleaner.clearDocument(billDocument);
        cleaner.connectBrokenWords(billDocument);

        Parser parser = new Parser();
        ParserRule parserRule = new ParserRule("(^[0-9]{3}\\.)|(^[0-9]{2}\\.)|(^[0-9]{1}\\.)", ParserRuleType.Unlimited);
        ParserRule parserRule1 = new ParserRule("(^Art.\\s[0-9]{3}\\.)|(Art.\\s[0-9]{2}\\.)|(Art.\\s[0-9]{1}\\.)", ParserRuleType.Unlimited);
        ParserRule parserRule2 = new ParserRule("(Rozdział [LCDMIVX]{4})|(Rozdział [LCDMIVX]{3})|(Rozdział [LCDMIVX]{2})|(Rozdział [LCDMIVX])", ParserRuleType.Unlimited);
        parserRule1.subRules.add(parserRule);
        parserRule2.subRules.add(parserRule1);

        parser.addParserRule(parserRule2);

        parser.parseDocument(billDocument);
        String tableOfContents = billDocument.getBillFragment().getTableOfContentsAsLine(2);
        BillFragment content = billDocument.getBillFragment().findFragmentWithIdentifier("Art. 12.");
        String newContent = billDocument.getBillFragment().getContentWithSubchildren();
        System.out.print(tableOfContents);
    }
}
