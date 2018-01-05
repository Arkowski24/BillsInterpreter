package DocumentRepresentation;

import java.util.ArrayList;
import java.util.List;

public class BillDocument {
    private List<String> billDocumentLines;
    private BillFragment billFragment;

    public BillDocument() {
        billDocumentLines = new ArrayList<>();
    }

    public BillDocument(List<String> billDocumentLines) {
        this.billDocumentLines = billDocumentLines;
    }

    public BillFragment getBillFragment() {
        return billFragment;
    }

    public void setBillFragment(BillFragment billFragment) {
        this.billFragment = billFragment;
    }

    public List<String> getBillDocumentLines() {
        return billDocumentLines;
    }

    public void setBillDocumentLines(List<String> billDocumentLines) {
        this.billDocumentLines = billDocumentLines;
    }

}
