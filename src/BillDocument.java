import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillDocument {
    private List<String> billDocumentLines;

    public BillDocument(){
        billDocumentLines = new ArrayList<>();
    }

    public BillDocument(List<String> billDocumentLines){
        this.billDocumentLines = billDocumentLines;
    }

    public List<String> getBillDocumentLines() {
        return billDocumentLines;
    }

    public void setBillDocumentLines(List<String> billDocumentLines) {
        this.billDocumentLines = billDocumentLines;
    }

}
