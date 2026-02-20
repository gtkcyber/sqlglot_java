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
package com.gtkcyber.sqlglot.dialects.clickhouse;

import com.gtkcyber.sqlglot.dialect.*;
import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;
import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.tokens.Tokenizer;

/**
 * ClickHouse SQL dialect.
 *
 * ClickHouse is a columnar OLAP database optimized for real-time analytics.
 * Key characteristics:
 * - Backtick identifiers
 * - Column-oriented storage with compression
 * - MergeTree table engine family
 * - ReplacingMergeTree for updates/deletes
 * - SummingMergeTree for pre-aggregation
 * - AggregatingMergeTree for complex aggregations
 * - Advanced aggregation functions: sumIf, countIf, avgIf, etc.
 * - Array operations: arrayMap, arrayFilter, arrayConcat, etc.
 * - Type-safe operations
 * - Distributed execution with Distributed tables
 * - Advanced analytics functions
 */
public class ClickhouseDialect extends Dialect {
    /**
     * Creates a new ClickHouse dialect instance.
     */
    public ClickhouseDialect() {
        super("CLICKHOUSE", NormalizationStrategy.UPPERCASE);
    }

    @Override
    public Tokenizer createTokenizer() {
        return new ClickhouseTokenizer();
    }

    @Override
    public Parser createParser() {
        return new ClickhouseParser();
    }

    @Override
    public Generator createGenerator(GeneratorConfig config) {
        return new ClickhouseGenerator(config);
    }
}
