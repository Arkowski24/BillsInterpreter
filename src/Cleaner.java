import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cleaner {
    private List<CleanerRule> cleanerRules;

    public Cleaner() {
        cleanerRules = new ArrayList<>();
    }

    public void addNewCleanRule(CleanerRule cleanerRule) {
        cleanerRules.add(cleanerRule);
    }




}