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
package com.gtkcyber.sqlglot.dialects.mssql;

import com.gtkcyber.sqlglot.dialect.*;
import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;
import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.tokens.Tokenizer;

/**
 * T-SQL / Microsoft SQL Server dialect.
 *
 * SQL Server is an enterprise-grade relational database management system.
 * Key characteristics:
 * - Square bracket identifiers [column_name]
 * - Variable syntax: @variable_name
 * - XML data type and XPath functions
 * - Common Table Expressions (CTEs)
 * - Window functions
 * - Stored procedures and triggers
 * - T-SQL control flow: IF, WHILE, CASE
 * - Transact-SQL functions: ISNULL, GETDATE, LEN, SUBSTRING, etc.
 * - IDENTITY keyword for auto-increment
 * - CAST and CONVERT for type conversion
 */
public class MssqlDialect extends Dialect {
    /**
     * Creates a new SQL Server dialect instance.
     */
    public MssqlDialect() {
        super("MSSQL", NormalizationStrategy.UPPERCASE);
    }

    @Override
    public Tokenizer createTokenizer() {
        return new MssqlTokenizer();
    }

    @Override
    public Parser createParser() {
        return new MssqlParser();
    }

    @Override
    public Generator createGenerator(GeneratorConfig config) {
        return new MssqlGenerator(config);
    }
}
