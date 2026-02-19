package io.sqlglot.parser;

import java.util.Objects;

/**
 * Configuration for the SQL parser.
 */
public record ParserConfig(
    ErrorLevel errorLevel,
    int maxErrors
) {
    /**
     * Compact constructor for validation.
     */
    public ParserConfig {
        Objects.requireNonNull(errorLevel, "errorLevel cannot be null");
        if (maxErrors < 1) throw new IllegalArgumentException("maxErrors must be >= 1");
    }

    /**
     * Creates a default config with RAISE error level and 100 max errors.
     */
    public static ParserConfig defaultConfig() {
        return new ParserConfig(ErrorLevel.RAISE, 100);
    }

    /**
     * Creates a permissive config that ignores errors.
     */
    public static ParserConfig permissive() {
        return new ParserConfig(ErrorLevel.IGNORE, Integer.MAX_VALUE);
    }

    /**
     * Creates a strict config that fails on first error.
     */
    public static ParserConfig strict() {
        return new ParserConfig(ErrorLevel.IMMEDIATE, 1);
    }
}
