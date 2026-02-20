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
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test aggregate functions.
 */
class AggregateFunctionsTest {


    @Test
    void testAggregatesWithGroupBy() {
        String sql = "SELECT category, COUNT(*), SUM(amount), AVG(price) FROM sales GROUP BY category";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse multiple aggregates");
    }

    @Test
    void testCountAll() {
        String sql = "SELECT COUNT(*), SUM(amount) FROM t";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse COUNT(*) and SUM");
    }

    @Test
    void testAggregateWithWhere() {
        String sql = "SELECT COUNT(*), SUM(amount) FROM transactions WHERE amount > 100";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse aggregates with WHERE");
    }

    @Test
    void testAggregateGeneration() {
        String originalSql = "SELECT COUNT(*), SUM(amount), AVG(price) FROM t";
        Optional<Expression> parsed = SqlGlot.parseOne(originalSql);
        assertTrue(parsed.isPresent(), "Failed to parse");

        String generated = SqlGlot.generate(parsed.get());
        System.out.println("Original: " + originalSql);
        System.out.println("Generated: " + generated);

        assertTrue(generated.contains("COUNT") && generated.contains("SUM"),
                "Generated should contain both COUNT and SUM: " + generated);
    }
}
