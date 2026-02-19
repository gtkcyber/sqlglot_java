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
 * Test DDL statements: CREATE, DROP, ALTER.
 */
class DDLTest {

    @Test
    void testCreateTable() {
        String sql = "CREATE TABLE users";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse CREATE TABLE");
    }

    @Test
    void testCreateTableGeneration() {
        String originalSql = "CREATE TABLE users";
        Optional<Expression> parsed = SqlGlot.parseOne(originalSql);
        assertTrue(parsed.isPresent(), "Failed to parse CREATE TABLE");

        String generated = SqlGlot.generate(parsed.get());
        System.out.println("Original: " + originalSql);
        System.out.println("Generated: " + generated);

        assertTrue(generated.contains("CREATE") || generated.contains("create"),
                "Generated should contain CREATE: " + generated);
    }

    @Test
    void testDropTable() {
        String sql = "DROP TABLE users";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse DROP TABLE");
    }

    @Test
    void testDropTableGeneration() {
        String originalSql = "DROP TABLE users";
        Optional<Expression> parsed = SqlGlot.parseOne(originalSql);
        assertTrue(parsed.isPresent(), "Failed to parse DROP TABLE");

        String generated = SqlGlot.generate(parsed.get());
        System.out.println("Original: " + originalSql);
        System.out.println("Generated: " + generated);

        assertTrue(generated.contains("DROP") || generated.contains("drop"),
                "Generated should contain DROP: " + generated);
    }

    @Test
    void testAlterTable() {
        String sql = "ALTER TABLE users";
        Optional<Expression> result = SqlGlot.parseOne(sql);
        assertTrue(result.isPresent(), "Failed to parse ALTER TABLE");
    }

    @Test
    void testAlterTableGeneration() {
        String originalSql = "ALTER TABLE users";
        Optional<Expression> parsed = SqlGlot.parseOne(originalSql);
        assertTrue(parsed.isPresent(), "Failed to parse ALTER TABLE");

        String generated = SqlGlot.generate(parsed.get());
        System.out.println("Original: " + originalSql);
        System.out.println("Generated: " + generated);

        assertTrue(generated.contains("ALTER") || generated.contains("alter"),
                "Generated should contain ALTER: " + generated);
    }
}
