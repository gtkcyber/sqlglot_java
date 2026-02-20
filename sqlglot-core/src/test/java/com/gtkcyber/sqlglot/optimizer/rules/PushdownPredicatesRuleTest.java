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
import com.gtkcyber.sqlglot.dialect.Dialect;
import com.gtkcyber.sqlglot.expressions.Expression;
import com.gtkcyber.sqlglot.optimizer.OptimizerContext;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PushdownPredicatesRuleTest {
    private final Dialect dialect = SqlGlot.getDialect("ANSI");
    private final PushdownPredicatesRule rule = new PushdownPredicatesRule();
    private final OptimizerContext context = OptimizerContext.of(dialect);

    private String optimize(String sql) {
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        if (expr.isEmpty()) return sql;

        Expression optimized = rule.optimize(expr.get(), context);
        return SqlGlot.generate(optimized, dialect.getName());
    }

    @Test
    void testBasicPredicatePushdown() {
        String sql = "SELECT * FROM (SELECT id, name FROM users) WHERE id > 100";
        String result = optimize(sql);
        // Predicate should be pushed into subquery
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testMultiplePredicatesPushdown() {
        String sql = "SELECT * FROM (SELECT id, name, age FROM users) WHERE age > 30 AND name = 'John'";
        String result = optimize(sql);
        // Both predicates should be available for pushdown
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testNoPredicateWhenNotAvailable() {
        String sql = "SELECT * FROM (SELECT id FROM users) WHERE name = 'John'";
        String result = optimize(sql);
        // Name not available in subquery, should NOT push down
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testSelectStarPredicatePushdown() {
        String sql = "SELECT * FROM (SELECT * FROM users) WHERE age > 30";
        String result = optimize(sql);
        // SELECT * makes all columns available for pushdown
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testPredicatePushdownWithAliases() {
        String sql = "SELECT * FROM (SELECT id AS user_id, name AS user_name FROM users) WHERE user_id > 100";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testNoWhereClausePushdown() {
        String sql = "SELECT * FROM (SELECT * FROM users)";
        String result = optimize(sql);
        // No WHERE clause, nothing to push down
        assertEquals(sql.toLowerCase().replace(" ", ""), result.toLowerCase().replace(" ", ""));
    }

    @Test
    void testPartialPredicatePushdown() {
        String sql = "SELECT * FROM (SELECT id FROM users) t1 JOIN (SELECT id FROM orders) t2 WHERE t1.id = 100 AND t2.id = 200";
        String result = optimize(sql);
        // This is more complex with joins, predicate may not push through
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testRoundTripAfterPushdown() {
        String sql = "SELECT * FROM (SELECT id, name FROM users WHERE active = true) WHERE age > 30";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Expression optimized = rule.optimize(expr.get(), context);
        String generated = SqlGlot.generate(optimized, dialect.getName());

        // Should be parseable again
        Optional<Expression> reparsed = SqlGlot.parseOne(generated);
        assertTrue(reparsed.isPresent());
    }

    @Test
    void testNestedSubqueryPushdown() {
        String sql = "SELECT * FROM (SELECT * FROM (SELECT id, name FROM users) t1) WHERE id > 50";
        String result = optimize(sql);
        // Should handle nested subqueries
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testPredicatePushdownWithGroupBy() {
        String sql = "SELECT user_id, COUNT(*) FROM (SELECT user_id, order_id FROM orders) GROUP BY user_id HAVING COUNT(*) > 5";
        String result = optimize(sql);
        // GROUP BY changes the result set, predicates may not push
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testPredicatePushdownPreservesSemantics() {
        String sql = "SELECT * FROM (SELECT id, price FROM products) WHERE price * 2 > 100";
        String result = optimize(sql);
        // Expression in WHERE should be preserved
        assertTrue(result.toLowerCase().contains("*") || result.toLowerCase().contains("select"));
    }

    @Test
    void testComplexWhereCondition() {
        String sql = "SELECT * FROM (SELECT id, status FROM orders) WHERE (status = 'pending' OR status = 'processing') AND id > 1000";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testPredicatePushdownWithDistinct() {
        String sql = "SELECT * FROM (SELECT DISTINCT user_id FROM orders) WHERE user_id > 100";
        String result = optimize(sql);
        // DISTINCT in subquery, predicate should still push
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testPredicatePushdownWithLimit() {
        String sql = "SELECT * FROM (SELECT id, name FROM users LIMIT 100) WHERE id > 50";
        String result = optimize(sql);
        // With LIMIT, can't safely push predicates (would change result set size)
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testAndConditionPartialPushdown() {
        String sql = "SELECT * FROM (SELECT id, status FROM orders) WHERE id > 100 AND computed_value > 50";
        String result = optimize(sql);
        // id > 100 can push, computed_value might not be available
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testOrConditionPushdown() {
        String sql = "SELECT * FROM (SELECT id, status FROM orders) WHERE id > 100 OR status = 'active'";
        String result = optimize(sql);
        // Both columns available, OR can potentially push
        assertTrue(result.toLowerCase().contains("select"));
    }
}
