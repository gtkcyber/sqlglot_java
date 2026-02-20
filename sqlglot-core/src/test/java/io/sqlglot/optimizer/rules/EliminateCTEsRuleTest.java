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

class EliminateCTEsRuleTest {
    private final Dialect dialect = SqlGlot.getDialect("ANSI");
    private final EliminateCTEsRule rule = new EliminateCTEsRule();
    private final OptimizerContext context = OptimizerContext.of(dialect);

    private String optimize(String sql) {
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        if (expr.isEmpty()) return sql;

        Expression optimized = rule.optimize(expr.get(), context);
        return SqlGlot.generate(optimized, dialect.getName());
    }

    @Test
    void testUnusedCTERemoval() {
        String sql = """
                WITH unused AS (SELECT 1)
                SELECT * FROM t
                """;
        String result = optimize(sql);
        assertFalse(result.toUpperCase().contains("WITH"), "Unused CTE should be removed");
    }

    @Test
    void testUsedCTERetention() {
        String sql = """
                WITH used AS (SELECT * FROM users)
                SELECT * FROM used
                """;
        String result = optimize(sql);
        assertTrue(result.toUpperCase().contains("WITH"), "Used CTE should be retained");
    }

    @Test
    void testMultipleCTEsPartialRemoval() {
        String sql = """
                WITH unused1 AS (SELECT 1),
                     used AS (SELECT * FROM orders),
                     unused2 AS (SELECT 2)
                SELECT * FROM used
                """;
        String result = optimize(sql);
        assertTrue(result.toUpperCase().contains("WITH"), "Used CTE should remain");
        assertTrue(result.toUpperCase().contains("used"), "used CTE should be present");
    }

    @Test
    void testCTEUsedInJoin() {
        String sql = """
                WITH cte1 AS (SELECT id FROM users),
                     cte2 AS (SELECT user_id FROM orders)
                SELECT * FROM cte1 JOIN cte2 ON cte1.id = cte2.user_id
                """;
        String result = optimize(sql);
        assertTrue(result.toUpperCase().contains("WITH"), "Both CTEs should be retained");
    }

    @Test
    void testUnusedCTEReferencedByUnusedCTE() {
        String sql = """
                WITH unused1 AS (SELECT * FROM users),
                     unused2 AS (SELECT * FROM unused1)
                SELECT * FROM t
                """;
        String result = optimize(sql);
        assertFalse(result.toUpperCase().contains("WITH"), "Both unused CTEs should be removed");
    }

    @Test
    void testChainedCTEs() {
        String sql = """
                WITH cte1 AS (SELECT 1),
                     cte2 AS (SELECT * FROM cte1),
                     cte3 AS (SELECT * FROM cte2)
                SELECT * FROM cte3
                """;
        String result = optimize(sql);
        assertTrue(result.toUpperCase().contains("WITH"), "All chained CTEs should be retained");
    }

    @Test
    void testAllCTEsUnused() {
        String sql = """
                WITH cte1 AS (SELECT 1),
                     cte2 AS (SELECT 2)
                SELECT * FROM t
                """;
        String result = optimize(sql);
        assertFalse(result.toUpperCase().contains("WITH"), "All unused CTEs should be removed");
    }

    @Test
    void testNoCTEs() {
        String sql = "SELECT * FROM users";
        String result = optimize(sql);
        assertEquals(sql, result, "Query without CTEs should remain unchanged");
    }

    @Test
    void testRoundTripAfterCTEElimination() {
        String sql = """
                WITH unused AS (SELECT 1),
                     used AS (SELECT a FROM t)
                SELECT * FROM used
                """;
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Expression optimized = rule.optimize(expr.get(), context);
        String generated = SqlGlot.generate(optimized, dialect.getName());

        // Should be parseable again
        Optional<Expression> reparsed = SqlGlot.parseOne(generated);
        assertTrue(reparsed.isPresent());
    }

    @Test
    void testComplexQueryWithUnusedCTE() {
        String sql = """
                WITH unused_data AS (SELECT * FROM old_table),
                     active_users AS (SELECT id, name FROM users WHERE active = true)
                SELECT au.id, au.name
                FROM active_users au
                WHERE au.id > 100
                """;
        String result = optimize(sql);
        assertTrue(result.toUpperCase().contains("active_users"), "Used CTE should remain");
        assertFalse(result.toUpperCase().contains("unused_data"), "Unused CTE should be removed");
    }

    @Test
    void testCTEInSubquery() {
        String sql = """
                WITH cte AS (SELECT 1)
                SELECT * FROM (SELECT * FROM cte)
                """;
        String result = optimize(sql);
        assertTrue(result.toUpperCase().contains("WITH"), "CTE used in subquery should be retained");
    }

    @Test
    void testSingleUnusedCTE() {
        String sql = "WITH unused AS (SELECT 1 AS num) SELECT * FROM table1";
        String result = optimize(sql);
        assertFalse(result.toUpperCase().contains("WITH"), "Single unused CTE should be removed");
    }

    @Test
    void testMultipleUsedCTEs() {
        String sql = """
                WITH cte1 AS (SELECT * FROM t1),
                     cte2 AS (SELECT * FROM t2)
                SELECT * FROM cte1, cte2
                """;
        String result = optimize(sql);
        assertTrue(result.toUpperCase().contains("cte1"), "cte1 should be retained");
        assertTrue(result.toUpperCase().contains("cte2"), "cte2 should be retained");
    }
}
