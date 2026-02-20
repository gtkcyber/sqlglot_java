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
package com.gtkcyber.sqlglot.dialects.iceberg;

import com.gtkcyber.sqlglot.dialect.*;
import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;
import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.tokens.Tokenizer;

/**
 * Apache Iceberg SQL dialect.
 *
 * Apache Iceberg is an open-source table format for data lakes that provides reliable ACID transactions,
 * version history, and partition evolution.
 * Key characteristics:
 * - Double quote identifiers (primary quoting mechanism)
 * - Snapshot-based versioning with time travel queries
 * - FOR SYSTEM_TIME AS OF for point-in-time queries
 * - Version history tracking and rollback capabilities
 * - Partition evolution without full table rewrite
 * - Schema evolution with backward compatibility
 * - MERGE INTO for upsert operations
 * - Time travel for data recovery and auditing
 * - Snapshot operations (expire, remove orphan files)
 * - Atomic commit protocol
 * - Predicate pushdown and partition elimination
 * - Managed and external table support
 */
public class IcebergDialect extends Dialect {
    /**
     * Creates a new Iceberg dialect instance.
     */
    public IcebergDialect() {
        super("ICEBERG", NormalizationStrategy.UPPERCASE);
    }

    @Override
    public Tokenizer createTokenizer() {
        return new IcebergTokenizer();
    }

    @Override
    public Parser createParser() {
        return new IcebergParser();
    }

    @Override
    public Generator createGenerator(GeneratorConfig config) {
        return new IcebergGenerator(config);
    }
}
