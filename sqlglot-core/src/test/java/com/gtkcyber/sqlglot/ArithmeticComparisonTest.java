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
 * Test arithmetic and comparison operators in WHERE clauses.
 */
class ArithmeticComparisonTest {

    @Test
    void testAddition() {
        String sql = "SELECT a + b FROM t";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse addition");
    }

    @Test
    void testSubtraction() {
        String sql = "SELECT a - b FROM t";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse subtraction");
    }

    @Test
    void testMultiplication() {
        String sql = "SELECT a * b FROM t";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse multiplication");
    }

    @Test
    void testDivision() {
        String sql = "SELECT a / b FROM t";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse division");
    }

    @Test
    void testModulo() {
        String sql = "SELECT a % b FROM t";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse modulo");
    }

    @Test
    void testEqualsComparison() {
        String sql = "SELECT * FROM t WHERE a = 5";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse equals comparison");
    }

    @Test
    void testNotEqualsComparison() {
        String sql = "SELECT * FROM t WHERE a != 5";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse not equals comparison");
    }

    @Test
    void testLessThanComparison() {
        String sql = "SELECT * FROM t WHERE a < 5";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse less than comparison");
    }

    @Test
    void testGreaterThanComparison() {
        String sql = "SELECT * FROM t WHERE a > 5";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse greater than comparison");
    }

    @Test
    void testLessThanOrEqualComparison() {
        String sql = "SELECT * FROM t WHERE a <= 5";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse less than or equal comparison");
    }

    @Test
    void testGreaterThanOrEqualComparison() {
        String sql = "SELECT * FROM t WHERE a >= 5";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse greater than or equal comparison");
    }

    @Test
    void testCombinedArithmetic() {
        String sql = "SELECT a + b * c FROM t";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse combined arithmetic");
    }

    @Test
    void testMultipleComparisons() {
        String sql = "SELECT * FROM t WHERE a > 1 AND b < 10 AND c = 5";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse multiple comparisons");
    }

    @Test
    void testArithmeticGeneration() {
        String originalSql = "SELECT a + b FROM t";
        Optional<Expression> parsed = SqlGlot.parseOne(originalSql);
        assertTrue(parsed.isPresent(), "Failed to parse arithmetic");

        String generated = SqlGlot.generate(parsed.get());
        System.out.println("Original: " + originalSql);
        System.out.println("Generated: " + generated);

        assertTrue(generated.contains("+"),
                "Generated should contain +: " + generated);
    }

    @Test
    void testComparisonGeneration() {
        String originalSql = "SELECT * FROM t WHERE a > 5";
        Optional<Expression> parsed = SqlGlot.parseOne(originalSql);
        assertTrue(parsed.isPresent(), "Failed to parse comparison");

        String generated = SqlGlot.generate(parsed.get());
        System.out.println("Original: " + originalSql);
        System.out.println("Generated: " + generated);

        assertTrue(generated.contains(">"),
                "Generated should contain >: " + generated);
    }
}
