package DocumentSystem;

import DocumentRepresentation.BillFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class PolishDocumentSystem extends AbstractDocumentSystem {

    @Override
    public String getTableOfContents(){
        BillFragment fragment = billDocument.getBillFragment();
        if (fragment == null){
            throw new IllegalStateException("Document hasn't been parsed, yet.");
        }

        Predicate<BillFragment> terminalPredicate = (BillFragment x) -> (x.getIdentifier() != null && x.getIdentifier().contains("Art. "));
        return appendList(fragment.getTableOfContentsWithEndingPredicate(2, terminalPredicate));
    }

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
            paragraph = getPart(article, paragraphIdentifier);
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

    public List<String> getParagraphInRangeContents(String articleNumber, String rangeStart, String rangeEnd){
        List<BillFragment> paragraphs;
        try {
            paragraphs = getParagraphInRange(articleNumber, rangeStart, rangeEnd);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't get content. " + e);
        }

        return getPartsContents(paragraphs);
    }

    //</editor-fold>
}
