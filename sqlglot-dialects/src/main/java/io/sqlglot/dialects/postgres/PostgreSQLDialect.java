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
package io.sqlglot.dialects.postgres;

import io.sqlglot.dialect.*;
import io.sqlglot.generator.Generator;
import io.sqlglot.generator.GeneratorConfig;
import io.sqlglot.parser.Parser;
import io.sqlglot.tokens.Tokenizer;

/**
 * PostgreSQL SQL dialect.
 *
 * PostgreSQL is a powerful, open source object-relational database system.
 * Key features:
 * - Full ACID compliance
 * - Advanced data types (arrays, JSON, ranges)
 * - Extensible type system
 * - Procedural languages (PL/pgSQL, PL/Python, etc.)
 * - Window functions, CTEs, and advanced query features
 * - Schema support
 * - Row-level security
 */
public class PostgreSQLDialect extends Dialect {
    public PostgreSQLDialect() {
        super("POSTGRES", NormalizationStrategy.UPPERCASE);
    }

    @Override
    public Tokenizer createTokenizer() {
        return new PostgreSQLTokenizer();
    }

    @Override
    public Parser createParser() {
        return new PostgreSQLParser();
    }

    @Override
    public Generator createGenerator(GeneratorConfig config) {
        return new PostgreSQLGenerator(config);
    }
}
