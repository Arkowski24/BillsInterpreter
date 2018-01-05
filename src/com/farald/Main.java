package com.farald;

import com.farald.DocumentSystem.AbstractDocumentSystem;
import com.farald.DocumentSystem.ConstitutionDocumentSystem;
import com.farald.DocumentSystem.ConsumersBillDocumentSystem;
import com.farald.DocumentSystem.PolishDocumentSystem;
import com.martiansoftware.jsap.*;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        JSAP jsap = new JSAP();
        JSAPResult results;
        try {
            fillOptionsForJsapParser(jsap);
        } catch (JSAPException e) {
            System.err.println("Couldn't add options to parser.");
            return;
        }
        try {
            results = jsap.parse(args);
        } catch (Exception e) {
            System.err.println("Couldn't parse given options.");
            System.err.println();
            System.err.println(jsap.getUsage());
            System.err.println();
            System.err.println(jsap.getHelp());
            return;
        }
        if (results.getBoolean("showHelp")) {
            System.out.println();
            System.out.println(jsap.getUsage());
            System.out.println();
            System.out.println(jsap.getHelp());
            return;
        }
        String filePath = results.getString("filepath");
        if (filePath == null) {
            System.err.println("No file path specified.");
            return;
        }
        List<String> fileLines;
        try {
            fileLines = AbstractDocumentSystem.readFile(filePath);
        } catch (IOException e) {
            System.err.println("Couldn't read file.");
            return;
        }
        switch (PolishDocumentSystem.checkDocumentType(fileLines)) {
            case Constitution:
                interpretConstitution(fileLines, results);
                break;
            case Bill:
                interpretBill(fileLines, results);
                break;
            case Unknown:
                System.err.println("Couldn't detect document type.");
                break;
        }
    }

    private static void fillOptionsForJsapParser(JSAP parser) throws JSAPException {
        Switch helpOption = new Switch("showHelp")
                .setShortFlag('h')
                .setLongFlag("help");

        helpOption.setHelp("Show help.");

        UnflaggedOption fileOption = new UnflaggedOption("filepath")
                .setStringParser(JSAP.STRING_PARSER)
                .setRequired(true);

        fileOption.setHelp("Open file specified by given filepath. The type of document (Constitution|Bill) is detected automatically.");

        Switch modeOption = new Switch("showTableOfContents")
                .setShortFlag('T')
                .setLongFlag("table-of-contents");

        modeOption.setHelp("Specify whether the program should display table of content instead of bill's content. If section is specified it's table of contents will be displayed instead.");

        FlaggedOption articleOption = new FlaggedOption("articleNumber")
                .setStringParser(JSAP.STRING_PARSER)
                .setShortFlag('a')
                .setLongFlag("article");

        articleOption.setHelp("Show article with specified number.");

        FlaggedOption articlesOption = new FlaggedOption("articles")
                .setStringParser(JSAP.STRING_PARSER)
                .setShortFlag('A')
                .setLongFlag("article-range")
                .setList(true)
                .setListSeparator(',');

        articlesOption.setHelp("Show articles within specified ranges. Two consecutive numbers create range. If the number of articles is odd, the last one is ignored.");

        FlaggedOption chapterOption = new FlaggedOption("chapter")
                .setStringParser(JSAP.STRING_PARSER)
                .setShortFlag('c')
                .setLongFlag("chapter");

        chapterOption.setHelp("Show chapter with specified number. For Bill type document section number is also required.");

        FlaggedOption sectionOption = new FlaggedOption("section")
                .setStringParser(JSAP.STRING_PARSER)
                .setShortFlag('s')
                .setLongFlag("section");

        sectionOption.setHelp("Show section with specified number.");

        UnflaggedOption showSpecific = new UnflaggedOption("articleSpecifics")
                .setGreedy(true);

        showSpecific.setHelp("Show specified part of article. Only the first valid rule for each element (article, paragraph, etc.) is considered.");

        try {
            parser.registerParameter(helpOption);
            parser.registerParameter(fileOption);
            parser.registerParameter(modeOption);
            parser.registerParameter(articleOption);
            parser.registerParameter(articlesOption);
            parser.registerParameter(chapterOption);
            parser.registerParameter(sectionOption);
            parser.registerParameter(showSpecific);
        } catch (JSAPException e) {
            throw new JSAPException("Couldn't add options.");
        }
    }

    private static void interpretConstitution(List<String> fileLines, JSAPResult parsingResults) {
        ConstitutionDocumentSystem constitutionDocumentSystem = new ConstitutionDocumentSystem(fileLines);
        constitutionDocumentSystem.interpret(parsingResults);
    }

    private static void interpretBill(List<String> fileLines, JSAPResult parsingResults) {
        ConsumersBillDocumentSystem consumersBillDocumentSystem = new ConsumersBillDocumentSystem(fileLines);
        consumersBillDocumentSystem.interpret(parsingResults);
    }
}
