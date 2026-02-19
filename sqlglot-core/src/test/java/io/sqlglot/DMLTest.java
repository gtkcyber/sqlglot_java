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
 * Test DML statements: INSERT, UPDATE, DELETE.
 */
class DMLTest {

    @Test
    void testInsert() {
        String sql = "INSERT INTO users VALUES (1, 'Alice')";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse INSERT");
    }

    @Test
    void testInsertGeneration() {
        String originalSql = "INSERT INTO users VALUES (1)";
        Optional<Expression> parsed = SqlGlot.parseOne(originalSql);
        assertTrue(parsed.isPresent(), "Failed to parse INSERT");

        String generated = SqlGlot.generate(parsed.get());
        System.out.println("Original: " + originalSql);
        System.out.println("Generated: " + generated);

        assertTrue(generated.contains("INSERT") || generated.contains("insert"),
                "Generated should contain INSERT: " + generated);
    }

    @Test
    void testDelete() {
        String sql = "DELETE FROM users";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse DELETE");
    }

    @Test
    void testDeleteWithWhere() {
        String sql = "DELETE FROM users WHERE id = 1";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse DELETE with WHERE");
    }

    @Test
    void testDeleteGeneration() {
        String originalSql = "DELETE FROM users";
        Optional<Expression> parsed = SqlGlot.parseOne(originalSql);
        assertTrue(parsed.isPresent(), "Failed to parse DELETE");

        String generated = SqlGlot.generate(parsed.get());
        System.out.println("Original: " + originalSql);
        System.out.println("Generated: " + generated);

        assertTrue(generated.contains("DELETE") || generated.contains("delete"),
                "Generated should contain DELETE: " + generated);
    }
}
