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
package com.gtkcyber.sqlglot.dialects.databricks;

import com.gtkcyber.sqlglot.dialect.*;
import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;
import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.tokens.Tokenizer;

/**
 * Databricks SQL dialect.
 *
 * Databricks is a unified analytics platform built on Apache Spark with Delta Lake support.
 * Key characteristics:
 * - Backtick identifiers for reserved words and special characters
 * - Delta Lake tables with versioning and time travel
 * - MERGE support for upsert operations
 * - Python and Scala UDF support
 * - GPU acceleration capabilities
 * - Unity Catalog for multi-workspace governance
 * - Advanced data types: ARRAY, STRUCT, MAP
 * - COPY INTO for data ingestion
 * - Dynamic views and materialized views
 * - Liquid clustering for automatic partitioning
 * - Advanced optimization hints
 */
public class DatabricksDialect extends Dialect {
    /**
     * Creates a new Databricks dialect instance.
     */
    public DatabricksDialect() {
        super("DATABRICKS", NormalizationStrategy.UPPERCASE);
    }

    @Override
    public Tokenizer createTokenizer() {
        return new DatabricksTokenizer();
    }

    @Override
    public Parser createParser() {
        return new DatabricksParser();
    }

    @Override
    public Generator createGenerator(GeneratorConfig config) {
        return new DatabricksGenerator(config);
    }
}
