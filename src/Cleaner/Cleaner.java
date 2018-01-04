package Cleaner;

import DocumentRepresentation.*;

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

    public void connectBrokenWords(BillDocument billDocument){
        Pattern brokenLinePattern = Pattern.compile(".+-$");
        List<String> billDocumentLines = billDocument.getBillDocumentLines();

        for (Integer i = 0; i < billDocumentLines.size(); i++){
            Matcher matcher = brokenLinePattern.matcher(billDocumentLines.get(i));
            if(matcher.matches()){
                connectBrokenWord(billDocumentLines, i);
            }
        }

        for (Integer i = 0; i < billDocumentLines.size(); i++) {
            if(billDocumentLines.get(i) == null)
                billDocumentLines.remove(i);
        }
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
        return matcher.find();
    }

    private String deletePhraseFromLine(String line, Pattern pattern){
        Matcher matcher = pattern.matcher(line);
        String newLine = matcher.replaceAll("");
        if (newLine.length() == 0) {
            newLine = null;
        }
        return newLine;
    }

    private void connectBrokenWord(List<String> billDocumentLines, int lineOfWordNumber){
        if (lineOfWordNumber == billDocumentLines.size() - 1){
            return;
        }

        String firstHalfLine = billDocumentLines.get(lineOfWordNumber);
        String secondHalfLine = billDocumentLines.get(lineOfWordNumber + 1);

        int firstHalfPositionStart = firstHalfLine.lastIndexOf(" ") + 1;
        int firstHalfPositionFinish = firstHalfLine.length() - 1;

        String newFirstLine;
        String newSecondLine;

        if(firstHalfPositionStart == 0){
            newFirstLine = null;
        }
        else {
            newFirstLine = firstHalfLine.substring(0, firstHalfPositionStart - 1);
        }
        newSecondLine = firstHalfLine.substring(firstHalfPositionStart, firstHalfPositionFinish) + secondHalfLine;

        billDocumentLines.set(lineOfWordNumber, newFirstLine);
        billDocumentLines.set(lineOfWordNumber + 1, newSecondLine);
    }


}