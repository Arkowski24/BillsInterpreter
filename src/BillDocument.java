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

    public void connectBrokenWords(){
        Pattern brokenLinePattern = Pattern.compile(".+-$");

        for (Integer i = 0; i < billDocumentLines.size(); i++){
            Matcher matcher = brokenLinePattern.matcher(billDocumentLines.get(i));
            if(matcher.matches()){
                connectBrokenWord(i);
            }
        }

        for (Integer i = 0; i < billDocumentLines.size(); i++) {
            if(billDocumentLines.get(i) == null)
                billDocumentLines.remove(i);
        }
    }

    private void connectBrokenWord(int lineOfWordNumber){
        if (lineOfWordNumber == billDocumentLines.size() - 1){
            return;
        }

        String firstHalfLine = billDocumentLines.get(lineOfWordNumber);
        String secondHalfLine = billDocumentLines.get(lineOfWordNumber + 1);

        int firstHalfPositionStart = firstHalfLine.lastIndexOf(" ") + 1;
        int firstHalfPositionFinish = firstHalfLine.length() - 2;

        String newFirstLine;
        String newSecondLine;

        if(firstHalfPositionStart == 0){
            newFirstLine = null;
        }
        else {
            newFirstLine = firstHalfLine.substring(0, firstHalfPositionStart - 1);
        }
        newSecondLine = firstHalfLine.substring(firstHalfPositionStart, firstHalfPositionFinish) + secondHalfLine;

        billDocumentLines.set(lineOfWordNumber, newFirstLine);
        billDocumentLines.set(lineOfWordNumber + 1, newSecondLine);
    }
}
