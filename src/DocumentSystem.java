import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class DocumentSystem {
    protected Cleaner cleaner;
    protected Parser parser;
    protected BillDocument billDocument;

    public DocumentSystem(){
        cleaner = new Cleaner();
        parser = new Parser();
    }

    protected List<String> readFile(String filepath) throws IOException {
        List<String> documentLines = new ArrayList<>();

        try (BufferedReader documentReader = new BufferedReader(new FileReader (filepath))){

            String line = documentReader.readLine();
            while (line != null) {
                documentLines.add(line);
                line = documentReader.readLine();
            }
        }
        catch (IOException e){
            throw new IOException("Document could not be read.", e);
        }

        return documentLines;
    }

    protected void fillCleanerRules(){
        cleaner.addNewCleanRule(new CleanerRule("^[0-9]{4}-[0-9]{2}-[0-9]{2}$", CleanerRuleType.DeleteLineWithPhrase));
        cleaner.addNewCleanRule(new CleanerRule("©Kancelaria Sejmu", CleanerRuleType.DeleteLineWithPhrase));
        cleaner.addNewCleanRule(new CleanerRule("^[a-zA-Z]$", CleanerRuleType.DeleteLineWithPhrase));
        cleaner.addNewCleanRule(new CleanerRule("^[0-9]$", CleanerRuleType.DeleteLineWithPhrase));
    }

    protected String appendList(List<String> list){
        if (list == null){
            throw new IllegalArgumentException("Null list cannot be appended.");
        }
        String newString = "";
        for (String line : list){
            if (line != null && line.trim().length() > 0){
                newString += line + "\n";
            }
        }
        return newString;
    }
}
