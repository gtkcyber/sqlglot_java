package io.sqlglot;

import io.sqlglot.dialect.Dialect;
import io.sqlglot.dialect.DialectRegistry;
import io.sqlglot.expressions.Expression;
import io.sqlglot.generator.GeneratorConfig;

import java.util.List;
import java.util.Optional;

/**
 * Main API for SQLGlot - SQL parsing, generation, and transpilation.
 * Provides convenient static methods for common operations.
 */
public final class SqlGlot {
    private SqlGlot() { /* utility class */ }

    /**
     * Parses SQL with ANSI dialect.
     */
    public static List<Expression> parse(String sql) {
        return parse(sql, "ANSI");
    }

    /**
     * Parses SQL with specified dialect.
     */
    public static List<Expression> parse(String sql, String dialect) {
        return getDialect(dialect).parse(sql);
    }

    /**
     * Parses a single SQL statement with ANSI dialect.
     */
    public static Optional<Expression> parseOne(String sql) {
        return parseOne(sql, "ANSI");
    }

    /**
     * Parses a single SQL statement with specified dialect.
     */
    public static Optional<Expression> parseOne(String sql, String dialect) {
        return getDialect(dialect).parseOne(sql);
    }

    /**
     * Transpiles SQL from one dialect to another.
     */
    public static String transpile(String sql, String fromDialect, String toDialect) {
        Dialect to = getDialect(toDialect);
        Dialect from = getDialect(fromDialect);
        Optional<Expression> expr = from.parseOne(sql);
        return expr.map(to::generate).orElse("");
    }

    /**
     * Formats SQL using ANSI dialect with pretty printing.
     */
    public static String format(String sql) {
        return format(sql, "ANSI");
    }

    /**
     * Formats SQL using specified dialect with pretty printing.
     */
    public static String format(String sql, String dialect) {
        return getDialect(dialect).format(sql);
    }

    /**
     * Generates SQL from an expression using ANSI dialect.
     */
    public static String generate(Expression expr) {
        return generate(expr, "ANSI");
    }

    /**
     * Generates SQL from an expression using specified dialect.
     */
    public static String generate(Expression expr, String dialect) {
        return getDialect(dialect).generate(expr);
    }

    /**
     * Generates SQL from an expression with custom config.
     */
    public static String generate(Expression expr, GeneratorConfig config) {
        return generate(expr, "ANSI", config);
    }

    /**
     * Generates SQL from an expression using specified dialect and config.
     */
    public static String generate(Expression expr, String dialect, GeneratorConfig config) {
        return getDialect(dialect).generate(expr, config);
    }

    /**
     * Gets a dialect by name.
     */
    public static Dialect getDialect(String name) {
        return Dialect.of(name);
    }

    /**
     * Gets all registered dialect names.
     */
    public static List<String> getDialectNames() {
        return new java.util.ArrayList<>(DialectRegistry.getInstance().getDialectNames());
    }

    /**
     * Checks if a dialect is registered.
     */
    public static boolean hasDialect(String name) {
        return DialectRegistry.getInstance().contains(name);
    }

    /**
     * Registers a custom dialect.
     */
    public static void registerDialect(Dialect dialect) {
        DialectRegistry.getInstance().registerDialect(dialect);
    }
}
