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
package com.gtkcyber.sqlglot.dialects.databricks;

import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.parser.ParserConfig;

/**
 * Databricks parser.
 * Extends base parser with Databricks-specific syntax:
 * - MERGE INTO statements for upsert operations
 * - MATCHED/NOT MATCHED clauses with update/delete/insert
 * - Delta Lake specific operations (OPTIMIZE, VACUUM, etc.)
 * - COPY INTO for data ingestion
 * - Python and Scala UDF definitions
 * - Unity Catalog three-part naming (catalog.schema.table)
 * - Advanced data types (ARRAY, STRUCT, MAP)
 * - CLUSTER BY for liquid clustering
 * - LOCATION specifications for external tables
 * - TBLPROPERTIES for table metadata
 * - Complex window functions and array operations
 */
public class DatabricksParser extends Parser {
    /**
     * Creates a new Databricks parser with default configuration.
     */
    public DatabricksParser() {
        this(ParserConfig.defaultConfig());
    }

    /**
     * Creates a new Databricks parser with custom configuration.
     *
     * @param config Parser configuration options
     */
    public DatabricksParser(ParserConfig config) {
        super(config);
    }
}
