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

import com.gtkcyber.sqlglot.tokens.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SQL tokenizer.
 */
class TokenizerTest {
    private final Tokenizer tokenizer = new Tokenizer();

    @Test
    void testBasicTokens() {
        List<Token> tokens = tokenizer.tokenize("SELECT a FROM t");

        assertEquals(5, tokens.size());
        assertEquals(TokenType.SELECT, tokens.get(0).type());
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).type());
        assertEquals("a", tokens.get(1).text());
        assertEquals(TokenType.FROM, tokens.get(2).type());
        assertEquals(TokenType.IDENTIFIER, tokens.get(3).type());
        assertEquals("t", tokens.get(3).text());
    }

    @Test
    void testNumbers() {
        List<Token> tokens = tokenizer.tokenize("SELECT 123, 45.67, 1e5");

        assertEquals(TokenType.SELECT, tokens.get(0).type());
        assertEquals(TokenType.NUMBER, tokens.get(1).type());
        assertEquals("123", tokens.get(1).text());
        assertEquals(TokenType.NUMBER, tokens.get(3).type());
        assertEquals("45.67", tokens.get(3).text());
    }

    @Test
    void testStrings() {
        List<Token> tokens = tokenizer.tokenize("SELECT 'hello', 'world'");

        assertEquals(TokenType.SELECT, tokens.get(0).type());
        assertEquals(TokenType.STRING, tokens.get(1).type());
        assertEquals("hello", tokens.get(1).text());
        assertEquals(TokenType.STRING, tokens.get(3).type());
        assertEquals("world", tokens.get(3).text());
    }

    @Test
    void testOperators() {
        List<Token> tokens = tokenizer.tokenize("a = 1 AND b > 2 OR c <= 3");

        assertTrue(tokens.stream().anyMatch(t -> t.type() == TokenType.EQ));
        assertTrue(tokens.stream().anyMatch(t -> t.type() == TokenType.AND));
        assertTrue(tokens.stream().anyMatch(t -> t.type() == TokenType.GT));
        assertTrue(tokens.stream().anyMatch(t -> t.type() == TokenType.OR));
        assertTrue(tokens.stream().anyMatch(t -> t.type() == TokenType.LTE));
    }

    @Test
    void testEmptyString() {
        List<Token> tokens = tokenizer.tokenize("");
        assertEquals(1, tokens.size());
        assertEquals(TokenType.EOF, tokens.get(0).type());
    }
}
