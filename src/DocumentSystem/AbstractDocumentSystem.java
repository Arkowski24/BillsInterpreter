package DocumentSystem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
        if (billDocument.getBillFragment() == null){
            throw new IllegalStateException("Bill hasn't been parsed, yet.");
        }
        return appendList(billDocument.getBillFragment().getTableOfContents(2));
    }

    public BillFragment getPart(BillFragment parent, int partNumber, Function< Integer, String> identifierCreation){
        if (partNumber <= 0){
            throw new IllegalArgumentException("Document part number must be positive.");
        }
        String identifier = identifierCreation.apply(partNumber);

        BillFragment part = parent.findFirstFragmentWithIdentifier(identifier);
        if (part == null){
            throw new IllegalArgumentException("Couldn't find: " + identifier);
        }

        return part;
    }

    public List<BillFragment> getPartsInRange(BillFragment parent, int partsNumberStart, int partsNumberEnd, Function< Integer, String> identifierCreation){
        if (partsNumberStart <= 0 ||  partsNumberEnd <= 0 || partsNumberStart > partsNumberEnd){
            throw new IllegalArgumentException("Invalid document parts range.");
        }

        List<BillFragment> parts = new ArrayList<>();
        for (int i = partsNumberStart; i <= partsNumberEnd; i++){
            try{
                BillFragment part = getPart(parent, i, identifierCreation);
                parts.add(part);
            }
            catch (IllegalArgumentException e){
                throw new IllegalArgumentException("Invalid range. " + e);
            }
        }
        return parts;
    }

}
