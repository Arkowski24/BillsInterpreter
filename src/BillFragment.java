import java.util.ArrayList;
import java.util.List;

public class BillFragment {
    private String identifier;
    private String content;
    private BillFragment parent;
    private List<BillFragment> children;


    public BillFragment() {
        this.children = new ArrayList<>();
    }

    public BillFragment(String identifier, String content) {
        this.identifier = identifier;
        this.content = content;
        this.parent = null;
        this.children = new ArrayList<>();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getContent() {
        return content;
    }

    public BillFragment getParent() {
        return parent;
    }

    public void setParent(BillFragment parent) {
        this.parent = parent;
    }

    public List<BillFragment> getChildren() {
        return children;
    }

    public void setChildren(List<BillFragment> children) {
        this.children = children;
    }

    public void addChild(BillFragment billFragment){
        children.add(billFragment);
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return identifier + content;
    }

    public String getTableOfContentsAsLine(int indentSize) {
        List<String> tableOfContents = getTableOfContents(indentSize);
        String newToC = "";
        for (String toc : tableOfContents) {
            newToC += toc;
            newToC += "\n";
        }
        return newToC;
    }

    public List<String> getTableOfContents(int indentSize){
        List<String> tableOfContents = new ArrayList<>();
        String indent = getSpacesForIndent(indentSize);
        tableOfContents.add(identifier);

        for (BillFragment child : children){
            List<String> childTableOfContents = child.getTableOfContents(indentSize);
            for (String childToC : childTableOfContents){
                tableOfContents.add(indent + childToC);
            }
        }
        return tableOfContents;
    }

    private String getSpacesForIndent(int indentSize){
        StringBuilder stringBuilder = new StringBuilder(indentSize);

        for (int i = 0; i < indentSize; i++){
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }
}
