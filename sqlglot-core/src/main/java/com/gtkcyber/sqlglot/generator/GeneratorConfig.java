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
package com.gtkcyber.sqlglot.generator;

/**
 * Configuration for SQL code generation.
 */
public record GeneratorConfig(
    boolean pretty,        // Format with newlines and indentation
    boolean identify,      // Identify all identifiers (add quotes)
    boolean normalize,     // Normalize keywords to uppercase
    int indentLevel        // Current indentation level
) {
    /**
     * Creates a default config with pretty printing and normalized keywords.
     */
    public static GeneratorConfig defaultConfig() {
        return new GeneratorConfig(true, false, true, 0);
    }

    /**
     * Creates a compact config without pretty printing.
     */
    public static GeneratorConfig compact() {
        return new GeneratorConfig(false, false, true, 0);
    }

    /**
     * Creates a config with increased indentation.
     */
    public GeneratorConfig indent() {
        return new GeneratorConfig(pretty, identify, normalize, indentLevel + 1);
    }

    /**
     * Creates a config with decreased indentation.
     */
    public GeneratorConfig dedent() {
        return new GeneratorConfig(pretty, identify, normalize, Math.max(0, indentLevel - 1));
    }

    /**
     * Returns the indentation string.
     */
    public String getIndent() {
        return "  ".repeat(indentLevel);
    }

    /**
     * Returns a newline if pretty printing is enabled, empty string otherwise.
     */
    public String newline() {
        return pretty ? "\n" : " ";
    }
}
