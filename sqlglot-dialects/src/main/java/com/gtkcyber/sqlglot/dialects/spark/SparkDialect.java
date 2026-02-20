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
package com.gtkcyber.sqlglot.dialects.spark;

import com.gtkcyber.sqlglot.dialect.*;
import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;
import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.tokens.Tokenizer;

/**
 * Apache Spark SQL dialect.
 *
 * Spark SQL is a distributed SQL query engine for big data processing.
 * Key characteristics:
 * - Backtick identifiers (for reserved words and special characters)
 * - Double quote and backtick support
 * - Delta Lake integration
 * - Distributed query execution
 * - Complex data types: ARRAY, MAP, STRUCT
 * - UDF (User Defined Functions) support
 * - Multi-dialect support (can read MySQL, PostgreSQL, Oracle syntax)
 * - Window functions and CTEs
 * - Advanced aggregations
 * - Machine learning functions (MLlib)
 * - Time travel and versioning (Delta)
 */
public class SparkDialect extends Dialect {
    /**
     * Creates a new Spark SQL dialect instance.
     */
    public SparkDialect() {
        super("SPARK", NormalizationStrategy.UPPERCASE);
    }

    @Override
    public Tokenizer createTokenizer() {
        return new SparkTokenizer();
    }

    @Override
    public Parser createParser() {
        return new SparkParser();
    }

    @Override
    public Generator createGenerator(GeneratorConfig config) {
        return new SparkGenerator(config);
    }
}
