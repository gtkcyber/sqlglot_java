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
package com.gtkcyber.sqlglot.dialects.hive;

import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.parser.ParserConfig;

/**
 * Apache Hive parser.
 * Extends base parser with Hive-specific syntax:
 * - LATERAL VIEW for array explosion
 * - Complex data types: STRUCT, ARRAY, MAP
 * - SKEWED tables
 * - CLUSTERED BY and DISTRIBUTED BY
 * - Multiple storage formats (ORC, Parquet, etc.)
 * - Table properties and SerDe specifications
 * - Partition specifications
 * - UNION and UNION ALL
 */
public class HiveParser extends Parser {
    /**
     * Creates a new Apache Hive parser with default configuration.
     */
    public HiveParser() {
        this(ParserConfig.defaultConfig());
    }

    /**
     * Creates a new Apache Hive parser with custom configuration.
     *
     * @param config Parser configuration options
     */
    public HiveParser(ParserConfig config) {
        super(config);
    }
}
