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
        billDocument.connectBrokenWords();
    }
}
