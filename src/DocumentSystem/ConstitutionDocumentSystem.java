package DocumentSystem;

import Cleaner.*;
import DocumentRepresentation.*;
import Parser.*;

import java.io.IOException;

public class ConstitutionDocumentSystem extends PolishDocumentSystem {

    public ConstitutionDocumentSystem(String filepath) throws  IOException{
        super();
        fillCleanerRules();
        fillConstitutionParser();
        readDocument(filepath);
        cleaner.clearDocument(billDocument);
        cleaner.connectBrokenWords(billDocument);
        parser.parseDocument(billDocument);
    }

    private void fillCleanerRules(){
        cleaner.addNewCleanRule(new CleanerRule("^[0-9]{4}-[0-9]{2}-[0-9]{2}$", CleanerRuleType.DeleteLineWithPhrase));
        cleaner.addNewCleanRule(new CleanerRule("©Kancelaria Sejmu", CleanerRuleType.DeleteLineWithPhrase));
        cleaner.addNewCleanRule(new CleanerRule("^[a-zA-Z]$", CleanerRuleType.DeleteLineWithPhrase));
        cleaner.addNewCleanRule(new CleanerRule("^[0-9]$", CleanerRuleType.DeleteLineWithPhrase));
    }

    private void fillConstitutionParser(){
        ParserRule litera = new ParserRule("((?m)^[0-9a-z]{3}\\))|((?m)^[0-9a-z]{2}\\))|((?m)^[0-9a-z]{1}\\))", ParserRuleType.Unlimited);
        ParserRule litera2 = new ParserRule("((?m)^[0-9a-z]{3}\\))|((?m)^[0-9a-z]{2}\\))|((?m)^[0-9a-z]{1}\\))", ParserRuleType.NoMatch);
        ParserRule ustep = new ParserRule("((?m)^[0-9]{3}[a-z]{1}\\.)|((?m)^[0-9]{3}\\.)" +
                "|((?m)^[0-9]{2}[a-z]{1}\\.)|((?m)^[0-9]{2}\\.)" +
                "|((?m)^[0-9]{1}[a-z]{1}\\.)|((?m)^[0-9]{1}\\.)", ParserRuleType.Unlimited);
        ParserRule artykul = new ParserRule("(Art.\\s[0-9]{3}[a-z]{1}\\.)|(Art.\\s[0-9]{3}\\.)" +
                "|(Art.\\s[0-9]{2}[a-z]{1}\\.)|(Art.\\s[0-9]{2}\\.)" +
                "|(Art.\\s[0-9]{1}[a-z]{1}\\.)|(Art.\\s[0-9]{1}\\.)", ParserRuleType.Unlimited);
        ParserRule rozdzial = new ParserRule("(Rozdział [LCDMIVX]{4})|(Rozdział [LCDMIVX]{3})|(Rozdział [LCDMIVX]{2})|(Rozdział [LCDMIVX])", ParserRuleType.Unlimited);

        ustep.addSubRule(litera);
        artykul.addSubRule(ustep);
        artykul.addSubRule(litera2);
        rozdzial.addSubRule(artykul);

        parser.addParserRule(rozdzial);
    }

    private BillFragment getChapter(int chapterNumber){
        String chapterIdentifier = "Rozdział " + chapterNumber;

        BillFragment chapter = billDocument.getBillFragment().findFirstFragmentWithIdentifier(chapterIdentifier);
        if (chapter == null){
            throw new IllegalArgumentException("Couldn't find: " + chapterIdentifier);
        }

        return chapter;
    }

    //To Do - Implement Roman Numbers Converter
    private String toRoman(int number){
        switch (number){
            case 1:
                return "I";
            case 2:
                return "II";
            default:
                return "I";
        }
    }

    public String getChapterContent(int chapterNumber) {
        BillFragment chapter;
        try {
            chapter = getChapter(chapterNumber);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't get content. " + e);
        }

        return chapter.getFragmentContentWithChildren();
    }


    public String getChapterTableOfContents(int chapterNumber){
        BillFragment chapter;
        try {
            chapter = getChapter(chapterNumber);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't get table of contents. " + e);
        }
        return appendList(chapter.getTableOfContents(2));
    }
}
