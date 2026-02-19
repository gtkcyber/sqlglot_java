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
package io.sqlglot.parser;

import java.util.Objects;

/**
 * Represents a single parse error with location information.
 */
public record ParseError(
    String message,
    int line,
    int col,
    String sql
) {
    /**
     * Compact constructor for validation.
     */
    public ParseError {
        Objects.requireNonNull(message, "message cannot be null");
        Objects.requireNonNull(sql, "sql cannot be null");
        if (line < 1) throw new IllegalArgumentException("line must be >= 1");
        if (col < 1) throw new IllegalArgumentException("col must be >= 1");
    }

    /**
     * Returns a formatted error message with location.
     */
    @Override
    public String toString() {
        return String.format("Parse error at line %d, col %d: %s\n%s", line, col, message, sql);
    }
}
