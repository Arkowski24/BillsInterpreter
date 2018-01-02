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

public abstract class AbstractDocumentSystem {
    protected Cleaner cleaner;
    protected Parser parser;
    protected BillDocument billDocument;

    public AbstractDocumentSystem(){
        cleaner = new Cleaner();
        parser = new Parser();
    }

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

    public String getTableOfContents(){
        return getTableOfContentsForPart(billDocument.getBillFragment(), (x) -> false);
    }

    public String getTableOfContentsForPart(BillFragment parent, Predicate<BillFragment> terminalPredicate){
        if (parent == null){
            throw new IllegalStateException("Document hasn't been parsed, yet.");
        }

        return appendList(parent.getTableOfContentsWithEndingPredicate(2, terminalPredicate));
    }

    public String getTableOfContentsForPart(String parentIdentifier, Predicate<BillFragment> terminalPredicate){
        if (billDocument.getBillFragment() == null){
            throw new IllegalStateException("Document hasn't been parsed, yet.");
        }

        BillFragment parent;
        try {
            parent = billDocument.getBillFragment().findFirstFragmentWithIdentifier(parentIdentifier);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Couldn't find part with identifier: " + parentIdentifier);
        }

        return appendList(parent.getTableOfContentsWithEndingPredicate(2, terminalPredicate));
    }

    public BillFragment getPart(BillFragment parent, String identifier){
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
        List<BillFragment> fragmentsInScope = parent.findFragmentsSatisfyingPredicate(rangePredicate);

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

    protected List<String> getPartsContents (List<BillFragment> parts){
        List<String> contents = new ArrayList<>();
        for (BillFragment part : parts){
            contents.add(part.getFragmentContentWithChildren());
        }
        return contents;
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
}
