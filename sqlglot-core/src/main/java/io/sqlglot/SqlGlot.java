/*
 * MIT License
 *
 * Copyright (c) 2026 GTK Cyber
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.sqlglot;

import io.sqlglot.dialect.Dialect;
import io.sqlglot.dialect.DialectRegistry;
import io.sqlglot.expressions.Expression;
import io.sqlglot.generator.GeneratorConfig;
import io.sqlglot.optimizer.Optimizer;
import io.sqlglot.optimizer.OptimizerConfig;
import io.sqlglot.optimizer.OptimizerContext;
import io.sqlglot.optimizer.rules.SimplifyRule;
import io.sqlglot.optimizer.rules.CanonicalizeRule;
import io.sqlglot.optimizer.rules.QuoteIdentifiersRule;
import io.sqlglot.optimizer.rules.EliminateCTEsRule;
import io.sqlglot.optimizer.rules.PushdownPredicatesRule;
import io.sqlglot.optimizer.rules.NormalizePredicatesRule;
import io.sqlglot.optimizer.rules.MergeSubqueriesRule;
import io.sqlglot.optimizer.rules.JoinReorderingRule;
import io.sqlglot.optimizer.rules.ProjectionPushdownRule;
import io.sqlglot.optimizer.rules.AnnotateTypesRule;
import io.sqlglot.optimizer.rules.QualifyColumnsRule;

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

    /**
     * Optimizes an expression using ANSI dialect with default configuration.
     */
    public static Expression optimize(Expression expr) {
        return optimize(expr, "ANSI", OptimizerConfig.DEFAULT);
    }

    /**
     * Optimizes an expression using specified dialect with default configuration.
     */
    public static Expression optimize(Expression expr, String dialect) {
        return optimize(expr, dialect, OptimizerConfig.DEFAULT);
    }

    /**
     * Optimizes an expression using specified dialect and custom configuration.
     */
    public static Expression optimize(Expression expr, String dialect, OptimizerConfig config) {
        Dialect d = getDialect(dialect);
        OptimizerContext context = OptimizerContext.of(d, config);
        Optimizer optimizer = new Optimizer(context);

        // Phase 5A rules
        if (config.simplify()) {
            optimizer.addRule(new SimplifyRule());
        }
        if (config.canonicalize()) {
            optimizer.addRule(new CanonicalizeRule());
        }
        if (config.quoteIdentifiers()) {
            optimizer.addRule(new QuoteIdentifiersRule());
        }
        if (config.eliminateCtes()) {
            optimizer.addRule(new EliminateCTEsRule());
        }

        // Phase 5B rules (advanced optimizations)
        if (config.normalizePredicates()) {
            optimizer.addRule(new NormalizePredicatesRule());
        }
        if (config.pushdownPredicates()) {
            optimizer.addRule(new PushdownPredicatesRule());
        }
        if (config.mergeSubqueries()) {
            optimizer.addRule(new MergeSubqueriesRule());
        }
        if (config.joinReordering()) {
            optimizer.addRule(new JoinReorderingRule());
        }
        if (config.projectionPushdown()) {
            optimizer.addRule(new ProjectionPushdownRule());
        }
        if (config.annotateTypes()) {
            optimizer.addRule(new AnnotateTypesRule());
        }
        if (config.qualifyColumns()) {
            optimizer.addRule(new QualifyColumnsRule());
        }

        return optimizer.optimize(expr);
    }

    /**
     * Parses and optimizes SQL with ANSI dialect and default configuration.
     */
    public static Optional<Expression> parseAndOptimize(String sql) {
        return parseAndOptimize(sql, "ANSI", OptimizerConfig.DEFAULT);
    }

    /**
     * Parses and optimizes SQL with specified dialect and default configuration.
     */
    public static Optional<Expression> parseAndOptimize(String sql, String dialect) {
        return parseAndOptimize(sql, dialect, OptimizerConfig.DEFAULT);
    }

    /**
     * Parses and optimizes SQL with specified dialect and custom configuration.
     */
    public static Optional<Expression> parseAndOptimize(String sql, String dialect, OptimizerConfig config) {
        Optional<Expression> expr = parseOne(sql, dialect);
        return expr.map(e -> optimize(e, dialect, config));
    }
}
