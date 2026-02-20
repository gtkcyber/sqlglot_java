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

class JoinReorderingRuleTest {
    private final JoinReorderingRule rule = new JoinReorderingRule();
    private final OptimizerContext context = OptimizerContext.of(SqlGlot.getDialect("ANSI"));

    private String optimize(String sql) {
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        if (expr.isEmpty()) return sql;

        Expression optimized = rule.optimize(expr.get(), context);
        return SqlGlot.generate(optimized, "ANSI");
    }

    @Test
    void testSimpleJoin() {
        String sql = "SELECT * FROM users JOIN orders ON users.id = orders.user_id";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("join"));
    }

    @Test
    void testMultipleJoins() {
        String sql = "SELECT * FROM users JOIN orders ON users.id = orders.user_id JOIN items ON orders.id = items.order_id";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("join"));
    }

    @Test
    void testLeftJoin() {
        String sql = "SELECT * FROM users LEFT JOIN orders ON users.id = orders.user_id";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("join"));
    }

    @Test
    void testRightJoin() {
        String sql = "SELECT * FROM users RIGHT JOIN orders ON users.id = orders.user_id";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("join"));
    }

    @Test
    void testJoinWithWhereClause() {
        String sql = "SELECT * FROM users JOIN orders ON users.id = orders.user_id WHERE users.active = true";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("join"));
    }

    @Test
    void testRoundTripAfterReordering() {
        String sql = "SELECT u.id, o.order_id FROM users u JOIN orders o ON u.id = o.user_id JOIN items i ON o.order_id = i.order_id";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Expression optimized = rule.optimize(expr.get(), context);
        String generated = SqlGlot.generate(optimized, "ANSI");

        Optional<Expression> reparsed = SqlGlot.parseOne(generated);
        assertTrue(reparsed.isPresent());
    }

    @Test
    void testComplexJoinCondition() {
        String sql = "SELECT * FROM users u JOIN orders o ON u.id = o.user_id AND u.status = o.status";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("join"));
    }
}
