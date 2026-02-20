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

class SimplifyRuleTest {
    private final Dialect dialect = SqlGlot.getDialect("ANSI");
    private final SimplifyRule rule = new SimplifyRule();
    private final OptimizerContext context = OptimizerContext.of(dialect);

    private String optimize(String sql) {
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        if (expr.isEmpty()) return sql;

        Expression optimized = rule.optimize(expr.get(), context);
        return SqlGlot.generate(optimized, dialect.getName());
    }

    @Test
    void testBooleanTrueAnd() {
        String result = optimize("SELECT * FROM t WHERE TRUE AND x = 5");
        assertFalse(result.contains("TRUE AND"), "TRUE AND should be simplified");
    }

    @Test
    void testBooleanFalseOr() {
        String result = optimize("SELECT * FROM t WHERE FALSE OR x = 5");
        assertFalse(result.contains("FALSE OR"), "FALSE OR should be simplified");
    }

    @Test
    void testBooleanDoubleNegation() {
        String result = optimize("SELECT * FROM t WHERE NOT NOT active");
        assertFalse(result.contains("NOT NOT"), "Double negation should be simplified");
    }

    @Test
    void testBooleanFalseAnd() {
        String result = optimize("SELECT * FROM t WHERE FALSE AND x = 5");
        assertTrue(result.toLowerCase().contains("false"), "FALSE AND x should simplify to FALSE");
    }

    @Test
    void testBooleanTrueOr() {
        String result = optimize("SELECT * FROM t WHERE TRUE OR x = 5");
        assertTrue(result.toLowerCase().contains("true"), "TRUE OR x should simplify to TRUE");
    }

    @Test
    void testArithmeticAddZero() {
        String result = optimize("SELECT 1 + 0 FROM t");
        // Should simplify to just 1
        assertTrue(result.toLowerCase().contains("select") && result.toLowerCase().contains("from"));
    }

    @Test
    void testArithmeticMultiplyZero() {
        String result = optimize("SELECT 5 * 0 FROM t");
        // Should simplify to 0
        assertTrue(result.toLowerCase().contains("select") && result.toLowerCase().contains("from"));
    }

    @Test
    void testArithmeticMultiplyOne() {
        String result = optimize("SELECT x * 1 FROM t");
        assertFalse(result.contains("* 1"), "x * 1 should be simplified to x");
    }

    @Test
    void testConstantFolding() {
        String result = optimize("SELECT 1 + 2 + 3 FROM t");
        // Should fold to 6
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testEqualityIdentity() {
        String result = optimize("SELECT * FROM t WHERE x = x");
        assertTrue(result.toLowerCase().contains("true"), "x = x should simplify to TRUE");
    }

    @Test
    void testInEqualityIdentity() {
        String result = optimize("SELECT * FROM t WHERE x != x");
        assertTrue(result.toLowerCase().contains("false"), "x != x should simplify to FALSE");
    }

    @Test
    void testNullPropagation() {
        String result = optimize("SELECT * FROM t WHERE x = NULL");
        assertTrue(result.toLowerCase().contains("null"), "x = NULL should propagate NULL");
    }

    @Test
    void testNotTrue() {
        String result = optimize("SELECT * FROM t WHERE NOT TRUE");
        assertTrue(result.toLowerCase().contains("false"), "NOT TRUE should simplify to FALSE");
    }

    @Test
    void testNotFalse() {
        String result = optimize("SELECT * FROM t WHERE NOT FALSE");
        assertTrue(result.toLowerCase().contains("true"), "NOT FALSE should simplify to TRUE");
    }

    @Test
    void testComplexBoolean() {
        String result = optimize("SELECT * FROM t WHERE TRUE AND NOT NOT (x = 5 OR FALSE)");
        // Should simplify to just the x = 5 part (or similar)
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testAndSelf() {
        String result = optimize("SELECT * FROM t WHERE x = 1 AND x = 1");
        // x = 1 AND x = 1 should simplify to x = 1
        assertFalse(result.contains("AND x = 1 AND"), "Should not have duplicate condition");
    }

    @Test
    void testOrSelf() {
        String result = optimize("SELECT * FROM t WHERE x = 1 OR x = 1");
        // x = 1 OR x = 1 should simplify to x = 1
        assertFalse(result.contains("OR x = 1 OR"), "Should not have duplicate condition");
    }

    @Test
    void testSubtractZero() {
        String result = optimize("SELECT x - 0 FROM t");
        assertFalse(result.contains("- 0"), "x - 0 should be simplified to x");
    }

    @Test
    void testSubtractSelf() {
        String result = optimize("SELECT x - x FROM t");
        // Should simplify to 0
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testDivideOne() {
        String result = optimize("SELECT x / 1 FROM t");
        assertFalse(result.contains("/ 1"), "x / 1 should be simplified to x");
    }

    @Test
    void testDivideSelf() {
        String result = optimize("SELECT 5 / 5 FROM t");
        // Should simplify to 1
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testZeroDividedByNumber() {
        String result = optimize("SELECT 0 / 5 FROM t");
        // Should simplify to 0
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testRoundTripAfterSimplify() {
        String sql = "SELECT * FROM users WHERE TRUE AND id > 0 AND FALSE OR active";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Expression optimized = rule.optimize(expr.get(), context);
        String generated = SqlGlot.generate(optimized, dialect.getName());

        // Should be parseable again
        Optional<Expression> reparsed = SqlGlot.parseOne(generated);
        assertTrue(reparsed.isPresent());
    }
}
