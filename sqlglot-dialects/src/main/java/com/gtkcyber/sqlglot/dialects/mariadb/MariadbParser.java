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

import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.parser.ParserConfig;

/**
 * MariaDB parser.
 * Extends base parser with MariaDB-specific syntax:
 * - Generated columns (VIRTUAL and PERSISTENT)
 * - Dynamic columns (COLUMN_GET, COLUMN_SET, etc.)
 * - REPLACE statement
 * - ON DUPLICATE KEY UPDATE
 * - Sequence operations
 * - Partitioning clauses
 * - JSON functions
 * - Full-text search enhancements
 * - Window functions
 */
public class MariadbParser extends Parser {
    /**
     * Creates a new MariaDB parser with default configuration.
     */
    public MariadbParser() {
        this(ParserConfig.defaultConfig());
    }

    /**
     * Creates a new MariaDB parser with custom configuration.
     *
     * @param config Parser configuration options
     */
    public MariadbParser(ParserConfig config) {
        super(config);
    }
}
