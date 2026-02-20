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
package com.gtkcyber.sqlglot.dialects.cockroachdb;

import com.gtkcyber.sqlglot.dialect.*;
import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;
import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.tokens.Tokenizer;

/**
 * CockroachDB SQL dialect.
 *
 * CockroachDB is a distributed SQL database designed for scale and resilience.
 * PostgreSQL-compatible with distributed transaction support.
 * Key features:
 * - Full PostgreSQL compatibility
 * - Distributed transactions
 * - Multi-region support
 * - MVCC concurrency control
 * - Horizontal scaling
 * - Automatic rebalancing
 * - Double-quote identifiers (PostgreSQL-compatible)
 */
public class CockroachdbDialect extends Dialect {
    /**
     * Constructs a CockroachDB dialect instance.
     */
    public CockroachdbDialect() {
        super("COCKROACHDB", NormalizationStrategy.UPPERCASE);
    }

    /**
     * Creates a CockroachDB-specific tokenizer.
     *
     * @return a new CockroachdbTokenizer instance
     */
    @Override
    public Tokenizer createTokenizer() {
        return new CockroachdbTokenizer();
    }

    /**
     * Creates a CockroachDB-specific parser.
     *
     * @return a new CockroachdbParser instance
     */
    @Override
    public Parser createParser() {
        return new CockroachdbParser();
    }

    /**
     * Creates a CockroachDB-specific generator.
     *
     * @param config the generator configuration
     * @return a new CockroachdbGenerator instance
     */
    @Override
    public Generator createGenerator(GeneratorConfig config) {
        if (config == null) {
            config = GeneratorConfig.defaultConfig();
        }
        return new CockroachdbGenerator(config);
    }
}
