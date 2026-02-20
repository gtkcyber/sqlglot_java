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
package com.gtkcyber.sqlglot.dialects.starrocks;

import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.parser.ParserConfig;

/**
 * StarRocks parser.
 * Extends base parser with StarRocks-specific syntax:
 * - Table model specifications (DUPLICATE KEY, PRIMARY KEY, UNIQUE KEY, AGGREGATE KEY)
 * - Bitmap index definitions and usage
 * - HyperLogLog (HLL) data type and operations
 * - DISTRIBUTED BY specifications with hash/range partitioning
 * - PARTITION BY clauses with range/list/hash partitioning
 * - BUCKET specifications with hash distribution
 * - AGGREGATE functions with aggregation type specifications
 * - ROLLUP definitions for materialized aggregations
 * - Complex data types (ARRAY, STRUCT, MAP) with nested structures
 * - Colocate group specifications
 * - Replication factor and placement specifications
 * - Dynamic schema evolution
 * - Column compression and encoding specifications
 * - Properties and settings for table behavior
 * - Primary key constraints with NOT NULL requirement
 */
public class StarrocksParser extends Parser {
    /**
     * Creates a new StarRocks parser with default configuration.
     */
    public StarrocksParser() {
        this(ParserConfig.defaultConfig());
    }

    /**
     * Creates a new StarRocks parser with custom configuration.
     *
     * @param config Parser configuration options
     */
    public StarrocksParser(ParserConfig config) {
        super(config);
    }
}
