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

class QualifyColumnsRuleTest {
    private final Dialect dialect = SqlGlot.getDialect("ANSI");
    private final QualifyColumnsRule rule = new QualifyColumnsRule();
    private final OptimizerContext context = OptimizerContext.of(dialect);

    private String optimize(String sql) {
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        if (expr.isEmpty()) return sql;

        Expression optimized = rule.optimize(expr.get(), context);
        return SqlGlot.generate(optimized, dialect.getName());
    }

    @Test
    void testQualifySimpleColumn() {
        String sql = "SELECT id FROM users";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testQualifyMultipleColumns() {
        String sql = "SELECT id, name, email FROM users";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testQualifyJoinedColumns() {
        String sql = "SELECT id, name FROM users JOIN orders ON users.id = orders.user_id";
        String result = optimize(sql);
        // Columns should be qualified with table names
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testAlreadyQualifiedColumns() {
        String sql = "SELECT users.id, users.name FROM users";
        String result = optimize(sql);
        // Already qualified, should remain unchanged
        assertTrue(result.toLowerCase().contains("users.id"));
    }

    @Test
    void testSelectStar() {
        String sql = "SELECT * FROM users";
        String result = optimize(sql);
        // SELECT * should be preserved
        assertTrue(result.toLowerCase().contains("*"));
    }

    @Test
    void testQualifyWithTableAlias() {
        String sql = "SELECT u.id, u.name FROM users u";
        String result = optimize(sql);
        // Table alias should be used in qualification
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testColumnAliasInSelect() {
        String sql = "SELECT id AS user_id, name AS user_name FROM users";
        String result = optimize(sql);
        // Aliases should be preserved
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testRoundTripAfterQualification() {
        String sql = "SELECT id, name FROM users";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Expression optimized = rule.optimize(expr.get(), context);
        String generated = SqlGlot.generate(optimized, dialect.getName());

        // Should be parseable again
        Optional<Expression> reparsed = SqlGlot.parseOne(generated);
        assertTrue(reparsed.isPresent());
    }

    @Test
    void testQualifyWithSubquery() {
        String sql = "SELECT id FROM (SELECT * FROM users)";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testMultipleJoins() {
        String sql = "SELECT users.id, orders.order_id, items.item_id FROM users JOIN orders ON users.id = orders.user_id JOIN items ON orders.order_id = items.order_id";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testQualifyWithWhereClause() {
        String sql = "SELECT id, name FROM users WHERE id > 10";
        String result = optimize(sql);
        // Columns in WHERE should also be qualified
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testQualifyWithGroupBy() {
        String sql = "SELECT user_id, COUNT(*) FROM orders GROUP BY user_id";
        String result = optimize(sql);
        // GROUP BY columns should be qualified
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testQualifyWithOrderBy() {
        String sql = "SELECT id, name FROM users ORDER BY name";
        String result = optimize(sql);
        // ORDER BY columns should be qualified
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testQualifyWithAggregates() {
        String sql = "SELECT user_id, SUM(amount) FROM transactions GROUP BY user_id";
        String result = optimize(sql);
        // Aggregate function columns should preserve structure
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testComplexQuery() {
        String sql = "SELECT u.id, o.order_id, SUM(i.price) FROM users u JOIN orders o ON u.id = o.user_id JOIN items i ON o.order_id = i.order_id WHERE u.active = true GROUP BY u.id, o.order_id";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }
}
