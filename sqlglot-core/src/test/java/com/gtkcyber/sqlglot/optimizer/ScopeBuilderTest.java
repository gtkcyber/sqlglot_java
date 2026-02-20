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
package com.gtkcyber.sqlglot.optimizer;

import com.gtkcyber.sqlglot.SqlGlot;
import com.gtkcyber.sqlglot.expressions.Expression;
import com.gtkcyber.sqlglot.expressions.Nodes;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ScopeBuilderTest {

    @Test
    void testBuildSimpleSelect() {
        String sql = "SELECT * FROM users";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Scope scope = ScopeBuilder.build(expr.get());
        assertNotNull(scope);
        assertEquals(ScopeType.ROOT, scope.getType());
    }

    @Test
    void testBuildFromClause() {
        String sql = "SELECT * FROM users u";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Scope scope = ScopeBuilder.build(expr.get());
        assertTrue(scope.getSource("u").isPresent());
    }

    @Test
    void testBuildMultipleSources() {
        String sql = "SELECT * FROM users u, orders o";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Scope scope = ScopeBuilder.build(expr.get());
        assertTrue(scope.getSource("u").isPresent());
        assertTrue(scope.getSource("o").isPresent());
    }

    @Test
    void testBuildWithJoin() {
        String sql = "SELECT * FROM users u JOIN orders o ON u.id = o.user_id";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Scope scope = ScopeBuilder.build(expr.get());
        assertTrue(scope.getSource("u").isPresent());
        assertTrue(scope.getSource("o").isPresent());
    }

    @Test
    void testBuildWithCTE() {
        String sql = "WITH cte AS (SELECT * FROM users) SELECT * FROM cte";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Scope scope = ScopeBuilder.build(expr.get());
        assertTrue(scope.getSource("cte").isPresent());
        var source = scope.getSource("cte").get();
        assertTrue(source instanceof Source.ScopeSource);
    }

    @Test
    void testBuildWithMultipleCTEs() {
        String sql = """
                WITH cte1 AS (SELECT * FROM users),
                     cte2 AS (SELECT * FROM orders)
                SELECT * FROM cte1, cte2
                """;
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Scope scope = ScopeBuilder.build(expr.get());
        assertTrue(scope.getSource("cte1").isPresent());
        assertTrue(scope.getSource("cte2").isPresent());
    }

    @Test
    void testBuildUnusedCTE() {
        String sql = """
                WITH unused AS (SELECT 1),
                     used AS (SELECT a FROM t)
                SELECT * FROM used
                """;
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Scope scope = ScopeBuilder.build(expr.get());
        assertTrue(scope.getSource("unused").isPresent());
        assertTrue(scope.getSource("used").isPresent());

        // Check reference counts
        assertEquals(0, scope.getCTERefCount("unused"));
    }

    @Test
    void testBuildSubquery() {
        String sql = "SELECT * FROM (SELECT * FROM users) u";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Scope scope = ScopeBuilder.build(expr.get());
        assertTrue(scope.getSource("u").isPresent());
        var source = scope.getSource("u").get();
        assertTrue(source instanceof Source.ScopeSource);
    }

    @Test
    void testScopeType() {
        String sql = "SELECT * FROM users";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Scope scope = ScopeBuilder.build(expr.get());
        assertEquals(ScopeType.ROOT, scope.getType());
    }

    @Test
    void testBuildComplexQuery() {
        String sql = """
                WITH t1 AS (SELECT * FROM users WHERE active = true),
                     t2 AS (SELECT * FROM orders WHERE status = 'pending')
                SELECT *
                FROM t1
                JOIN t2 ON t1.id = t2.user_id
                WHERE t1.created_at > '2024-01-01'
                """;
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Scope scope = ScopeBuilder.build(expr.get());
        assertNotNull(scope);
        assertTrue(scope.getSource("t1").isPresent());
        assertTrue(scope.getSource("t2").isPresent());
    }

    @Test
    void testBuildNestedSubqueries() {
        String sql = "SELECT * FROM (SELECT * FROM (SELECT * FROM users) t1) t2";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Scope scope = ScopeBuilder.build(expr.get());
        assertTrue(scope.getSource("t2").isPresent());
        var source = scope.getSource("t2").get();
        assertTrue(source instanceof Source.ScopeSource);
    }
}
