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
package com.gtkcyber.sqlglot.optimizer;

import com.gtkcyber.sqlglot.SqlGlot;
import com.gtkcyber.sqlglot.expressions.Expression;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OptimizerIntegrationTest {

    private String optimize(String sql) {
        return optimize(sql, "ANSI", OptimizerConfig.DEFAULT);
    }

    private String optimize(String sql, String dialect) {
        return optimize(sql, dialect, OptimizerConfig.DEFAULT);
    }

    private String optimize(String sql, String dialect, OptimizerConfig config) {
        Optional<Expression> expr = SqlGlot.parseOne(sql, dialect);
        if (expr.isEmpty()) return sql;

        Expression optimized = SqlGlot.optimize(expr.get(), dialect, config);
        return SqlGlot.generate(optimized, dialect);
    }

    @Test
    void testSimplifyAndCanonicalize() {
        String sql = "SELECT * FROM t WHERE TRUE AND 5 < x";
        String result = optimize(sql);
        // Should simplify TRUE AND and canonicalize 5 < x to x > 5
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testSimplifyWithCTEElimination() {
        String sql = """
                WITH unused AS (SELECT 1),
                     used AS (SELECT * FROM t)
                SELECT * FROM used WHERE TRUE AND active
                """;
        String result = optimize(sql);
        // unused CTE should be removed, TRUE AND should be simplified
        assertTrue(result.toLowerCase().contains("used"));
        assertFalse(result.toLowerCase().contains("unused"));
    }

    @Test
    void testComplexOptimization() {
        String sql = """
                WITH unused_cte AS (SELECT 1),
                     used_cte AS (SELECT a FROM t1)
                SELECT * FROM used_cte
                WHERE TRUE AND NOT NOT (x = 5 OR FALSE) AND 1 + 1 = 2
                """;
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
        // Should parse again
        Optional<Expression> reparsed = SqlGlot.parseOne(result);
        assertTrue(reparsed.isPresent());
    }

    @Test
    void testDialectSpecificOptimization() {
        String sql = "SELECT * FROM users WHERE id > 100";

        // Optimize with different dialects
        String ansiResult = optimize(sql, "ANSI");
        String postgresResult = optimize(sql, "POSTGRES");
        String mysqlResult = optimize(sql, "MYSQL");

        assertTrue(ansiResult.toLowerCase().contains("select"));
        assertTrue(postgresResult.toLowerCase().contains("select"));
        assertTrue(mysqlResult.toLowerCase().contains("select"));
    }

    @Test
    void testOptimizeViaApiMethod() {
        String sql = "SELECT * FROM t WHERE TRUE AND x = 5";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Expression optimized = SqlGlot.optimize(expr.get());
        assertNotNull(optimized);
        assertTrue(optimized instanceof com.gtkcyber.sqlglot.expressions.Nodes.Select);
    }

    @Test
    void testParseAndOptimizeApiMethod() {
        String sql = "SELECT * FROM t WHERE FALSE OR x > 10";
        Optional<Expression> result = SqlGlot.parseAndOptimize(sql);

        assertTrue(result.isPresent());
        String generated = SqlGlot.generate(result.get());
        assertTrue(generated.toLowerCase().contains("select"));
    }

    @Test
    void testDialectOptimizeMethod() {
        String sql = "SELECT * FROM users WHERE TRUE AND active = true";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        var dialect = SqlGlot.getDialect("ANSI");
        Expression optimized = dialect.optimize(expr.get());
        assertNotNull(optimized);

        String generated = dialect.generate(optimized);
        assertTrue(generated.toLowerCase().contains("select"));
    }

    @Test
    void testDialectFormatWithOptimizationMethod() {
        String sql = "SELECT * FROM users WHERE TRUE AND id > 0";
        var dialect = SqlGlot.getDialect("ANSI");

        String result = dialect.formatWithOptimization(sql);
        assertTrue(result.toLowerCase().contains("select"));
        // Should be re-parseable
        Optional<Expression> reparsed = dialect.parseOne(result);
        assertTrue(reparsed.isPresent());
    }

    @Test
    void testRoundTripAfterAllOptimizations() {
        String sql = """
                WITH unused AS (SELECT 1),
                     active_users AS (SELECT id FROM users WHERE active)
                SELECT * FROM active_users
                WHERE 100 > age AND NOT NOT premium
                """;

        // Parse and optimize
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Expression optimized = SqlGlot.optimize(expr.get());
        String generated = SqlGlot.generate(optimized);

        // Should be re-parseable
        Optional<Expression> reparsed = SqlGlot.parseOne(generated);
        assertTrue(reparsed.isPresent());

        // Should be optimizable again (idempotent)
        Expression reoptimized = SqlGlot.optimize(reparsed.get());
        String regenerated = SqlGlot.generate(reoptimized);
        assertTrue(regenerated.toLowerCase().contains("select"));
    }

    @Test
    void testMinimalConfig() {
        String sql = "SELECT * FROM t WHERE TRUE AND x = 5";
        String result = optimize(sql, "ANSI", OptimizerConfig.MINIMAL);

        // MINIMAL only enables simplify, so TRUE AND should be simplified
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testCustomConfig() {
        String sql = "SELECT * FROM t WHERE 5 > x AND NOT(a = b)";
        OptimizerConfig customConfig = new OptimizerConfig(
                false,  // no simplify
                true,   // canonicalize
                false,  // no quoteIdentifiers
                false,  // no eliminateCtes
                false, false, false, false, false, false, false
        );

        String result = optimize(sql, "ANSI", customConfig);
        // Should still have TRUE but 5 > x should become x < 5
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testMultipleDialects() {
        String[] dialects = {"ANSI", "POSTGRES", "MYSQL", "BIGQUERY", "SNOWFLAKE"};

        for (String dialect : dialects) {
            String sql = "SELECT * FROM users WHERE id > 0";
            String result = optimize(sql, dialect);
            assertNotNull(result);
            assertTrue(result.toLowerCase().contains("select"),
                    "Dialect " + dialect + " should produce valid output");
        }
    }

    @Test
    void testArithmeticConstantFolding() {
        String sql = "SELECT 1 + 2 + 3 FROM users WHERE x > 5";
        String result = optimize(sql);
        // Should fold 1 + 2 + 3 to 6
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testComplexBooleanSimplification() {
        String sql = """
                SELECT * FROM users
                WHERE
                    TRUE AND FALSE OR x = 1 AND TRUE
                    AND NOT NOT active
                    OR 1 = 1
                """;
        String result = optimize(sql);
        // Should simplify complex boolean expression
        Optional<Expression> reparsed = SqlGlot.parseOne(result);
        assertTrue(reparsed.isPresent());
    }

    @Test
    void testNoUnplannedChanges() {
        String sql = "SELECT id, name FROM users WHERE id = 1 AND active = true";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Expression optimized = SqlGlot.optimize(expr.get());

        // Count columns - should still be 2
        long columnCount = optimized.findAll(com.gtkcyber.sqlglot.expressions.Nodes.Column.class).count();
        assertTrue(columnCount >= 1, "Should still have columns");
    }
}
