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
 * Test function calls with arguments.
 */
class FunctionCallTest {

    @Test
    void testCountStar() {
        String sql = "SELECT COUNT(*) FROM t";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse COUNT(*)");
    }

    @Test
    void testCountStarGeneration() {
        String originalSql = "SELECT COUNT(*) FROM t";
        Optional<Expression> parsed = SqlGlot.parseOne(originalSql);
        assertTrue(parsed.isPresent(), "Failed to parse COUNT(*)");

        String generated = SqlGlot.generate(parsed.get());
        System.out.println("Original: " + originalSql);
        System.out.println("Generated: " + generated);

        assertTrue(generated.contains("COUNT") || generated.contains("count"),
                "Generated should contain COUNT: " + generated);
    }

    @Test
    void testCountColumn() {
        String sql = "SELECT COUNT(a) FROM t";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse COUNT(column)");
    }

    @Test
    void testSumFunction() {
        String sql = "SELECT SUM(amount) FROM t";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse SUM function");
    }

    @Test
    void testAvgFunction() {
        String sql = "SELECT AVG(price) FROM t";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse AVG function");
    }

    @Test
    void testMaxFunction() {
        String sql = "SELECT MAX(value) FROM t";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse MAX function");
    }

    @Test
    void testMinFunction() {
        String sql = "SELECT MIN(value) FROM t";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse MIN function");
    }

    @Test
    void testFunctionWithMultipleArguments() {
        String sql = "SELECT SUBSTR(name, 1, 3) FROM t";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse SUBSTR with multiple arguments");
    }

    @Test
    void testMultipleFunctionsInSelect() {
        String sql = "SELECT COUNT(*), SUM(amount), MAX(price) FROM t";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse multiple functions in SELECT");
    }
}
