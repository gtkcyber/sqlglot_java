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
package com.gtkcyber.sqlglot.dialects.presto;

import com.gtkcyber.sqlglot.dialect.*;
import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;
import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.tokens.Tokenizer;

/**
 * Presto SQL dialect.
 *
 * Presto is a distributed SQL query engine for interactive analytics.
 * Key characteristics:
 * - Double quote identifiers
 * - Multiple data source connectors (Hive, MySQL, PostgreSQL, etc.)
 * - Complex data types: ROW, ARRAY, MAP
 * - Advanced window functions
 * - APPROX functions for approximate aggregations
 * - Advanced JSON support
 * - Type casting with TRY_CAST
 * - MAP and REDUCE operations on arrays
 * - Interval arithmetic
 * - UUID type
 * - IPAddress type
 * - HyperLogLog type for cardinality estimation
 * - Window frame specifications
 */
public class PrestoDialect extends Dialect {
    /**
     * Creates a new Presto dialect instance.
     */
    public PrestoDialect() {
        super("PRESTO", NormalizationStrategy.UPPERCASE);
    }

    @Override
    public Tokenizer createTokenizer() {
        return new PrestoTokenizer();
    }

    @Override
    public Parser createParser() {
        return new PrestoParser();
    }

    @Override
    public Generator createGenerator(GeneratorConfig config) {
        return new PrestoGenerator(config);
    }
}
