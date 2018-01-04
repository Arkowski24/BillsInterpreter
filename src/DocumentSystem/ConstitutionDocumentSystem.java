package DocumentSystem;

import Cleaner.*;
import DocumentRepresentation.*;
import Parser.*;
import com.martiansoftware.jsap.JSAPResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ConstitutionDocumentSystem extends PolishDocumentSystem {
    public void interpret(JSAPResult parsingResults){
        boolean showTableOfContents = parsingResults.getBoolean("showTableOfContents");
        if (showTableOfContents){
            interpretTableOfContents(parsingResults);
        }
        else {
            interpretShowChapter(parsingResults);
        }
    }

    private void interpretTableOfContents(JSAPResult parsingResults){
        String chapter = parsingResults.getString("chapter");
        if (chapter == null){
            System.out.println(this.getTableOfContents());
        }
        else {
            try {
                System.out.println(this.getChapterTableOfContents(chapter));
            }
            catch (IllegalArgumentException e){
                System.err.println("No such chapter.");
                return;
            }
        }
    }

    private void interpretShowChapter(JSAPResult parsingResults) {
        String chapterNumber = parsingResults.getString("chapter");
        if (chapterNumber == null){
            interpretShowArticleRange(parsingResults);
        }
        else System.out.println(this.getChapterContent(chapterNumber));
    }

    protected void showArticleSpecifics(JSAPResult parsingResults){
        List<String> specifics = correctSpecifics(Arrays.asList(parsingResults.getStringArray("articleSpecifics")));
        String articleNumber = getArticleSpecific(specifics);
        String paragraphNumber = getParagraphSpecific(specifics);
        String pointNumber = getPointSpecific(specifics);

        if (articleNumber == null){
            System.err.println("Article number required.");
            return;
        }

        showPoint(pointNumber, paragraphNumber, articleNumber);
    }

    public ConstitutionDocumentSystem(String filepath) throws  IOException{
        super();
        fillCleanerRules();
        fillConstitutionParser();
        readDocument(filepath);
        cleaner.clearDocument(billDocument);
        cleaner.connectBrokenWords(billDocument);
        parser.parseDocument(billDocument);
        fixPreamble();
    }

    public ConstitutionDocumentSystem(List<String> fileLines){
        super();
        fillCleanerRules();
        fillConstitutionParser();
        readDocument(fileLines);
        cleaner.clearDocument(billDocument);
        cleaner.connectBrokenWords(billDocument);
        parser.parseDocument(billDocument);
        fixPreamble();
    }

    private void fillCleanerRules(){
        cleaner.addNewCleanRule(new CleanerRule("^[0-9]{4}-[0-9]{2}-[0-9]{2}$", CleanerRuleType.DeleteLineWithPhrase));
        cleaner.addNewCleanRule(new CleanerRule("©Kancelaria Sejmu", CleanerRuleType.DeleteLineWithPhrase));
        cleaner.addNewCleanRule(new CleanerRule("^[a-zA-Z]$", CleanerRuleType.DeleteLineWithPhrase));
        cleaner.addNewCleanRule(new CleanerRule("^[0-9]$", CleanerRuleType.DeleteLineWithPhrase));
    }

    private void fillConstitutionParser(){
        ParserRule punkt = new ParserRule("((?m)^[0-9a-z]{3}\\))|((?m)^[0-9a-z]{2}\\))|((?m)^[0-9a-z]{1}\\))", ParserRuleType.Unlimited);
        ParserRule punkt2 = new ParserRule("((?m)^[0-9a-z]{3}\\))|((?m)^[0-9a-z]{2}\\))|((?m)^[0-9a-z]{1}\\))", ParserRuleType.NoMatch);
        ParserRule ustep = new ParserRule("((?m)^[0-9]{3}[a-z]{1}\\.)|((?m)^[0-9]{3}\\.)" +
                "|((?m)^[0-9]{2}[a-z]{1}\\.)|((?m)^[0-9]{2}\\.)" +
                "|((?m)^[0-9]{1}[a-z]{1}\\.)|((?m)^[0-9]{1}\\.)", ParserRuleType.Unlimited);
        ParserRule artykul = new ParserRule("(Art.\\s[0-9]{3}[a-z]{1}\\.)|(Art.\\s[0-9]{3}\\.)" +
                "|(Art.\\s[0-9]{2}[a-z]{1}\\.)|(Art.\\s[0-9]{2}\\.)" +
                "|(Art.\\s[0-9]{1}[a-z]{1}\\.)|(Art.\\s[0-9]{1}\\.)", ParserRuleType.Unlimited);
        ParserRule tytul = new ParserRule("(?m)^[A-Z\\W]+$", ParserRuleType.Unlimited);
        ParserRule rozdzial = new ParserRule("(Rozdział [LCDMIVX]{4})|(Rozdział [LCDMIVX]{3})|(Rozdział [LCDMIVX]{2})|(Rozdział [LCDMIVX])", ParserRuleType.Unlimited);
        ParserRule preambula = new ParserRule("(Preambula)|(z dnia 2 kwietnia 1997 r.)", ParserRuleType.Limited, 1);

        ustep.addSubRule(punkt);
        artykul.addSubRule(ustep);
        artykul.addSubRule(punkt2);
        tytul.addSubRule(artykul);
        rozdzial.addSubRule(tytul);
        parser.addParserRule(preambula);
        parser.addParserRule(rozdzial);
    }

    private void fixPreamble(){
        BillFragment preamble = billDocument.getBillFragment().findFirstFragmentWithIdentifier("z dnia 2 kwietnia 1997 r.");
        if (preamble == null){
            return;
        }
        else {
            preamble.setIdentifier("Preambuła");
        }
    }

    @Override
    public String getTableOfContents(){
        BillFragment fragment = billDocument.getBillFragment();
        if (fragment == null){
            throw new IllegalStateException("Document hasn't been parsed, yet.");
        }

        Predicate<BillFragment> terminalPredicate = (BillFragment x) -> (x.getIdentifier() == null || x.getIdentifier().matches("[\\WA-Z]+") || x.getIdentifier().contains("Rozdział"));
        Predicate<String> contentPredicate = (String x) -> x != null && x.replaceAll(".", "").length() == 0;
        return appendList(fragment.getTableOfContentsWithEndingPredicateAndContentPredicate(2, terminalPredicate, contentPredicate));
    }

    private BillFragment getChapter(String chapterNumber){
        String chapterIdentifier = "Rozdział " + getRomanNumber(chapterNumber);

        BillFragment chapter = billDocument.getBillFragment().findFirstFragmentWithIdentifier(chapterIdentifier);
        if (chapter == null){
            throw new IllegalArgumentException("Couldn't find: " + chapterIdentifier);
        }

        return chapter;
    }

    public String getChapterContent(String chapterNumber) {
        BillFragment chapter;
        try {
            chapter = getChapter(chapterNumber);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't get content. " + e);
        }

        return chapter.getFragmentContentWithChildren();
    }

    public String getChapterTableOfContents(String chapterNumber){
        BillFragment chapter;
        try {
            chapter = getChapter(chapterNumber);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't get table of contents. " + e);
        }
        Predicate<BillFragment> terminalPredicate = (BillFragment x) -> (x.getIdentifier() != null && (x.getIdentifier().matches("[A-Z\\W]+") || x.getIdentifier().contains("Rozdział")));
        Predicate<String> contentPredicate = (String x) -> x != null && x.replaceAll(".", "").length() == 0;
        return appendList(chapter.getTableOfContentsWithEndingPredicateAndContentPredicate(2, terminalPredicate, contentPredicate));
    }
}
