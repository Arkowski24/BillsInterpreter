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
}
