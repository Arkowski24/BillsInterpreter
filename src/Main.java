import DocumentSystem.ConstitutionDocumentSystem;
import DocumentSystem.ConsumersBillDocumentSystem;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        ConstitutionDocumentSystem documentSystem;
        ConsumersBillDocumentSystem documentSystem1;
        try {
            documentSystem = new ConstitutionDocumentSystem("konstytucja.txt");
            documentSystem1 = new ConsumersBillDocumentSystem("uokik.txt");
        }
        catch (IOException e){
            System.out.println("Couldn't read document.");
            return;
        }
        //System.out.print(documentSystem.getTableOfContents());
        //System.out.print(documentSystem.getArticle("5"));
        //System.out.print(documentSystem.getChapterContent(1));
        //System.out.print(documentSystem.getParagraphContent(25, 2));
        //System.out.print(documentSystem1.getTableOfContents());
        List<String> articles = documentSystem.getArticlesInRangeContents("145", "151");
        for (String article : articles){
            System.out.print(article);
        }
        List<String> paragraphs = documentSystem.getParagraphInRangeContents("149","2", "3");
        for (String paragraph : paragraphs){
            System.out.print(paragraph);
        }
    }
}
