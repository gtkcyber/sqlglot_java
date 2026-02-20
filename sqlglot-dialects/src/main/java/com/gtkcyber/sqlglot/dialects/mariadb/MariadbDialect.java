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
package com.gtkcyber.sqlglot.dialects.mariadb;

import com.gtkcyber.sqlglot.dialect.*;
import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;
import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.tokens.Tokenizer;

/**
 * MariaDB SQL dialect.
 *
 * MariaDB is a popular open-source relational database that is a MySQL fork.
 * Key characteristics:
 * - Backtick identifiers
 * - JSON support with JSON functions
 * - Full-text search
 * - Sequence support
 * - Window functions
 * - Common Table Expressions (CTEs)
 * - Dynamic columns
 * - Virtual and persistent generated columns
 * - InnoDB for ACID transactions
 * - Partitioning
 * - LIMIT offset syntax
 * - REPLACE statement (extension)
 * - INSERT ON DUPLICATE KEY UPDATE
 * - Multiple table syntax variants
 */
public class MariadbDialect extends Dialect {
    /**
     * Creates a new MariaDB dialect instance.
     */
    public MariadbDialect() {
        super("MARIADB", NormalizationStrategy.UPPERCASE);
    }

    @Override
    public Tokenizer createTokenizer() {
        return new MariadbTokenizer();
    }

    @Override
    public Parser createParser() {
        return new MariadbParser();
    }

    @Override
    public Generator createGenerator(GeneratorConfig config) {
        return new MariadbGenerator(config);
    }
}
