import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        ConstitutionDocumentSystem documentSystem;
        try {
            documentSystem = new ConstitutionDocumentSystem("konstytucja.txt");
        }
        catch (IOException e){
            System.out.println("Couldn't read document.");
            return;
        }
        System.out.print(documentSystem.getTableOfContents());
        System.out.print(documentSystem.getArticleContent(25));
        System.out.print(documentSystem.getChapterContent(1));
        System.out.print(documentSystem.getParagraphContent(25, 2));
    }
}
