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
