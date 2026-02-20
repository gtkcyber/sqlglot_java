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
package com.gtkcyber.sqlglot;

import com.gtkcyber.sqlglot.expressions.Expression;
import com.gtkcyber.sqlglot.expressions.Nodes;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test individual advanced features to identify which causes issues.
 * Run each feature in a separate test class to avoid memory issues.
 */
class FeatureDebugTest {

    @Test
    void testUnionAll() {
        String sql = "SELECT a FROM t1 UNION ALL SELECT b FROM t2";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse UNION ALL");
    }

    @Test
    void testUnionGeneration() {
        String originalSql = "SELECT a FROM t1 UNION SELECT b FROM t2";
        Optional<Expression> parsed = SqlGlot.parseOne(originalSql);
        assertTrue(parsed.isPresent(), "Failed to parse UNION");

        // Try to generate SQL back from the parsed expression
        String generated = SqlGlot.generate(parsed.get());
        System.out.println("Original SQL: " + originalSql);
        System.out.println("Generated SQL: " + generated);

        // Should contain UNION keyword
        assertTrue(generated.contains("UNION") || generated.contains("Union") || generated.contains("union"),
                "Generated SQL should contain UNION: " + generated);
    }

    @Test
    void testSimpleCTE() {
        String sql = "WITH cte AS (SELECT a FROM t) SELECT * FROM cte";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse simple CTE");
        assertTrue(result.get() instanceof Nodes.With, "Result should be With node");
    }

    @Test
    void testMultipleCTEs() {
        String sql = "WITH cte1 AS (SELECT a FROM t1), cte2 AS (SELECT b FROM t2) SELECT * FROM cte1";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse multiple CTEs");
    }

    @Test
    void testCTEGeneration() {
        String originalSql = "WITH cte AS (SELECT a FROM t) SELECT * FROM cte";
        Optional<Expression> parsed = SqlGlot.parseOne(originalSql);
        assertTrue(parsed.isPresent(), "Failed to parse CTE");

        String generated = SqlGlot.generate(parsed.get());
        System.out.println("Original SQL: " + originalSql);
        System.out.println("Generated SQL: " + generated);

        // Should contain WITH keyword
        assertTrue(generated.contains("WITH") || generated.contains("with"),
                "Generated SQL should contain WITH: " + generated);
    }

}
