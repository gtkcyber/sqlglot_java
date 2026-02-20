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
package com.gtkcyber.sqlglot.parser;

import java.util.Objects;

/**
 * Configuration for the SQL parser.
 */
public record ParserConfig(
    ErrorLevel errorLevel,
    int maxErrors
) {
    /**
     * Compact constructor for validation.
     */
    public ParserConfig {
        Objects.requireNonNull(errorLevel, "errorLevel cannot be null");
        if (maxErrors < 1) throw new IllegalArgumentException("maxErrors must be >= 1");
    }

    /**
     * Creates a default config with RAISE error level and 100 max errors.
     */
    public static ParserConfig defaultConfig() {
        return new ParserConfig(ErrorLevel.RAISE, 100);
    }

    /**
     * Creates a permissive config that ignores errors.
     */
    public static ParserConfig permissive() {
        return new ParserConfig(ErrorLevel.IGNORE, Integer.MAX_VALUE);
    }

    /**
     * Creates a strict config that fails on first error.
     */
    public static ParserConfig strict() {
        return new ParserConfig(ErrorLevel.IMMEDIATE, 1);
    }
}
