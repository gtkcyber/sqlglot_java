package io.sqlglot.parser;

/**
 * Error handling level for parsing.
 */
public enum ErrorLevel {
    /**
     * Fail immediately on first error.
     */
    IMMEDIATE,

    /**
     * Collect errors and emit warnings but continue parsing.
     */
    WARN,

    /**
     * Collect errors and raise an exception at the end.
     */
    RAISE,

    /**
     * Silently ignore errors.
     */
    IGNORE
}
