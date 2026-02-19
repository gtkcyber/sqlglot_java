/*
 * MIT License
 *
 * Copyright (c) 2026 GTK Cyber
 */
package io.sqlglot.dialects.bigquery;

import io.sqlglot.parser.Parser;
import io.sqlglot.parser.ParserConfig;

/**
 * BigQuery parser.
 * Extends base parser with BigQuery-specific syntax:
 * - EXCEPT/REPLACE in SELECT
 * - DATE_TRUNC functions
 * - ARRAY_AGG and other analytics functions
 * - Project-qualified identifiers
 */
public class BigQueryParser extends Parser {
    public BigQueryParser() {
        this(ParserConfig.defaultConfig());
    }

    public BigQueryParser(ParserConfig config) {
        super(config);
    }
}
