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
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test DISTINCT keyword support.
 */
class DistinctTest {

    @Test
    void testSelectDistinctGeneration() {
        String originalSql = "SELECT DISTINCT name FROM users";
        Optional<Expression> parsed = SqlGlot.parseOne(originalSql);
        assertTrue(parsed.isPresent(), "Failed to parse SELECT DISTINCT");

        String generated = SqlGlot.generate(parsed.get());
        System.out.println("Original SQL: " + originalSql);
        System.out.println("Generated SQL: " + generated);

        assertTrue(generated.contains("DISTINCT") || generated.contains("distinct"),
                "Generated SQL should contain DISTINCT: " + generated);
    }

    @Test
    void testSelectDistinct() {
        String sql = "SELECT DISTINCT a FROM t";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse SELECT DISTINCT");
        assertTrue(result.get() instanceof Nodes.Select, "Result should be Select node");

        Nodes.Select select = (Nodes.Select) result.get();
        assertTrue(select.isDistinct(), "Select should have distinct=true");
    }
}
