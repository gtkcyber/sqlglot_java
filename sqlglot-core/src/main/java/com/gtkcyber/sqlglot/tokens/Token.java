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
package com.gtkcyber.sqlglot.tokens;

import java.util.List;
import java.util.Objects;

/**
 * Represents a single SQL token with type, text, position, and comments.
 */
public record Token(
    TokenType type,
    String text,
    int line,
    int col,
    int start,
    int end,
    List<String> comments
) {
    /**
     * Compact constructor for validation.
     */
    public Token {
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(text, "text cannot be null");
        Objects.requireNonNull(comments, "comments cannot be null");
        if (line < 1) throw new IllegalArgumentException("line must be >= 1");
        if (col < 1) throw new IllegalArgumentException("col must be >= 1");
        if (start < 0) throw new IllegalArgumentException("start must be >= 0");
        if (end < start) throw new IllegalArgumentException("end must be >= start");
    }

    /**
     * Creates a token with default position values (for testing).
     */
    public static Token of(TokenType type, String text) {
        return new Token(type, text, 1, 1, 0, text.length(), List.of());
    }

    /**
     * Creates a token with line and column info.
     */
    public static Token of(TokenType type, String text, int line, int col) {
        return new Token(type, text, line, col, 0, text.length(), List.of());
    }

    /**
     * Returns the length of this token's text.
     */
    public int length() {
        return end - start;
    }

    /**
     * Returns true if this token is a keyword.
     */
    public boolean isKeyword() {
        return type.isKeyword();
    }

    /**
     * Returns true if this token is an operator.
     */
    public boolean isOperator() {
        return type.isOperator();
    }

    /**
     * Returns true if this token is punctuation.
     */
    public boolean isPunctuation() {
        return type.isPunctuation();
    }

    /**
     * Returns a string representation: TYPE "text" (line:col)
     */
    @Override
    public String toString() {
        return String.format("%s \"%s\" (%d:%d)", type, text, line, col);
    }
}
