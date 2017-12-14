import java.util.ArrayList;
import java.util.List;

public class BillFragmentParser {
    private List<BillFragmentParserRule> billFragmentParserRules;

    public BillFragmentParser(){
        billFragmentParserRules = new ArrayList<>();
    }

    public BillFragment parseDocument(BillDocument billDocument){
        BillFragment billFragment = new BillFragment();
        return billFragment;
    }
}
