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

import com.gtkcyber.sqlglot.dialect.*;
import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;
import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.tokens.Tokenizer;

/**
 * AWS Athena SQL dialect.
 *
 * Athena is an AWS interactive query service for S3 data built on Presto/Trino.
 * Key characteristics:
 * - Double quote identifiers (Presto-based)
 * - Backtick identifiers as alternative (Hive compatibility)
 * - S3 location specification for external tables
 * - Support for Parquet, ORC, CSV, JSON file formats
 * - Partitioning and bucketing support
 * - Complex data types: STRUCT, ARRAY, MAP
 * - Window functions and CTEs
 * - Lambda functions for array/map operations
 * - JSON operations with JSON path expressions
 * - View support with OR REPLACE
 * - Projection pushdown optimization
 * - Partition elimination optimization
 * - Row-based and columnar storage formats
 * - Integration with Glue Catalog metadata
 * - Iceberg table format support
 */
public class AthenaDialect extends Dialect {
    /**
     * Creates a new AWS Athena dialect instance.
     */
    public AthenaDialect() {
        super("ATHENA", NormalizationStrategy.UPPERCASE);
    }

    @Override
    public Tokenizer createTokenizer() {
        return new AthenaTokenizer();
    }

    @Override
    public Parser createParser() {
        return new AthenaParser();
    }

    @Override
    public Generator createGenerator(GeneratorConfig config) {
        return new AthenaGenerator(config);
    }
}
