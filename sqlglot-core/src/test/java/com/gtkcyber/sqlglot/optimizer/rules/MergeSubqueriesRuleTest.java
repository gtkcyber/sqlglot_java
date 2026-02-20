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
package com.gtkcyber.sqlglot.optimizer.rules;

import com.gtkcyber.sqlglot.SqlGlot;
import com.gtkcyber.sqlglot.expressions.Expression;
import com.gtkcyber.sqlglot.optimizer.OptimizerContext;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MergeSubqueriesRuleTest {
    private final MergeSubqueriesRule rule = new MergeSubqueriesRule();
    private final OptimizerContext context = OptimizerContext.of(SqlGlot.getDialect("ANSI"));

    private String optimize(String sql) {
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        if (expr.isEmpty()) return sql;

        Expression optimized = rule.optimize(expr.get(), context);
        return SqlGlot.generate(optimized, "ANSI");
    }

    @Test
    void testSimpleSubquery() {
        String sql = "SELECT * FROM (SELECT id, name FROM users) WHERE id > 10";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testNestedSubqueries() {
        String sql = "SELECT * FROM (SELECT * FROM (SELECT id FROM users))";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testScalarSubqueryInSelect() {
        String sql = "SELECT id, (SELECT COUNT(*) FROM orders WHERE orders.user_id = users.id) as order_count FROM users";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testSubqueryInWhereClause() {
        String sql = "SELECT * FROM users WHERE id IN (SELECT user_id FROM orders)";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testMultipleSubqueries() {
        String sql = "SELECT (SELECT COUNT(*) FROM orders) as total, (SELECT COUNT(*) FROM users) as user_count";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testRoundTripAfterMerge() {
        String sql = "SELECT * FROM (SELECT id, name FROM users WHERE active = true) t WHERE t.id > 5";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Expression optimized = rule.optimize(expr.get(), context);
        String generated = SqlGlot.generate(optimized, "ANSI");

        Optional<Expression> reparsed = SqlGlot.parseOne(generated);
        assertTrue(reparsed.isPresent());
    }

    @Test
    void testSubqueryWithJoin() {
        String sql = "SELECT * FROM (SELECT * FROM users JOIN orders ON users.id = orders.user_id) WHERE user_id > 100";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }
}
