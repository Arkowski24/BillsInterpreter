package DocumentRepresentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

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

    public void setContent(String content) {
        this.content = content;
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

    public void addChild(BillFragment billFragment) {
        children.add(billFragment);
    }
    //</editor-fold>

    @Override
    public String toString() {
        return identifier + content;
    }

    //<editor-fold desc="Table Of Contents">
    public List<String> getTableOfContentsWithEndingPredicate(int indentSize, Predicate<BillFragment> endSubtreePredicate) {
        return getTableOfContentsWithEndingPredicateAndContentPredicate(indentSize, endSubtreePredicate, (x) -> true);
    }

    public List<String> getTableOfContentsWithEndingPredicateAndContentPredicate(int indentSize, Predicate<BillFragment> traverseSubtree, Predicate<String> subContentPredicate) {
        List<String> tableOfContents = new ArrayList<>();
        if (!traverseSubtree.test(this)) {
            return tableOfContents;
        }

        String indent = getSpacesForIndent(indentSize);
        if (identifier != null) {
            tableOfContents.addAll(Arrays.asList(identifier.split("\n")));
        }
        if (subContentPredicate.test(content)) {
            tableOfContents.add(content);
        }

        for (BillFragment child : children) {
            List<String> childTableOfContents = child.getTableOfContentsWithEndingPredicateAndContentPredicate(indentSize, traverseSubtree, subContentPredicate);
            for (String childToC : childTableOfContents) {
                tableOfContents.add(indent + childToC);
            }
        }
        return tableOfContents;
    }

    public List<String> getTableOfContents(int indentSize) {
        return getTableOfContentsWithEndingPredicate(indentSize, (x) -> false);
    }

    private String getSpacesForIndent(int indentSize) {
        StringBuilder stringBuilder = new StringBuilder(indentSize);

        for (int i = 0; i < indentSize; i++) {
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }
    //</editor-fold>

    //<editor-fold desc="Tree Operations">
    public BillFragment findFirstFragmentSatisfyingPredicate(Predicate<BillFragment> predicate) {
        List<BillFragment> fragments = findAllFragmentsSatisfyingPredicate(predicate);
        if (fragments.size() == 0) {
            throw new IllegalArgumentException("Couldn't find fragment satisfying predicate.");
        }
        return fragments.get(0);
    }

    public List<BillFragment> findAllFragmentsSatisfyingPredicate(Predicate<BillFragment> predicate) {
        List<BillFragment> fragments = new ArrayList<>();
        if (predicate.test(this)) {
            fragments.add(this);
        }
        if (children != null) {
            for (BillFragment child : children) {
                List<BillFragment> childList = child.findAllFragmentsSatisfyingPredicate(predicate);
                fragments.addAll(childList);
            }
        }
        return fragments;
    }

    public List<BillFragment> findAllFragmentsWithIdentifier(String identifier) {
        if (identifier == null) {
            throw new IllegalArgumentException("Identifier cannot be null.");
        }
        Predicate<BillFragment> identifierChecker = (BillFragment x) -> x.identifier != null && x.identifier.equals(identifier);
        return findAllFragmentsSatisfyingPredicate(identifierChecker);
    }

    public BillFragment findFirstFragmentWithIdentifier(String identifier) {
        List<BillFragment> fragments;
        try {
            fragments = findAllFragmentsWithIdentifier(identifier);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        }
        if (fragments.size() == 0) {
            return null;
        } else {
            return fragments.get(0);
        }
    }

    public String getFragmentContentWithChildren() {
        StringBuilder contents = new StringBuilder("");
        if (identifier != null) {
            contents.append(this.identifier);
            contents.append(" ");
        }
        if (content != null) {
            contents.append(this.content);
            contents.append("\n");
        }

        for (BillFragment child : this.children) {
            String childContents = child.getFragmentContentWithChildren();
            contents.append(childContents);
        }

        return contents.toString();
    }
    //</editor-fold>
}
