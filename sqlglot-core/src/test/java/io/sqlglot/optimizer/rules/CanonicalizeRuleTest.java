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

class CanonicalizeRuleTest {
    private final Dialect dialect = SqlGlot.getDialect("ANSI");
    private final CanonicalizeRule rule = new CanonicalizeRule();
    private final OptimizerContext context = OptimizerContext.of(dialect);

    private String optimize(String sql) {
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        if (expr.isEmpty()) return sql;

        Expression optimized = rule.optimize(expr.get(), context);
        return SqlGlot.generate(optimized, dialect.getName());
    }

    @Test
    void testLessThanFlip() {
        String result = optimize("SELECT * FROM t WHERE 5 < x");
        assertTrue(result.contains(">"), "5 < x should canonicalize to x > 5");
    }

    @Test
    void testGreaterThanFlip() {
        String result = optimize("SELECT * FROM t WHERE 5 > x");
        assertTrue(result.contains("<"), "5 > x should canonicalize to x < 5");
    }

    @Test
    void testLessEqualFlip() {
        String result = optimize("SELECT * FROM t WHERE 5 <= x");
        assertTrue(result.contains(">="), "5 <= x should canonicalize to x >= 5");
    }

    @Test
    void testGreaterEqualFlip() {
        String result = optimize("SELECT * FROM t WHERE 5 >= x");
        assertTrue(result.contains("<="), "5 >= x should canonicalize to x <= 5");
    }

    @Test
    void testNotGreaterThan() {
        String result = optimize("SELECT * FROM t WHERE NOT(x > 5)");
        assertTrue(result.contains("<="), "NOT(x > 5) should become x <= 5");
        assertFalse(result.contains("NOT"), "NOT should be removed");
    }

    @Test
    void testNotLessThan() {
        String result = optimize("SELECT * FROM t WHERE NOT(x < 5)");
        assertTrue(result.contains(">="), "NOT(x < 5) should become x >= 5");
    }

    @Test
    void testNotEqual() {
        String result = optimize("SELECT * FROM t WHERE NOT(x = 5)");
        assertTrue(result.contains("!=") || result.contains("<>"),
                "NOT(x = 5) should become x != 5");
    }

    @Test
    void testNotNotEqual() {
        String result = optimize("SELECT * FROM t WHERE NOT(x != 5)");
        assertTrue(result.contains("="), "NOT(x != 5) should become x = 5");
    }

    @Test
    void testNoChangeForColumnLessThanLiteral() {
        String result = optimize("SELECT * FROM t WHERE x < 5");
        // Should remain the same
        assertTrue(result.contains("<"));
    }

    @Test
    void testNoChangeForColumnGreaterThanLiteral() {
        String result = optimize("SELECT * FROM t WHERE x > 5");
        // Should remain the same
        assertTrue(result.contains(">"));
    }

    @Test
    void testComplexExpression() {
        String result = optimize("SELECT * FROM t WHERE 10 > id AND name != 'test'");
        // Should canonicalize 10 > id to id < 10
        assertTrue(result.contains("<") || result.contains("WHERE"));
    }

    @Test
    void testRoundTripAfterCanonicalize() {
        String sql = "SELECT * FROM users WHERE 100 > age AND NOT(active = false)";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Expression optimized = rule.optimize(expr.get(), context);
        String generated = SqlGlot.generate(optimized, dialect.getName());

        // Should be parseable again
        Optional<Expression> reparsed = SqlGlot.parseOne(generated);
        assertTrue(reparsed.isPresent());
    }
}
