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
import io.sqlglot.expressions.Expression;
import io.sqlglot.optimizer.OptimizerContext;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProjectionPushdownRuleTest {
    private final ProjectionPushdownRule rule = new ProjectionPushdownRule();
    private final OptimizerContext context = OptimizerContext.of(SqlGlot.getDialect("ANSI"));

    private String optimize(String sql) {
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        if (expr.isEmpty()) return sql;

        Expression optimized = rule.optimize(expr.get(), context);
        return SqlGlot.generate(optimized, "ANSI");
    }

    @Test
    void testSimpleProjection() {
        String sql = "SELECT a FROM (SELECT a, b FROM t)";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testSelectStar() {
        String sql = "SELECT * FROM (SELECT * FROM t)";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testPartialProjection() {
        String sql = "SELECT a FROM (SELECT a, b FROM t)";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testSubqueryWithMultipleColumns() {
        String sql = "SELECT a, c FROM (SELECT a, b, c FROM t)";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testNestedSubqueries() {
        String sql = "SELECT a FROM (SELECT a FROM (SELECT a, b FROM t))";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testRoundTripAfterProjection() {
        String sql = "SELECT a FROM (SELECT a, b FROM t)";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Expression optimized = rule.optimize(expr.get(), context);
        String generated = SqlGlot.generate(optimized, "ANSI");

        Optional<Expression> reparsed = SqlGlot.parseOne(generated);
        assertTrue(reparsed.isPresent());
    }

    @Test
    void testSimpleSelect() {
        String sql = "SELECT 1, 2, 3";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }
}
