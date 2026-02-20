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

import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.parser.ParserConfig;

/**
 * Iceberg parser.
 * Extends base parser with Iceberg-specific syntax:
 * - SNAPSHOT specifications for versioning
 * - VERSION and SYSTEM_TIME clauses for time travel
 * - FOR SYSTEM_TIME AS OF for point-in-time queries
 * - HISTORY for snapshot history retrieval
 * - ROLLBACK operations to previous versions
 * - COMMIT semantics for atomic writes
 * - MERGE INTO for upsert operations
 * - Partition evolution syntax
 * - Schema evolution with backward compatibility
 * - REPLACE for table replacement
 * - REWRITE_DATA_FILES for maintenance operations
 * - Snapshot expiration and orphan file cleanup
 * - Branching and tagging for version management
 * - Column masking and row-level access control
 * - Advanced partitioning transformations
 */
public class IcebergParser extends Parser {
    /**
     * Creates a new Iceberg parser with default configuration.
     */
    public IcebergParser() {
        this(ParserConfig.defaultConfig());
    }

    /**
     * Creates a new Iceberg parser with custom configuration.
     *
     * @param config Parser configuration options
     */
    public IcebergParser(ParserConfig config) {
        super(config);
    }
}
