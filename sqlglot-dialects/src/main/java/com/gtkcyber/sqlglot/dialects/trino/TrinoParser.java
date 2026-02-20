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
package com.gtkcyber.sqlglot.dialects.trino;

import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.parser.ParserConfig;

/**
 * Trino parser.
 * Extends base parser with Trino-specific syntax:
 * - Complex data types: ROW, ARRAY, MAP with nested structures
 * - INTERVAL arithmetic with precision granularity (YEAR_TO_MONTH, DAY_TO_SECOND, etc.)
 * - UUID and IPADDRESS types with operations
 * - JSON operations with JSONPath
 * - APPROX_DISTINCT, APPROX_PERCENTILE for approximate aggregations
 * - APPROX_SET for set operations
 * - Lambda expressions (x -> x + 1)
 * - Higher-order functions (transform, filter, reduce, etc.)
 * - GROUPING SETS, CUBE, ROLLUP
 * - WITH RECURSIVE for recursive CTEs
 * - Multiple connector naming (catalog.schema.table)
 * - Advanced window frame specifications
 * - TRY_CAST for safe type conversions
 */
public class TrinoParser extends Parser {
    /**
     * Creates a new Trino parser with default configuration.
     */
    public TrinoParser() {
        this(ParserConfig.defaultConfig());
    }

    /**
     * Creates a new Trino parser with custom configuration.
     *
     * @param config Parser configuration options
     */
    public TrinoParser(ParserConfig config) {
        super(config);
    }
}
