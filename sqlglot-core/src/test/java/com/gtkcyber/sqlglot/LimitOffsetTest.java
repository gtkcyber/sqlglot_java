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
 * Test LIMIT and OFFSET clauses.
 */
class LimitOffsetTest {

    @Test
    void testSelectLimit() {
        String sql = "SELECT a FROM t LIMIT 10";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse SELECT with LIMIT");
        assertTrue(result.get() instanceof Nodes.Select, "Result should be Select node");

        Nodes.Select select = (Nodes.Select) result.get();
        assertNotNull(select.getLimit(), "Select should have LIMIT clause");
    }

    @Test
    void testSelectOffset() {
        String sql = "SELECT a FROM t OFFSET 5";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse SELECT with OFFSET");
        assertTrue(result.get() instanceof Nodes.Select, "Result should be Select node");

        Nodes.Select select = (Nodes.Select) result.get();
        assertNotNull(select.getOffset(), "Select should have OFFSET clause");
    }

    @Test
    void testSelectLimitOffset() {
        String sql = "SELECT a FROM t LIMIT 10 OFFSET 5";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse SELECT with LIMIT and OFFSET");
        assertTrue(result.get() instanceof Nodes.Select, "Result should be Select node");

        Nodes.Select select = (Nodes.Select) result.get();
        assertNotNull(select.getLimit(), "Select should have LIMIT clause");
        assertNotNull(select.getOffset(), "Select should have OFFSET clause");
    }

    @Test
    void testSelectLimitGeneration() {
        String originalSql = "SELECT a FROM t LIMIT 10";
        Optional<Expression> parsed = SqlGlot.parseOne(originalSql);
        assertTrue(parsed.isPresent(), "Failed to parse SELECT LIMIT");

        String generated = SqlGlot.generate(parsed.get());
        System.out.println("Original SQL: " + originalSql);
        System.out.println("Generated SQL: " + generated);

        assertTrue(generated.contains("LIMIT") || generated.contains("limit"),
                "Generated SQL should contain LIMIT: " + generated);
    }
}
