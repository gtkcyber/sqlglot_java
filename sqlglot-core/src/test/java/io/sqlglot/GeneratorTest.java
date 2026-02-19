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

import io.sqlglot.expressions.*;
import io.sqlglot.generator.Generator;
import io.sqlglot.generator.GeneratorConfig;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SQL generator.
 */
class GeneratorTest {
    private final Generator generator = new Generator(GeneratorConfig.compact());

    @Test
    void testLiteral() {
        Expression expr = new Nodes.Literal("42", false);
        assertEquals("42", generator.sql(expr));
    }

    @Test
    void testStringLiteral() {
        Expression expr = new Nodes.Literal("hello", true);
        assertEquals("'hello'", generator.sql(expr));
    }

    @Test
    void testIdentifier() {
        Expression expr = new Nodes.Identifier("myColumn");
        assertEquals("myColumn", generator.sql(expr));
    }

    @Test
    void testColumn() {
        Expression expr = new Nodes.Column(
            new Nodes.Identifier("t"),
            new Nodes.Identifier("a")
        );
        assertEquals("t.a", generator.sql(expr));
    }

    @Test
    void testAdd() {
        Expression expr = new Nodes.Add(
            new Nodes.Literal("1", false),
            new Nodes.Literal("2", false)
        );
        assertEquals("1 + 2", generator.sql(expr));
    }

    @Test
    void testMultiply() {
        Expression expr = new Nodes.Mul(
            new Nodes.Literal("2", false),
            new Nodes.Literal("3", false)
        );
        assertEquals("2 * 3", generator.sql(expr));
    }

    @Test
    void testSelect() {
        Nodes.Select expr = new Nodes.Select(List.of(
            new Nodes.Identifier("a"),
            new Nodes.Identifier("b")
        ));
        String sql = generator.sql(expr);
        assertTrue(sql.contains("SELECT"));
        assertTrue(sql.contains("a"));
        assertTrue(sql.contains("b"));
    }

    @Test
    void testSelectWithFrom() {
        Nodes.Select expr = new Nodes.Select(
            List.of(Nodes.Star.INSTANCE),
            new Nodes.From(new Nodes.Identifier("myTable")),
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
        String sql = generator.sql(expr);
        assertTrue(sql.contains("SELECT"));
        assertTrue(sql.contains("FROM"));
    }

    @Test
    void testFunction() {
        Expression expr = new Nodes.Function("COUNT", List.of(Nodes.Star.INSTANCE));
        String result = generator.sql(expr);
        assertEquals("COUNT(*)", result);
    }

    @Test
    void testNull() {
        Expression expr = Nodes.Null.INSTANCE;
        assertEquals("NULL", generator.sql(expr));
    }

    @Test
    void testTrue() {
        Expression expr = Nodes.True.INSTANCE;
        assertEquals("TRUE", generator.sql(expr));
    }

    @Test
    void testFalse() {
        Expression expr = Nodes.False.INSTANCE;
        assertEquals("FALSE", generator.sql(expr));
    }
}
