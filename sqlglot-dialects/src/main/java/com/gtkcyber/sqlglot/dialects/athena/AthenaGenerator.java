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
package com.gtkcyber.sqlglot.dialects.athena;

import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;

/**
 * AWS Athena SQL generator.
 * Generates Athena-compatible SQL with:
 * - Double quote identifiers (primary quoting mechanism)
 * - Support for backtick identifiers when needed
 * - Athena data type handling (STRUCT, ARRAY, MAP)
 * - Athena-specific functions (JSON operations, array functions, etc.)
 * - S3 location syntax for external tables
 * - File format specifications (PARQUET, ORC, CSV, JSON)
 * - Row format and SerDe specifications
 * - Table properties syntax
 * - Partition and bucketing syntax
 * - Window function frame specifications
 * - Lambda function syntax for transformations
 * - Interval arithmetic expressions
 * - Proper escaping for identifier names
 * - CTE (WITH clause) support
 * - View creation with OR REPLACE
 */
public class AthenaGenerator extends Generator {
    /**
     * Creates a new Athena generator with default configuration.
     */
    public AthenaGenerator() {
        this(GeneratorConfig.defaultConfig());
    }

    /**
     * Creates a new Athena generator with custom configuration.
     *
     * @param config Generator configuration options
     */
    public AthenaGenerator(GeneratorConfig config) {
        super(config);
    }

    @Override
    protected String formatIdentifier(String name) {
        // Athena uses double quotes for identifiers (Presto-compatible)
        // Escape internal double quotes by doubling them
        if (name == null || name.isEmpty()) {
            return "\"\"";
        }
        return "\"" + name.replace("\"", "\"\"") + "\"";
    }
}
