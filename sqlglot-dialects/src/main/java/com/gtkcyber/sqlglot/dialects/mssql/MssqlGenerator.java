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

import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;

/**
 * SQL Server generator.
 * Generates T-SQL compatible SQL with:
 * - Square bracket identifiers
 * - SQL Server data type handling
 * - SQL Server-specific functions (ISNULL, CONVERT, etc.)
 * - IDENTITY keyword for auto-increment
 * - OUTPUT clause support
 */
public class MssqlGenerator extends Generator {
    /**
     * Creates a new SQL Server generator with default configuration.
     */
    public MssqlGenerator() {
        this(GeneratorConfig.defaultConfig());
    }

    /**
     * Creates a new SQL Server generator with custom configuration.
     *
     * @param config Generator configuration options
     */
    public MssqlGenerator(GeneratorConfig config) {
        super(config);
    }

    @Override
    protected String formatIdentifier(String name) {
        // SQL Server uses square brackets for identifiers
        return "[" + name.replace("]", "]]") + "]";
    }
}
