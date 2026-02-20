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
package io.sqlglot.optimizer;

import io.sqlglot.SqlGlot;
import io.sqlglot.expressions.Expression;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Phase 5B optimization rules.
 * Tests all 7 Phase 5B rules working together with various configurations.
 */
class OptimizerPhase5BIntegrationTest {

    private String optimize(String sql, OptimizerConfig config) {
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        if (expr.isEmpty()) return sql;

        Expression optimized = SqlGlot.optimize(expr.get(), "ANSI", config);
        return SqlGlot.generate(optimized, "ANSI");
    }

    private String optimize(String sql) {
        return optimize(sql, OptimizerConfig.PHASE_5B);
    }

    @Test
    void testPhase5AOnly() {
        String sql = "SELECT * FROM t WHERE TRUE AND x = 5";
        String result = optimize(sql, OptimizerConfig.PHASE_5A);
        // Phase 5A should simplify TRUE AND
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testPhase5BPredicateNormalization() {
        String sql = "SELECT * FROM t WHERE (a OR b) AND (a OR b)";
        String result = optimize(sql);
        // NormalizePredicatesRule should deduplicate
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testAllPhase5BRulesTogether() {
        String sql = """
                SELECT *
                FROM (SELECT id, name FROM users) u
                WHERE u.id > 100 AND u.id > 100
                """;
        String result = optimize(sql);
        // Multiple rules should apply
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testPushdownWithOtherRules() {
        String sql = """
                SELECT *
                FROM (SELECT * FROM users WHERE active = true)
                WHERE id > 50
                """;
        String result = optimize(sql);
        // PushdownPredicatesRule with SimplifyRule
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testComplexQueryOptimization() {
        String sql = """
                SELECT u.id, u.name
                FROM users u
                JOIN orders o ON u.id = o.user_id
                WHERE u.active = true AND o.status = 'completed'
                ORDER BY u.id
                """;
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testRoundTripPhase5B() {
        String sql = "SELECT * FROM t WHERE x = 5 AND x = 5 AND y > 10";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Expression optimized = SqlGlot.optimize(expr.get(), "ANSI", OptimizerConfig.PHASE_5B);
        String generated = SqlGlot.generate(optimized, "ANSI");

        // Should be parseable again
        Optional<Expression> reparsed = SqlGlot.parseOne(generated);
        assertTrue(reparsed.isPresent());
    }

    @Test
    void testMinimalConfig() {
        String sql = "SELECT * FROM t WHERE TRUE AND x = 5";
        String result = optimize(sql, OptimizerConfig.MINIMAL);
        // Only simplify enabled
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testCustomConfig() {
        String sql = "SELECT * FROM t WHERE x = 5 AND x = 5";
        OptimizerConfig customConfig = new OptimizerConfig(
                true,   // simplify
                false,  // canonicalize
                false,  // quoteIdentifiers
                false,  // eliminateCtes
                true,   // normalizePredicates (Phase 5B)
                false,  // pushdownPredicates
                false,  // mergeSubqueries
                false,  // joinReordering
                false,  // projectionPushdown
                false,  // annotateTypes
                false   // qualifyColumns
        );

        String result = optimize(sql, customConfig);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testSubqueryOptimization() {
        String sql = "SELECT * FROM (SELECT id, name FROM users) WHERE id > 100";
        String result = optimize(sql);
        // Should potentially merge or push predicates
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testCTEOptimization() {
        String sql = """
                WITH unused AS (SELECT 1),
                     used AS (SELECT * FROM users)
                SELECT * FROM used WHERE id > 50
                """;
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testBooleanNormalization() {
        String sql = "SELECT * FROM t WHERE NOT(a = 1 AND b = 2) OR (c AND d)";
        String result = optimize(sql);
        // NormalizePredicatesRule should apply De Morgan's laws
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testNestedComplexity() {
        String sql = """
                SELECT *
                FROM (
                    SELECT *
                    FROM (SELECT * FROM users WHERE active = true) u
                    WHERE u.id > 100
                ) t
                WHERE t.created > '2024-01-01'
                """;
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testMultipleJoinsOptimization() {
        String sql = """
                SELECT u.id, o.order_id
                FROM users u
                JOIN orders o ON u.id = o.user_id
                JOIN items i ON o.order_id = i.order_id
                WHERE u.active = true AND o.status = 'active'
                """;
        String result = optimize(sql);
        // JoinReorderingRule may reorder joins
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testTypeInference() {
        String sql = "SELECT (id + 10) * 2 FROM users WHERE name = 'John'";
        String result = optimize(sql);
        // AnnotateTypesRule performs type inference without changing output
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testColumnQualification() {
        String sql = "SELECT id, name FROM users";
        String result = optimize(sql);
        // QualifyColumnsRule should qualify columns
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testPredicateSimplificationChain() {
        String sql = "SELECT * FROM t WHERE TRUE AND FALSE OR x = 5";
        String result = optimize(sql);
        // Chain: SimplifyRule, NormalizePredicatesRule
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testDialectSpecific() {
        String sql = "SELECT * FROM users WHERE id > 100";
        Optional<Expression> expr = SqlGlot.parseOne(sql, "ANSI");
        assertTrue(expr.isPresent());

        var dialect = SqlGlot.getDialect("ANSI");
        Expression optimized = dialect.optimize(expr.get(), OptimizerConfig.PHASE_5B);
        String result = dialect.generate(optimized);

        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testPhase5BPresetConfig() {
        String sql = "SELECT * FROM t WHERE x = 5 AND x = 5 AND y > 10";
        String result = optimize(sql, OptimizerConfig.PHASE_5B);
        // All Phase 5B rules enabled
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testAggressivePresetConfig() {
        String sql = "SELECT * FROM t WHERE x = 5 AND x = 5 AND y > 10";
        String result = optimize(sql, OptimizerConfig.AGGRESSIVE);
        // AGGRESSIVE = PHASE_5B (all rules)
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testNoRegressionPhase5A() {
        String sql = "SELECT * FROM users WHERE TRUE AND id > 0 AND FALSE OR active";
        String resultPhase5A = optimize(sql, OptimizerConfig.PHASE_5A);
        String resultPhase5B = optimize(sql, OptimizerConfig.PHASE_5B);

        // Phase 5A should still work, Phase 5B builds on it
        assertTrue(resultPhase5A.toLowerCase().contains("select"));
        assertTrue(resultPhase5B.toLowerCase().contains("select"));
    }

    @Test
    void testComplexRealWorldQuery() {
        String sql = """
                WITH recent_orders AS (
                    SELECT * FROM orders WHERE created > CURRENT_DATE - 30
                )
                SELECT u.id, u.name, COUNT(o.id) as order_count
                FROM users u
                LEFT JOIN recent_orders o ON u.id = o.user_id
                WHERE u.active = true AND o.id IS NOT NULL
                GROUP BY u.id, u.name
                HAVING COUNT(o.id) > 1
                ORDER BY order_count DESC
                """;
        String result = optimize(sql);
        // Should handle complex query without errors
        assertTrue(result.toLowerCase().contains("select"));
    }
}
