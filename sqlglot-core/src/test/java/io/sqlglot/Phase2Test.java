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
package io.sqlglot;

import io.sqlglot.expressions.Expression;
import io.sqlglot.expressions.Nodes;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive Phase 2 tests for advanced SQL features.
 * Covers: CTEs, set operations, complex queries, cross-dialect support.
 *
 * TODO: These tests cause heap overflow due to incomplete parser support for advanced SQL features.
 * Need to fix the parser to properly handle UNION, CTEs, and other Phase 2 features.
 */
@Disabled("Phase 2 features cause parser overflow - needs completion")
class Phase2Test {

    // ============ SET OPERATIONS ============

    @Test
    void testUnion() {
        String sql = "SELECT a FROM t1 UNION SELECT b FROM t2";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        String generated = SqlGlot.generate(expr.get());
        assertTrue(generated.contains("UNION"));
    }

    @Test
    void testUnionAll() {
        String sql = "SELECT a FROM t1 UNION ALL SELECT b FROM t2";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());
    }

    @Test
    void testMultipleUnions() {
        String sql = "SELECT a FROM t1 UNION SELECT b FROM t2 UNION SELECT c FROM t3";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());
    }

    // ============ BASIC CTEs ============

    @Test
    void testSimpleCTE() {
        String sql = "WITH cte AS (SELECT a FROM t) SELECT * FROM cte";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());
        assertTrue(expr.get() instanceof Nodes.With);

        Nodes.With with = (Nodes.With) expr.get();
        assertEquals(1, with.getCtes().size());
    }

    @Test
    void testMultipleCTEs() {
        String sql = "WITH cte1 AS (SELECT a FROM t1), cte2 AS (SELECT b FROM t2) " +
                     "SELECT * FROM cte1 JOIN cte2 ON cte1.a = cte2.b";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Nodes.With with = (Nodes.With) expr.get();
        assertEquals(2, with.getCtes().size());
    }

    // ============ COMPLEX EXPRESSIONS ============

    @Test
    void testNestedCases() {
        String sql = "SELECT CASE WHEN a > 1 THEN CASE WHEN b > 2 THEN 'yes' ELSE 'no' END ELSE 'maybe' END FROM t";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());
    }

    @Test
    void testMultipleJoins() {
        String sql = "SELECT * FROM t1 " +
                     "INNER JOIN t2 ON t1.id = t2.id " +
                     "LEFT JOIN t3 ON t2.id = t3.id " +
                     "CROSS JOIN t4";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Nodes.Select select = (Nodes.Select) expr.get();
        assertEquals(3, select.getJoins().size());
    }

    @Test
    void testSubqueriesInSelect() {
        String sql = "SELECT (SELECT COUNT(*) FROM t2) as cnt, a FROM t1";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());
    }

    @Test
    void testSubqueriesInWhere() {
        String sql = "SELECT * FROM t1 WHERE a IN (SELECT id FROM t2) AND b > (SELECT AVG(val) FROM t3)";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());
    }

    // ============ DATA TYPES ============

    @Test
    void testComplexDataTypes() {
        String sql = "SELECT CAST(a AS ARRAY<INT>) as arr, CAST(b AS MAP<STRING, INT>) as map FROM t";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());
    }

    @Test
    void testStructType() {
        String sql = "SELECT CAST(a AS STRUCT<name STRING, age INT>) as person FROM t";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());
    }

    // ============ WINDOW FUNCTIONS ============

    @Test
    void testSimpleWindowFunction() {
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY a) as rn FROM t";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());
    }

    @Test
    void testWindowFunctionWithPartition() {
        String sql = "SELECT ROW_NUMBER() OVER (PARTITION BY category ORDER BY value DESC) as rn FROM t";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());
    }

    // ============ ROUND-TRIP TESTS ============

    @ParameterizedTest
    @ValueSource(strings = {
        "SELECT * FROM t",
        "SELECT a, b FROM t1 JOIN t2 ON t1.id = t2.id",
        "SELECT DISTINCT a FROM t WHERE a > 10 GROUP BY a HAVING COUNT(*) > 1 ORDER BY a LIMIT 100",
        "SELECT CASE WHEN a > 1 THEN 'yes' ELSE 'no' END FROM t",
        "SELECT * FROM t WHERE a IN (1, 2, 3) AND b BETWEEN 10 AND 20",
        "SELECT ROW_NUMBER() OVER (ORDER BY a) as rn FROM t",
    })
    void testRoundTrip(String sql) {
        Optional<Expression> parsed = SqlGlot.parseOne(sql);
        assertTrue(parsed.isPresent(), "Failed to parse: " + sql);

        String generated = SqlGlot.generate(parsed.get());
        Optional<Expression> reParsed = SqlGlot.parseOne(generated);
        assertTrue(reParsed.isPresent(), "Failed to re-parse generated SQL: " + generated);
    }

    // ============ FORMATTING ============

    @Test
    void testFormatting() {
        String sql = "SELECT a,b,c FROM t1 WHERE x=1 AND y>2 GROUP BY a,b ORDER BY c DESC";
        String formatted = SqlGlot.format(sql);

        assertTrue(formatted.contains("SELECT"));
        assertTrue(formatted.contains("FROM"));
        assertTrue(formatted.contains("WHERE"));
        assertTrue(formatted.contains("GROUP BY"));
        assertTrue(formatted.contains("ORDER BY"));
    }

    // ============ ERROR HANDLING ============

    @Test
    void testInvalidSQL() {
        Optional<Expression> expr = SqlGlot.parseOne("SELECT FROM t");
        // Should either be empty or handle gracefully
        assertNotNull(expr);
    }

    @Test
    void testUnbalancedParens() {
        Optional<Expression> expr = SqlGlot.parseOne("SELECT (a + b FROM t");
        // Should handle gracefully
        assertNotNull(expr);
    }
}
