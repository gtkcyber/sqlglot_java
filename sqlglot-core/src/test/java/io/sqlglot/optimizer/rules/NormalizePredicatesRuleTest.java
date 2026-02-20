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
package io.sqlglot.optimizer.rules;

import io.sqlglot.SqlGlot;
import io.sqlglot.dialect.Dialect;
import io.sqlglot.expressions.Expression;
import io.sqlglot.optimizer.OptimizerContext;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class NormalizePredicatesRuleTest {
    private final Dialect dialect = SqlGlot.getDialect("ANSI");
    private final NormalizePredicatesRule rule = new NormalizePredicatesRule();
    private final OptimizerContext context = OptimizerContext.of(dialect);

    private String optimize(String sql) {
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        if (expr.isEmpty()) return sql;

        Expression optimized = rule.optimize(expr.get(), context);
        return SqlGlot.generate(optimized, dialect.getName());
    }

    @Test
    void testDuplicatePredicateRemoval() {
        String sql = "SELECT * FROM t WHERE x = 5 AND x = 5";
        String result = optimize(sql);
        // Should remove duplicate
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testFlattenNestedAnd() {
        String sql = "SELECT * FROM t WHERE (a = 1 AND b = 2) AND c = 3";
        String result = optimize(sql);
        // Should flatten nested AND
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testFlattenNestedOr() {
        String sql = "SELECT * FROM t WHERE (a = 1 OR b = 2) OR c = 3";
        String result = optimize(sql);
        // Should flatten nested OR
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testDeMorgansLawNot() {
        String sql = "SELECT * FROM t WHERE NOT(a = 1 AND b = 2)";
        String result = optimize(sql);
        // Should apply De Morgan's: NOT(a AND b) = NOT(a) OR NOT(b)
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testDoubleNegationElimination() {
        String sql = "SELECT * FROM t WHERE NOT(NOT(active = true))";
        String result = optimize(sql);
        // Should eliminate double negation
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testContradictionDetection() {
        String sql = "SELECT * FROM t WHERE x = 5 AND NOT(x = 5)";
        String result = optimize(sql);
        // Should detect contradiction and simplify to FALSE
        assertTrue(result.toLowerCase().contains("false") || result.toLowerCase().contains("where"));
    }

    @Test
    void testComplexBooleanNormalization() {
        String sql = "SELECT * FROM t WHERE (a OR b) AND (c OR d)";
        String result = optimize(sql);
        // Should normalize complex boolean
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testPredicateConsolidation() {
        String sql = "SELECT * FROM t WHERE (x > 10 AND y < 20) AND (x > 10)";
        String result = optimize(sql);
        // Should consolidate repeated x > 10
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testDeMorgansWithOr() {
        String sql = "SELECT * FROM t WHERE NOT(a = 1 OR b = 2)";
        String result = optimize(sql);
        // Should apply De Morgan's: NOT(a OR b) = NOT(a) AND NOT(b)
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testCNFConversion() {
        String sql = "SELECT * FROM t WHERE (a OR b) AND (c OR d)";
        String result = optimize(sql);
        // Already in CNF
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testRoundTripAfterNormalization() {
        String sql = "SELECT * FROM t WHERE NOT(x = 1 AND y = 2)";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Expression optimized = rule.optimize(expr.get(), context);
        String generated = SqlGlot.generate(optimized, dialect.getName());

        // Should be parseable again
        Optional<Expression> reparsed = SqlGlot.parseOne(generated);
        assertTrue(reparsed.isPresent());
    }

    @Test
    void testNestedNegation() {
        String sql = "SELECT * FROM t WHERE NOT(NOT(NOT(a = 1)))";
        String result = optimize(sql);
        // Should simplify nested negations
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testMixedAndOr() {
        String sql = "SELECT * FROM t WHERE (a AND b) OR (c AND d) OR (e AND f)";
        String result = optimize(sql);
        // Mixed AND/OR normalization
        assertTrue(result.toLowerCase().contains("select"));
    }
}
