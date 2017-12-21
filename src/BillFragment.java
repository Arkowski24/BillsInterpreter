import java.util.List;

public class BillFragment {
    private String fragmentContent;
    private BillFragment parentBillFragment;
    private List<BillFragment> childrenBillFragments;

    public String getFragmentContent() {
        return fragmentContent;
    }

    public BillFragment getParentBillFragment() {
        return parentBillFragment;
    }

    public void setParentBillFragment(BillFragment parentBillFragment) {
        this.parentBillFragment = parentBillFragment;
    }

    public List<BillFragment> getChildrenBillFragments() {
        return childrenBillFragments;
    }

    public void setChildrenBillFragments(List<BillFragment> childrenBillFragments) {
        this.childrenBillFragments = childrenBillFragments;
    }

    public void setFragmentContent(String fragmentContent) {
        this.fragmentContent = fragmentContent;
    }
}
