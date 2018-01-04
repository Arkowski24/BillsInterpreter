package DocumentSystem;

import DocumentRepresentation.BillFragment;
import DocumentRepresentation.DocumentType;
import com.martiansoftware.jsap.JSAPResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public abstract class PolishDocumentSystem extends AbstractDocumentSystem {
    PolishDocumentSystem(){
        super();
    }

    public abstract void interpret(JSAPResult parsingResults);

    protected void interpretShowArticleRange(JSAPResult parsingResults) {
        String[] articlesNumbers = parsingResults.getStringArray("articles");
        if (articlesNumbers == null || articlesNumbers.length == 0){
            interpretShowArticle(parsingResults);
        }
        else showArticleRange(parsingResults);
    }

    protected void interpretShowArticle(JSAPResult parsingResults){
        String articleNumber = parsingResults.getString("article");
        if (articleNumber == null){
            interpretShowArticleSpecifics(parsingResults);
        }
        else showArticle(parsingResults);
    }

    protected void interpretShowArticleSpecifics(JSAPResult parsingResults){
        String[] articleSpecifics = parsingResults.getStringArray("articleSpecifics");
        if (articleSpecifics == null || articleSpecifics.length == 0){
            //Show full document
            System.out.println(this.billDocument.getBillFragment().getFragmentContentWithChildren());
        }
        else {
            showArticleSpecifics(parsingResults);
        }
    }

    private void showArticleRange(JSAPResult parsingResults){
        String[] articles = parsingResults.getStringArray("articles");
        if (articles.length < 2){
            System.out.println("Not enough arguments to create range.");
        }
        List<String> articlesToPrint = new ArrayList<>();
        int articlesLength = articles.length;
        if (articlesLength % 2 == 1) {
            articlesLength--;
        }
        for (int i = 0; i < articlesLength; i+=2){
            try {
                articlesToPrint.addAll(this.getArticlesInRangeContents(articles[i], articles[i + 1]));
            }
            catch (IllegalArgumentException e){
                System.err.println("Given range is not valid.");
                return;
            }
        }
        for (String article : articlesToPrint) {
            System.out.println(article);
        }
        return;
    }

    private void showArticle(JSAPResult parsingResults){
        String articleNumber = parsingResults.getString("article");
        try {
            System.out.println(getArticleContent(articleNumber));
        }
        catch (IllegalArgumentException e){
            System.err.println("No such article.");
            return;
        }
    }

    protected abstract void showArticleSpecifics(JSAPResult parsingResults);

    protected void showPoint(String pointNumber, String paragraphNumber, String articleNumber){
        if (pointNumber != null && articleNumber != null){
            if (paragraphNumber != null) {
                try {
                    System.out.println(getPointContent(articleNumber, paragraphNumber, pointNumber));
                } catch (IllegalArgumentException e) {
                    System.err.println("No such point.");
                    return;
                }
            }
            else {
                try {
                    System.out.println(getPointContent(articleNumber, pointNumber));
                } catch (IllegalArgumentException e) {
                    System.err.println("No such point.");
                    return;
                }
            }
        }
        else {
            showParagraph(paragraphNumber, articleNumber);
        }
    }

    protected void showParagraph(String paragraphNumber, String articleNumber){
        if (paragraphNumber != null && articleNumber != null){
            try {
                System.out.println(getParagraphContent(articleNumber, paragraphNumber));
            }
            catch (IllegalArgumentException e){
                System.err.println("No such paragraph.");
                return;
            }
        }
        else {
            showArticle(articleNumber);
        }
    }

    protected  void  showArticle(String articleNumber){
        if (articleNumber != null){
            try {
                System.out.println(getArticleContent(articleNumber));
            }
            catch (IllegalArgumentException e){
                System.err.println("No such article.");
                return;
            }
        }
    }

    protected List<String> correctSpecifics(List<String> specifics){
        String appended = "";
        for (String specific : specifics){
            appended += specific.replaceAll("\\.","").replaceAll("\\)", "");
        }
        return Arrays.asList(appended.split(","));
    }

    protected String getArticleSpecific(List<String> specifics){
        for (String specific : specifics){
            if (specific.matches("^art[0-9]+")){
                return specific.substring(3);
            }
        }
        return null;
    }

    protected String getParagraphSpecific(List<String> specifics){
        for (String specific : specifics){
            if (specific.matches("^ust[0-9]+")){
                return specific.substring(3);
            }
        }
        return null;
    }

    protected String getPointSpecific(List<String> specifics){
        for (String specific : specifics){
            if (specific.matches("^pkt[0-9]+")){
                return specific.substring(3);
            }
        }
        return null;
    }

    protected String getLetterSpecific(List<String> specifics){
        for (String specific : specifics){
            if (specific.matches("^lit[a-z]+")){
                return specific.substring(3);
            }
        }
        return null;
    }
    @Override
    public abstract String getTableOfContents();

    //<editor-fold desc="Document fragment retrievers">
    public BillFragment getArticle(String articleNumber){
        List<BillFragment> article;
        try {
            article = getArticlesInRange(articleNumber, articleNumber);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't retrieve article. " + e);
        }
        return article.get(0);
    }

    public List<BillFragment> getArticlesInRange(String rangeStart, String rangeEnd){
        String rangeStartIdentifier = "Art. " + rangeStart + ".";
        String rangeEndIdentifier = "Art. " + rangeEnd + ".";

        Predicate<BillFragment> articlePredicate = (x) -> x.getIdentifier() != null && x.getIdentifier().contains("Art.");

        List<BillFragment> articles = getPartsInRange(billDocument.getBillFragment(), articlePredicate, rangeStartIdentifier, rangeEndIdentifier);
        return articles;
    }

    public BillFragment getParagraph(String articleNumber, String paragraphNumber){
        BillFragment article;
        try{
            article = getArticle(articleNumber);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't retrieve article. " + e);
        }

        String paragraphIdentifier = paragraphNumber + ".";
        BillFragment paragraph;
        try {
            paragraph = getPartWithIdentifier(article, paragraphIdentifier);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't retrieve paragraph: " + e);
        }
        return paragraph;
    }

    public List<BillFragment> getParagraphInRange(String articleNumber, String rangeStart, String rangeEnd){
        BillFragment article;
        try{
            article = getArticle(articleNumber);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't retrieve article. " + e);
        }

        Predicate<BillFragment> paragraphPredicate = (x) -> x.getIdentifier() != null && x.getIdentifier().contains(".");
        String rangeStartIdentifier = rangeStart + ".";
        String rangeEndIdentifier = rangeEnd + ".";

        List<BillFragment> paragraphs = getPartsInRange(article, paragraphPredicate, rangeStartIdentifier, rangeEndIdentifier);
        return paragraphs;
    }

    public BillFragment getPoint(String articleNumber, String paragraphNumber, String pointNumber){
        BillFragment paragraph = getParagraph(articleNumber, paragraphNumber);
        String pointIdentifier = pointNumber + ")";
        BillFragment point;
        try {
            point = getPartWithIdentifier(paragraph, pointIdentifier);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't retrieve point: " + e);
        }
        return point;
    }

    public BillFragment getPoint(String articleNumber, String pointNumber){
        BillFragment article = getArticle(articleNumber);
        String pointIdentifier = pointNumber + ")";
        BillFragment point;
        try {
            point = getPartWithIdentifier(article, pointIdentifier);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't retrieve point: " + e);
        }
        return point;
    }

    public List<BillFragment> getPointsInRange(String articleNumber, String paragraphNumber, String rangeStart, String rangeEnd){
        BillFragment paragraph = getParagraph(articleNumber, paragraphNumber);

        Predicate<BillFragment> pointsPredicate = (x) -> x.getIdentifier() != null && x.getIdentifier().matches("[0-9]+\\)");
        String rangeStartIdentifier = rangeStart + ")";
        String rangeEndIdentifier = rangeEnd + ")";
        List<BillFragment> points = getPartsInRange(paragraph, pointsPredicate, rangeStartIdentifier, rangeEndIdentifier);
        return points;
    }

    public List<BillFragment> getPointsInRange(String articleNumber, String rangeStart, String rangeEnd){
        BillFragment article = getArticle(articleNumber);

        Predicate<BillFragment> pointsPredicate = (x) -> x.getIdentifier() != null && x.getIdentifier().matches("[0-9]+\\)");
        String rangeStartIdentifier = rangeStart + ")";
        String rangeEndIdentifier = rangeEnd + ")";
        List<BillFragment> points = getPartsInRange(article, pointsPredicate, rangeStartIdentifier, rangeEndIdentifier);
        return points;
    }
    //</editor-fold>

    //<editor-fold desc="Fragment content retrievers">
    public String getArticleContent(String articleNumber){
        BillFragment article;
        try {
            article = getArticle(articleNumber);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't get content. " + e);
        }

        return article.getFragmentContentWithChildren();
    }

    public List<String> getArticlesInRangeContents(String rangeStart, String rangeEnd){
        List<BillFragment> articles;
        try {
            articles = getArticlesInRange(rangeStart, rangeEnd);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't get content. " + e);
        }

        return getPartsContents(articles);
    }

    public String getParagraphContent(String articleNumber, String paragraphNumber){
        BillFragment paragraph;
        try {
            paragraph = getParagraph(articleNumber, paragraphNumber);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't get content. " + e);
        }

        return paragraph.getFragmentContentWithChildren();
    }

    public List<String> getParagraphsInRangeContents(String articleNumber, String rangeStart, String rangeEnd){
        List<BillFragment> paragraphs;
        try {
            paragraphs = getParagraphInRange(articleNumber, rangeStart, rangeEnd);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't get content. " + e);
        }

        return getPartsContents(paragraphs);
    }

    public String getPointContent(String articleNumber, String paragraphNumber, String pointNumber){
        BillFragment point;
        try {
            point = getPoint(articleNumber, paragraphNumber, pointNumber);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't get content. " + e);
        }
        return point.getFragmentContentWithChildren();
    }

    public String getPointContent(String articleNumber, String pointNumber){
        BillFragment point;
        try {
            point = getPoint(articleNumber, pointNumber);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't get content. " + e);
        }
        return point.getFragmentContentWithChildren();
    }

    public List<String> getPointsInRangeContents(String articleNumber, String paragraph, String rangeStart, String rangeEnd){
        List<BillFragment> points;
        try {
            points = getPointsInRange(articleNumber, paragraph, rangeStart, rangeEnd);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't get content. " + e);
        }

        return getPartsContents(points);
    }

    public List<String> getPointsInRangeContents(String articleNumber, String rangeStart, String rangeEnd){
        List<BillFragment> points;
        try {
            points = getPointsInRange(articleNumber, rangeStart, rangeEnd);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't get content. " + e);
        }

        return getPartsContents(points);
    }
    //</editor-fold>

    public static DocumentType checkDocumentType(List<String> documentLines){
        for (String line : documentLines){
            if (line.matches("KONSTYTUCJA")){
                return DocumentType.Constitution;
            }
        }
        for (String line : documentLines){
            if (line.matches("USTAWA")){
                return DocumentType.Bill;
            }
        }
        return DocumentType.Unknown;
    }
}
