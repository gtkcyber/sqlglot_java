package io.sqlglot.parser;

import java.util.Objects;

/**
 * Represents a single parse error with location information.
 */
public record ParseError(
    String message,
    int line,
    int col,
    String sql
) {
    /**
     * Compact constructor for validation.
     */
    public ParseError {
        Objects.requireNonNull(message, "message cannot be null");
        Objects.requireNonNull(sql, "sql cannot be null");
        if (line < 1) throw new IllegalArgumentException("line must be >= 1");
        if (col < 1) throw new IllegalArgumentException("col must be >= 1");
    }

    /**
     * Returns a formatted error message with location.
     */
    @Override
    public String toString() {
        return String.format("Parse error at line %d, col %d: %s\n%s", line, col, message, sql);
    }
}
