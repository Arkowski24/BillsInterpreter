package DocumentRepresentation;

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

    //<editor-fold desc="Getters and Setters">
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
    //</editor-fold>

    @Override
    public String toString() {
        return identifier + content;
    }

    //<editor-fold desc="Table Of Contents">
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
    //</editor-fold>

    //<editor-fold desc="Tree Operations">
    public BillFragment findFirstFragmentWithIdentifier(String identifier){
        if (identifier == null){
            throw new IllegalArgumentException("Identifier cannot be null.");
        }

        if (this.identifier != null && this.identifier.equals(identifier)){
            return this;
        }
        else if (children == null || children.size() == 0){
            return null;
        }
        else {
            for (BillFragment child : children){
                BillFragment foundIdentifier = child.findFirstFragmentWithIdentifier(identifier);
                if (foundIdentifier != null){
                    return foundIdentifier;
                }
            }
            return null;
        }
    }

    public List<BillFragment> findAllFragmentsWithIdentifier(String identifier){
        if (identifier == null){
            throw new IllegalArgumentException("Identifier cannot be null.");
        }
        List<BillFragment> billFragments = new ArrayList<>();

        if (this.identifier != null && this.identifier.equals(identifier)){
            billFragments.add(this);
        }
        else if (children != null) {
            for (BillFragment child : children){
                List<BillFragment> childBillFragments = child.findAllFragmentsWithIdentifier(identifier);
                billFragments.addAll(childBillFragments);
            }
        }
        return billFragments;
    }

    public String getFragmentContentWithChildren(){
        String contents = "";
        contents += this.identifier;
        contents += " ";
        contents += this.content;
        contents += "\n";

        for (BillFragment child : this.children){
            String childContents = child.getFragmentContentWithChildren();
            contents += childContents;
        }

        return contents;
    }
    //</editor-fold>
}
