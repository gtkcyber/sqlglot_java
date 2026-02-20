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
package com.gtkcyber.sqlglot.dialects.starrocks;

import com.gtkcyber.sqlglot.dialect.*;
import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;
import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.tokens.Tokenizer;

/**
 * StarRocks SQL dialect.
 *
 * StarRocks is a real-time, cost-effective OLAP database that supports both MPP and shared-nothing architectures.
 * Key characteristics:
 * - Backtick identifiers for reserved words and special characters
 * - Bitmap index support for cardinality estimation
 * - HyperLogLog (HLL) approximate counting
 * - Complex data types: ARRAY, STRUCT, MAP
 * - Auto partitioning and bucketing strategies
 * - Multiple table model support (Duplicate Key, Primary Key, Unique Key, Aggregate)
 * - Distributed query execution
 * - Column storage with compression codecs
 * - Colocated table joins
 * - Pipeline execution engine
 * - Advanced replication and recovery
 * - Material views for query optimization
 * - Dynamic schema evolution
 */
public class StarrocksDialect extends Dialect {
    /**
     * Creates a new StarRocks dialect instance.
     */
    public StarrocksDialect() {
        super("STARROCKS", NormalizationStrategy.UPPERCASE);
    }

    @Override
    public Tokenizer createTokenizer() {
        return new StarrocksTokenizer();
    }

    @Override
    public Parser createParser() {
        return new StarrocksParser();
    }

    @Override
    public Generator createGenerator(GeneratorConfig config) {
        return new StarrocksGenerator(config);
    }
}
