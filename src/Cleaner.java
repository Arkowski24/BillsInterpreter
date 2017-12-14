import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Cleaner {
    private List<CleanerRule> cleanerRules;

    public Cleaner() {
        cleanerRules = new ArrayList<>();
    }

    public Cleaner(List<CleanerRule> cleanerRules){
        cleanerRules = cleanerRules;
    }

    public void addNewCleanRule(CleanerRule cleanerRule) {
        cleanerRules.add(cleanerRule);
    }

    public void clearDocument(BillDocument billDocument){
        List<String> documentLines = billDocument.getBillDocumentLines();
        for (CleanerRule cleanerRule : cleanerRules){
            switch (cleanerRule.cleanerRuleType){
                case DeleteLineWithPhrase:
                    documentLines = deleteLineRule(cleanerRule.regexPattern, documentLines);
                    break;
                case DeletePhrase:
                    documentLines = deletePhrase(cleanerRule.regexPattern, documentLines);
                    break;
            }
        }
        billDocument.setBillDocumentLines(documentLines);
    }

    private List<String>  deleteLineRule(Pattern rulePattern, List<String> documentLines){
        List<String> newDocumentLines = documentLines.stream()
                .filter((String s) -> notContainsPattern(s, rulePattern))
                .collect(Collectors.toList());
        return newDocumentLines;
    }

    private List<String> deletePhrase(Pattern rulePattern, List<String> documentLines){
        List<String> newDocumentLines = documentLines.stream()
                .map((String s) -> deletePhraseFromLine(s, rulePattern))
                .filter((String s) -> s != null)
                .collect(Collectors.toList());
        return newDocumentLines;
    }

    private boolean notContainsPattern(String line, Pattern pattern){
        return !containsPattern(line, pattern);
    }

    private boolean containsPattern(String line, Pattern pattern){
        Matcher matcher = pattern.matcher(line);
        return matcher.matches();
    }

    private String deletePhraseFromLine(String line, Pattern pattern){
        Matcher matcher = pattern.matcher(line);
        String newLine = matcher.replaceAll("");
        if (newLine.length() == 0) {
            newLine = null;
        }
        return newLine;
    }


}