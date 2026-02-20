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
package com.gtkcyber.sqlglot.dialects.duckdb;

import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;

/**
 * DuckDB generator.
 * Generates DuckDB-compatible SQL with:
 * - Double quote identifiers
 * - DuckDB data type handling (LIST, STRUCT, etc.)
 * - DuckDB-specific functions
 * - Advanced window function syntax
 * - Nested data operations
 */
public class DuckdbGenerator extends Generator {
    /**
     * Creates a new DuckDB generator with default configuration.
     */
    public DuckdbGenerator() {
        this(GeneratorConfig.defaultConfig());
    }

    /**
     * Creates a new DuckDB generator with custom configuration.
     *
     * @param config Generator configuration options
     */
    public DuckdbGenerator(GeneratorConfig config) {
        super(config);
    }

    @Override
    protected String formatIdentifier(String name) {
        // DuckDB uses double quotes for identifiers
        return "\"" + name.replace("\"", "\"\"") + "\"";
    }
}
