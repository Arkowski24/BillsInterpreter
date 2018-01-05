package com.farald.DocumentSystem;

import com.farald.Cleaner.Cleaner;
import com.farald.DocumentRepresentation.BillDocument;
import com.farald.DocumentRepresentation.BillFragment;
import com.farald.Parser.Parser;
import com.frequal.romannumerals.Converter;
import com.martiansoftware.jsap.JSAPResult;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractDocumentSystem {
    protected Cleaner cleaner;
    protected Parser parser;
    protected BillDocument billDocument;

    public AbstractDocumentSystem() {
        cleaner = new Cleaner();
        parser = new Parser();
    }

    //<editor-fold desc="Read document methods">
    public static List<String> readFile(String filepath) throws IOException {
        List<String> documentLines = new ArrayList<>();

        try (BufferedReader documentReader = new BufferedReader(new FileReader(filepath))) {

            String line = documentReader.readLine();
            while (line != null) {
                documentLines.add(line);
                line = documentReader.readLine();
            }
        } catch (IOException e) {
            throw new IOException("Document could not be read.", e);
        }

        return documentLines;
    }

    public abstract void interpret(JSAPResult parsingResults);

    protected void readDocument(String filepath) throws IOException {
        List<String> documentLines;
        try {
            documentLines = readFile(filepath);
        } catch (IOException e) {
            throw new IOException(e);
        }

        this.billDocument = new BillDocument(documentLines);
    }

    protected void readDocument(List<String> fileLines) {
        this.billDocument = new BillDocument(fileLines);
    }

    protected String appendList(List<String> list) {
        if (list == null) {
            throw new IllegalArgumentException("Null list cannot be appended.");
        }
        StringBuilder builder = new StringBuilder("");
        for (String line : list) {
            if (line != null && line.trim().length() > 0) {
                builder.append(line).append("\n");
            }
        }
        return builder.toString();
    }
    //</editor-fold>

    //<editor-fold desc="Document parts retrieval methods">
    public BillFragment getPartWithSearchPattern(BillFragment parent, List<String> identifierSearchPattern) {
        if (identifierSearchPattern == null || identifierSearchPattern.size() == 0) {
            throw new IllegalArgumentException("Wrong search pattern");
        }
        BillFragment checked = parent;
        for (String identifier : identifierSearchPattern) {
            try {
                checked = getPartWithIdentifier(checked, identifier);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Wrong search pattern. " + e);
            }
        }
        return checked;
    }

    public List<BillFragment> getPartsWithSearchPatternInRange(BillFragment parent, List<String> identifierSearchPattern,
                                                               Predicate<BillFragment> rangePredicate, String fromIdentifier, String toIdentifier) {
        BillFragment checked;
        try {
            checked = getPartWithSearchPattern(parent, identifierSearchPattern);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Couldn't get fragment for range search.");
        }
        return getPartsInRange(checked, rangePredicate, fromIdentifier, toIdentifier);
    }

    public BillFragment getPartWithIdentifier(BillFragment parent, String identifier) {
        if (parent == null) {
            throw new IllegalArgumentException("Parent cannot be null.");
        }
        BillFragment part = parent.findFirstFragmentWithIdentifier(identifier);
        if (part == null) {
            throw new IllegalArgumentException("Couldn't find: " + identifier);
        }

        return part;
    }

    public List<BillFragment> getPartsInRange(BillFragment parent, Predicate<BillFragment> rangePredicate, String fromIdentifier, String toIdentifier) {
        if (parent == null) {
            throw new IllegalArgumentException("Parent cannot be null.");
        }
        List<BillFragment> fragmentsInScope = parent.findAllFragmentsSatisfyingPredicate(rangePredicate);

        if (fragmentsInScope.size() == 0) {
            throw new IllegalArgumentException("Couldn't find elements with given predicate.");
        }

        int startPosition = getStartPosition(fragmentsInScope, fromIdentifier);
        int endPosition = getEndPosition(fragmentsInScope, toIdentifier);

        if (startPosition == -1) {
            throw new IllegalArgumentException("Couldn't find start of range for: " + fromIdentifier);
        }
        if (endPosition == -1) {
            throw new IllegalArgumentException("Couldn't find end of range for: " + toIdentifier);
        }

        return fragmentsInScope.subList(startPosition, endPosition + 1);
    }

    private int getStartPosition(List<BillFragment> scope, String startIdentifier) {
        int startPosition = -1;
        for (int i = 0; i < scope.size(); i++) {
            if (scope.get(i).getIdentifier().equals(startIdentifier)) {
                startPosition = i;
                break;

            }
        }
        return startPosition;
    }

    private int getEndPosition(List<BillFragment> scope, String endIdentifier) {
        int endPosition = -1;
        for (int i = scope.size() - 1; i >= 0; i--) {
            if (scope.get(i).getIdentifier().equals(endIdentifier)) {
                endPosition = i;
                break;

            }
        }
        return endPosition;
    }
    //</editor-fold>

    //<editor-fold desc="Table of contents methods">
    public String getTableOfContents() {
        return getTableOfContentsForPart(billDocument.getBillFragment(), (x) -> false);
    }

    public String getTableOfContentsForPart(BillFragment parent, Predicate<BillFragment> terminalPredicate) {
        if (parent == null) {
            throw new IllegalStateException("Document hasn't been parsed, yet.");
        }

        return appendList(parent.getTableOfContentsWithEndingPredicate(2, terminalPredicate));
    }
    //</editor-fold>

    protected List<String> getPartsContents(List<BillFragment> parts) {
        List<String> contents = new ArrayList<>();
        for (BillFragment part : parts) {
            contents.add(part.getFragmentContentWithChildren());
        }
        return contents;
    }

    protected String getRomanNumber(String number) {
        if (isRomanNumber(number)) {
            return formRomanNumber(number);
        } else {
            return getRomanFromArabNumber(number);
        }
    }

    private String getRomanFromArabNumber(String number) {
        Converter romanConverter = new Converter();
        String digitPart = number.replaceAll("\\D+", "");
        String lettersPart = number.replaceAll("\\d+", "");
        String romanPart;
        try {
            int arabNumber = Integer.parseInt(digitPart);
            romanPart = romanConverter.toRomanNumerals(arabNumber);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Couldn't create Roman Number.");
        }
        return romanPart + lettersPart;
    }

    protected boolean isRomanNumber(String number) {
        Converter romanConverter = new Converter();
        String digitPart = number.replaceAll("[^MDCLXVI]+", "");
        if (digitPart.length() == 0) {
            return false;
        }
        try {
            romanConverter.toNumber(digitPart);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    private String formRomanNumber(String number) {
        String letterPart = number.replaceAll("[MDCLXVI]+", "");
        String digitPart = number.replaceAll("[^MDCLXVI]+", "");

        return letterPart + digitPart;
    }
}
