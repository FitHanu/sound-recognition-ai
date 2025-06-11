package org.fit.sra.model;
import org.tensorflow.lite.support.label.Category;

public class CategoryWithSeverity {
    private final Category cat;
    private final String severity;

    public CategoryWithSeverity(Category cat, String severity) {
        this.cat = cat;
        this.severity = severity;
    }

    public Category getCat() {
        return cat;
    }

    // No setter for 'cat' since it's declared as final and cannot be changed after construction.

    public String getSeverity() {
        return severity;
    }

    @Override
    public String toString() {
        return "Label: " + cat.getLabel() + " (Index: " + cat.getIndex() + ", Score: " + cat.getScore() + ", Severity: " + severity + ")";
    }
}
