package io.sqlglot.dialect;

/**
 * Strategy for normalizing identifiers and keywords.
 */
public enum NormalizationStrategy {
    /**
     * Convert to lowercase.
     */
    LOWERCASE,

    /**
     * Convert to uppercase.
     */
    UPPERCASE,

    /**
     * Keep original case.
     */
    CASE_SENSITIVE;

    public String normalize(String text) {
        return switch (this) {
            case LOWERCASE -> text.toLowerCase();
            case UPPERCASE -> text.toUpperCase();
            case CASE_SENSITIVE -> text;
        };
    }
}
