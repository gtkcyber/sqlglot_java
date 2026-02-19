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
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test basic operators that should work.
 */
class BasicOperatorsTest {

    @Test
    void testBetween() {
        String sql = "SELECT * FROM t WHERE x BETWEEN 1 AND 10";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse BETWEEN");
    }

    @Test
    void testInOperator() {
        String sql = "SELECT * FROM t WHERE x IN (1, 2, 3)";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse IN operator");
    }

    @Test
    void testIsNull() {
        String sql = "SELECT * FROM t WHERE x IS NULL";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse IS NULL");
    }

    @Test
    void testCast() {
        String sql = "SELECT CAST(a AS INT) FROM t";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse CAST");
    }

    @Test
    void testLike() {
        String sql = "SELECT * FROM t WHERE name LIKE '%foo%'";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse LIKE");
    }

    @Test
    void testComplexWhere() {
        String sql = "SELECT * FROM t WHERE a > 1 AND b < 10 OR c BETWEEN 5 AND 15";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse complex WHERE");
    }
}
