import DocumentRepresentation.BillDocument;
import DocumentSystem.AbstractDocumentSystem;
import DocumentSystem.ConstitutionDocumentSystem;
import DocumentSystem.ConsumersBillDocumentSystem;
import DocumentSystem.PolishDocumentSystem;
import com.martiansoftware.jsap.*;

import java.io.IOException;
import java.util.List;
import java.util.jar.JarException;

public class Main {

    public static void main(String[] args) {
        JSAP jsap = new JSAP();
        JSAPResult results;
        try{
            fillOptionsForJsapParser(jsap);
        }
        catch (JSAPException e){
            System.err.println("Couldn't add options to parser.");
            return;
        }
        try {
            results = jsap.parse(args);
        }
        catch (Exception e){
            System.err.println("Couldn't parse given options.");
            System.err.println(jsap.getUsage());
            return;
        }
        String filePath = results.getString("filepath");
        if (filePath == null){
            System.err.println("No file path specified.");
            return;
        }
        List<String> fileLines;
        try {
            fileLines = AbstractDocumentSystem.readFile(filePath);
        }
        catch (IOException e){
            System.err.println("Couldn't read file.");
            return;
        }
        switch (PolishDocumentSystem.checkDocumentType(fileLines)){
            case Constitution:
                interpretConstitution(fileLines, results);
                break;
            case Bill:
                interpretBill(fileLines, results);
                break;

        }
    }

    private static void fillOptionsForJsapParser(JSAP parser) throws JSAPException{
        Switch helpOption = new Switch("showHelp")
                .setShortFlag('h')
                .setLongFlag("help");

        UnflaggedOption fileOption = new UnflaggedOption("filepath")
                .setStringParser(JSAP.STRING_PARSER)
                .setRequired(true);

        Switch modeOption = new Switch("showTableOfContents")
                                .setShortFlag('T')
                                .setLongFlag("table-of-contents");

        FlaggedOption articleOption = new FlaggedOption("article")
                .setStringParser(JSAP.STRING_PARSER)
                .setShortFlag('a')
                .setLongFlag("article");

        FlaggedOption articlesOption = new FlaggedOption("articles")
                .setStringParser(JSAP.STRING_PARSER)
                .setShortFlag('A')
                .setLongFlag("article-range")
                .setList(true)
                .setListSeparator(',');

        FlaggedOption chapterOption  = new FlaggedOption("chapter")
                .setStringParser(JSAP.STRING_PARSER)
                .setShortFlag('c')
                .setLongFlag("chapter");

        FlaggedOption sectionOption  = new FlaggedOption("section")
                .setStringParser(JSAP.STRING_PARSER)
                .setShortFlag('s')
                .setLongFlag("section");

        UnflaggedOption showSpecific = new UnflaggedOption("articleSpecifics")
                                            .setGreedy(true);

        try {
            parser.registerParameter(fileOption);
            parser.registerParameter(modeOption);
            parser.registerParameter(articleOption);
            parser.registerParameter(articlesOption);
            parser.registerParameter(chapterOption);
            parser.registerParameter(sectionOption);
            parser.registerParameter(showSpecific);
        }
        catch (JSAPException e){
            throw new JSAPException("Couldn't add options.");
        }
    }

    private static void interpretConstitution(List<String> fileLines, JSAPResult parsingResults){
        ConstitutionDocumentSystem constitutionDocumentSystem = new ConstitutionDocumentSystem(fileLines);
        constitutionDocumentSystem.interpret(parsingResults);
    }

    private static void interpretBill(List<String> fileLines, JSAPResult parsingResults){
        ConsumersBillDocumentSystem consumersBillDocumentSystem = new ConsumersBillDocumentSystem(fileLines);
        consumersBillDocumentSystem.interpret(parsingResults);
    }
}
