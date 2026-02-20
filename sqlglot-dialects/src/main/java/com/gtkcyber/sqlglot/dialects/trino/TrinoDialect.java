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

import com.gtkcyber.sqlglot.dialect.*;
import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;
import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.tokens.Tokenizer;

/**
 * Trino SQL dialect (formerly Presto SQL).
 *
 * Trino is a distributed SQL query engine for interactive analytics across multiple data sources.
 * Key characteristics:
 * - Double quote identifiers (primary) with backtick alternative
 * - Complex data types: ROW, ARRAY, MAP
 * - Advanced JSON operations
 * - Interval arithmetic with granular precision
 * - UUID support and operations
 * - APPROX_DISTINCT for approximate aggregations
 * - Multiple connector support (Hive, MySQL, PostgreSQL, MongoDB, etc.)
 * - Advanced window functions and frame specifications
 * - Type casting with TRY_CAST for safe conversions
 * - Lambda expressions and higher-order functions
 * - Advanced array and map manipulation
 * - GROUPING SETS, CUBE, ROLLUP for advanced grouping
 * - Recursive CTEs
 */
public class TrinoDialect extends Dialect {
    /**
     * Creates a new Trino dialect instance.
     */
    public TrinoDialect() {
        super("TRINO", NormalizationStrategy.UPPERCASE);
    }

    @Override
    public Tokenizer createTokenizer() {
        return new TrinoTokenizer();
    }

    @Override
    public Parser createParser() {
        return new TrinoParser();
    }

    @Override
    public Generator createGenerator(GeneratorConfig config) {
        return new TrinoGenerator(config);
    }
}
