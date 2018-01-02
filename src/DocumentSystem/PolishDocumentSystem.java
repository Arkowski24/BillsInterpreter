package DocumentSystem;

import DocumentRepresentation.BillFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PolishDocumentSystem extends AbstractDocumentSystem {
    public BillFragment getArticle(int articleNumber){
        List<BillFragment> article;
        try {
            article = getArticlesInRange(articleNumber, articleNumber);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't retrieve article. " + e);
        }
        return article.get(0);
    }

    public List<BillFragment> getArticlesInRange(int rangeStart, int rangeEnd){
        Function articleCreator = (x -> "Art. " + x + ".");
        List<BillFragment> articles = getPartsInRange(billDocument.getBillFragment(), rangeStart, rangeEnd, articleCreator);
        return articles;
    }

    public BillFragment getParagraph(int articleNumber, int paragraphNumber){
        BillFragment article;
        try{
            article = getArticle(articleNumber);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't retrieve article. " + e);
        }

        Function paragraphCreator = (x -> x + ".");
        BillFragment paragraph;
        try {
            paragraph = getPart(article, paragraphNumber, paragraphCreator);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't retrieve paragraph. " + e);
        }
        return paragraph;
    }

    public List<BillFragment> getParagraphInRange(int articleNumber, int rangeStart, int rangeEnd){
        BillFragment article;
        try{
            article = getArticle(articleNumber);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't retrieve article. " + e);
        }

        Function paragraphCreator = (x -> x + ".");
        return getPartsInRange(article, rangeStart, rangeEnd, paragraphCreator);
    }
    //</editor-fold>

    //<editor-fold desc="Fragment content retrievers">
    public String getArticleContent(int articleNumber){
        BillFragment article;
        try {
            article = getArticle(articleNumber);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't get content. " + e);
        }

        return article.getFragmentContentWithChildren();
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
    //</editor-fold>
}
