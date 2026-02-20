/*
 * MIT License
 *
 * Copyright (c) 2026 GTK Cyber
 */
package com.gtkcyber.sqlglot.dialects.snowflake;

import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.parser.ParserConfig;

/**
 * Snowflake parser.
 * Extends base parser with Snowflake-specific syntax:
 * - Stage references (@stage/path)
 * - QUALIFY clause (QUALIFY ROW_NUMBER() OVER ...)
 * - PIVOT and UNPIVOT
 * - LATERAL FLATTEN
 * - Schema-qualified identifiers with . syntax
 */
public class SnowflakeParser extends Parser {
    public SnowflakeParser() {
        this(ParserConfig.defaultConfig());
    }

    public SnowflakeParser(ParserConfig config) {
        super(config);
    }
}
