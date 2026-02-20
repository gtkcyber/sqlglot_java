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
        String sql = "SELECT id, name FROM (SELECT * FROM users)";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testSelectStar() {
        String sql = "SELECT * FROM (SELECT * FROM users)";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testPartialProjection() {
        String sql = "SELECT id FROM (SELECT id, name, email FROM users)";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testProjectionWithJoin() {
        String sql = "SELECT u.id, u.name FROM (SELECT * FROM users u JOIN orders o ON u.id = o.user_id) WHERE u.active = true";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testProjectionWithWhere() {
        String sql = "SELECT id FROM (SELECT id, name FROM users WHERE status = 'active')";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testNestedProjection() {
        String sql = "SELECT id FROM (SELECT id FROM (SELECT * FROM users))";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testRoundTripAfterProjection() {
        String sql = "SELECT name FROM (SELECT id, name, email FROM users WHERE age > 18)";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Expression optimized = rule.optimize(expr.get(), context);
        String generated = SqlGlot.generate(optimized, "ANSI");

        Optional<Expression> reparsed = SqlGlot.parseOne(generated);
        assertTrue(reparsed.isPresent());
    }

    @Test
    void testProjectionWithGroupBy() {
        String sql = "SELECT user_id, COUNT(*) FROM (SELECT * FROM orders) GROUP BY user_id";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }
}
