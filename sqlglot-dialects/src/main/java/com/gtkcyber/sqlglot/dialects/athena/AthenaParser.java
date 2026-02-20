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

import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.parser.ParserConfig;

/**
 * AWS Athena parser.
 * Extends base parser with Athena-specific syntax:
 * - Complex data types: STRUCT, ARRAY, MAP (Presto-compatible)
 * - S3 external table location specifications
 * - File format specifications: PARQUET, ORC, CSV, JSON
 * - Row format and SerDe specifications for custom formats
 * - Table properties for configuration
 * - Partition and bucket specifications
 * - Window functions with frame specifications
 * - Lambda functions for transformations
 * - JSON operations with JSON_EXTRACT and path expressions
 * - Interval arithmetic
 * - Advanced aggregation functions
 * - Type casting with TRY_CAST for safe conversions
 * - Glue Catalog metadata operations
 * - Iceberg format support
 * - WITH clauses for CTEs
 */
public class AthenaParser extends Parser {
    /**
     * Creates a new Athena parser with default configuration.
     */
    public AthenaParser() {
        this(ParserConfig.defaultConfig());
    }

    /**
     * Creates a new Athena parser with custom configuration.
     *
     * @param config Parser configuration options
     */
    public AthenaParser(ParserConfig config) {
        super(config);
    }
}
