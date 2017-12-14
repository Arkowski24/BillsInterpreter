import java.util.List;

public class BillFragment {
    private String fragmentTitle;
    private String fragmentContent;

    private BillFragment superBillFragment;
    private List<BillFragment> subBillFragments;

    public String getFragmentTitle() {
        return fragmentTitle;
    }

    public void setFragmentTitle(String fragmentTitle) {
        this.fragmentTitle = fragmentTitle;
    }

    public String getFragmentContent() {
        return fragmentContent;
    }

    public void setFragmentContent(String fragmentContent) {
        this.fragmentContent = fragmentContent;
    }

    public BillFragment getSuperBillFragment() {
        return superBillFragment;
    }

    public void setSuperBillFragment(BillFragment superBillFragment) {
        this.superBillFragment = superBillFragment;
    }

    public List<BillFragment> getSubBillFragments() {
        return subBillFragments;
    }

    public void setSubBillFragments(List<BillFragment> subBillFragments) {
        this.subBillFragments = subBillFragments;
    }
}
