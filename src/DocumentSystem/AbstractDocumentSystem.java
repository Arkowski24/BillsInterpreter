package DocumentSystem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import DocumentRepresentation.*;
import Parser.*;
import Cleaner.*;
import com.frequal.romannumerals.Converter;

public abstract class AbstractDocumentSystem {
    protected Cleaner cleaner;
    protected Parser parser;
    protected BillDocument billDocument;

    public AbstractDocumentSystem(){
        cleaner = new Cleaner();
        parser = new Parser();
    }

    //<editor-fold desc="Read document methods">
    protected List<String> readFile(String filepath) throws IOException {
        List<String> documentLines = new ArrayList<>();

        try (BufferedReader documentReader = new BufferedReader(new FileReader (filepath))){

            String line = documentReader.readLine();
            while (line != null) {
                documentLines.add(line);
                line = documentReader.readLine();
            }
        }
        catch (IOException e){
            throw new IOException("Document could not be read.", e);
        }

        return documentLines;
    }

    protected void readDocument(String filepath) throws IOException {
        List<String> documentLines;
        try {
            documentLines = readFile(filepath);
        }
        catch (IOException e){
            throw new IOException(e);
        }
        BillDocument billDocument = new BillDocument(documentLines);

        this.billDocument = billDocument;
    }

    protected String appendList(List<String> list){
        if (list == null){
            throw new IllegalArgumentException("Null list cannot be appended.");
        }
        String newString = "";
        for (String line : list){
            if (line != null && line.trim().length() > 0){
                newString += line + "\n";
            }
        }
        return newString;
    }
    //</editor-fold>

    //<editor-fold desc="Document parts retrieval methods">
    public BillFragment getPartWithSearchPattern(BillFragment parent, List<String> identifierSearchPattern){
        if (identifierSearchPattern == null || identifierSearchPattern.size() == 0){
            throw new IllegalArgumentException("Wrong search pattern");
        }
        BillFragment checked = parent;
        for (String identifier : identifierSearchPattern){
            try {
                checked = getPartWithIdentifier(checked, identifier);
            }
            catch (IllegalArgumentException e){
                throw new IllegalArgumentException("Wrong search pattern. " + e);
            }
        }
        return checked;
    }

    public List<BillFragment> getPartsWithSearchPatternInRange(BillFragment parent, List<String> identifierSearchPattern,
                                                         Predicate<BillFragment> rangePredicate, String fromIdentifier, String toIdentifier){
        BillFragment checked;
        try {
            checked = getPartWithSearchPattern(parent, identifierSearchPattern);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't get fragment for range search.");
        }
        return getPartsInRange(checked, rangePredicate, fromIdentifier, toIdentifier);
    }

    public BillFragment getPartWithIdentifier(BillFragment parent, String identifier){
        if (parent == null){
            throw new IllegalArgumentException("Parent cannot be null.");
        }
        BillFragment part = parent.findFirstFragmentWithIdentifier(identifier);
        if (part == null){
            throw new IllegalArgumentException("Couldn't find: " + identifier);
        }

        return part;
    }

    public List<BillFragment> getPartsInRange(BillFragment parent, Predicate<BillFragment> rangePredicate, String fromIdentifier, String toIdentifier){
        if (parent == null){
            throw new IllegalArgumentException("Parent cannot be null.");
        }
        List<BillFragment> fragmentsInScope = parent.findAllFragmentsSatisfyingPredicate(rangePredicate);

        if (fragmentsInScope.size() == 0) {
            throw new IllegalArgumentException("Couldn't find elements with given predicate.");
        }

        int startPosition = getStartPosition(fragmentsInScope, fromIdentifier);
        int endPosition = getEndPosition(fragmentsInScope, toIdentifier);

        if (startPosition == -1){
            throw new IllegalArgumentException("Couldn't find start of range for: " + fromIdentifier);
        }
        if (endPosition == -1){
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

    private int getEndPosition(List<BillFragment> scope, String endIdentifier){
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
    public String getTableOfContents(){
        return getTableOfContentsForPart(billDocument.getBillFragment(), (x) -> false);
    }

    public String getTableOfContentsForPart(BillFragment parent, Predicate<BillFragment> terminalPredicate){
        if (parent == null){
            throw new IllegalStateException("Document hasn't been parsed, yet.");
        }

        return appendList(parent.getTableOfContentsWithEndingPredicate(2, terminalPredicate));
    }
    //</editor-fold>

    protected List<String> getPartsContents (List<BillFragment> parts){
        List<String> contents = new ArrayList<>();
        for (BillFragment part : parts){
            contents.add(part.getFragmentContentWithChildren());
        }
        return contents;
    }

    protected String getRomanNumber(String number){
        Converter romanConverter = new Converter();
        String digitPart = number.replaceAll("\\D+", "");
        String lettersPart = number.replaceAll("\\d+", "");

        int numberFromDigits = Integer.parseInt(digitPart);
        return romanConverter.toRomanNumerals(numberFromDigits) + lettersPart;
    }
}