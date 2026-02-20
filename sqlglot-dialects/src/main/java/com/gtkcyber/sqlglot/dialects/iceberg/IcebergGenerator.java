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

import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;

/**
 * Iceberg generator.
 * Generates Iceberg-compatible SQL with:
 * - Double quote identifiers for SQL compatibility
 * - SNAPSHOT and VERSION specifications for time travel
 * - FOR SYSTEM_TIME AS OF syntax for historical queries
 * - HISTORY retrieval for version tracking
 * - ROLLBACK operations for version recovery
 * - COMMIT semantics for atomic operations
 * - MERGE INTO for upsert statements
 * - REPLACE operations for table replacement
 * - REWRITE_DATA_FILES for maintenance tasks
 * - Snapshot expiration and orphan file cleanup syntax
 * - Partition evolution syntax
 * - Schema evolution with type coercion
 * - Branching and tagging specifications
 * - Column masking and access control
 * - Advanced partitioning transformation expressions
 * - Metadata table queries (snapshots, manifests, etc.)
 */
public class IcebergGenerator extends Generator {
    /**
     * Creates a new Iceberg generator with default configuration.
     */
    public IcebergGenerator() {
        this(GeneratorConfig.defaultConfig());
    }

    /**
     * Creates a new Iceberg generator with custom configuration.
     *
     * @param config Generator configuration options
     */
    public IcebergGenerator(GeneratorConfig config) {
        super(config);
    }

    @Override
    protected String formatIdentifier(String name) {
        // Iceberg uses double quotes for identifiers (SQL standard)
        if (name == null || name.isEmpty()) {
            return name;
        }
        // Always quote identifiers for safety and SQL compatibility
        return "\"" + name.replace("\"", "\"\"") + "\"";
    }
}
