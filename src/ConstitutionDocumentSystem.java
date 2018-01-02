import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConstitutionDocumentSystem extends DocumentSystem {

    public ConstitutionDocumentSystem(String filepath) throws  IOException{
        super();
        readDocument(filepath);
    }

    private void readDocument(String filepath) throws IOException {
        List<String> documentLines;
        try {
            documentLines = readFile(filepath);
        }
        catch (IOException e){
            throw new IOException(e);
        }
        BillDocument billDocument = new BillDocument(documentLines);

        fillCleanerRules();
        fillConstitutionParser();
        cleaner.clearDocument(billDocument);
        parser.parseDocument(billDocument);

        this.billDocument = billDocument;
    }

    private void fillConstitutionParser(){
        ParserRule parserRule = new ParserRule("((?m)^[0-9]{3}\\.)|((?m)^[0-9]{2}\\.)|((?m)^[0-9]{1}\\.)", ParserRuleType.Unlimited);
        ParserRule parserRule1 = new ParserRule("(Art.\\s[0-9]{3}\\.)|(Art.\\s[0-9]{2}\\.)|(Art.\\s[0-9]{1}\\.)", ParserRuleType.Unlimited);
        ParserRule parserRule2 = new ParserRule("(Rozdział [LCDMIVX]{4})|(Rozdział [LCDMIVX]{3})|(Rozdział [LCDMIVX]{2})|(Rozdział [LCDMIVX])", ParserRuleType.Unlimited);
        parserRule1.subRules.add(parserRule);
        parserRule2.subRules.add(parserRule1);

        parser.addParserRule(parserRule2);
    }

    private BillFragment getChapter(int chapterNumber){
        if (chapterNumber <= 0){
            throw new IllegalArgumentException("Chapter number must be positive.");
        }
        String chapterIdentifier = "Rozdział " + toRoman(chapterNumber);

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

    private BillFragment getArticle(int articleNumber){
        if (articleNumber <= 0){
            throw new IllegalArgumentException("Article number must be positive.");
        }
        String articleIdentifier = "Art. " + Integer.toString(articleNumber) + ".";

        BillFragment article = billDocument.getBillFragment().findFirstFragmentWithIdentifier(articleIdentifier);
        if (article == null){
            throw new IllegalArgumentException("Couldn't find: " + articleIdentifier);
        }

        return article;
    }

    private BillFragment getParagraph(int articleNumber, int paragraphNumber){
        if (paragraphNumber < 0){
            throw new IllegalArgumentException("Paragraph number must be positive.");
        }

        BillFragment article;
        try{
            article = getArticle(articleNumber);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't retrieve article. " + e);
        }

        String paragraphIdentifier = Integer.toString(paragraphNumber) + ".";
        BillFragment paragraph = article.findFirstFragmentWithIdentifier(paragraphIdentifier);
        if (paragraph == null){
            throw new IllegalArgumentException("Couldn't find: " + paragraphIdentifier);
        }

        return paragraph;
    }

    private List<BillFragment> getArticlesInRange(int rangeStart, int rangeEnd){
        if (rangeStart <= 0 || rangeEnd <= 0|| rangeStart > rangeEnd){
            throw new IllegalArgumentException("Illegal articles range.");
        }

        List<BillFragment> articles = new ArrayList<>();
        for (int i = rangeStart; i <= rangeEnd; i++){
            try{
                articles.add(getArticle(i));
            }
            catch (IllegalArgumentException e){
                throw new IllegalArgumentException("Illegal range. " + e);
            }
        }

        return articles;
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


    public String getArticleContent(int articleNumber){
        BillFragment article;
        try {
            article = getArticle(articleNumber);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't get content. " + e);
        }

        if (article == null){
            throw new IllegalArgumentException("No article with identifier: " + "Art. " + articleNumber + ".");
        }
        else {
            return article.getFragmentContentWithChildren();
        }
    }

    public List<String> getArticlesContentsInRange(int rangeStart, int rangeEnd){
        List<BillFragment> articles;
        try {
            articles = getArticlesInRange(rangeStart, rangeEnd);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't get content. " + e);
        }

        List<String> contents = new ArrayList<>();
        for (BillFragment article : articles){
            contents.add(article.getFragmentContentWithChildren());
        }

        return contents;
    }

    public String getParagraphContent(int articleNumber, int paragraphNumber){
        BillFragment paragraph;
        try {
            paragraph = getParagraph(articleNumber, paragraphNumber);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't get content. " + e);
        }

        return paragraph.getFragmentContentWithChildren();
    }

    public String getTableOfContents(){
        return appendList(billDocument.getBillFragment().getTableOfContents(2));
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
