/*
 * MIT License
 *
 * Copyright (c) 2026 GTK Cyber
 */
package com.gtkcyber.sqlglot.dialects.snowflake;

import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;

/**
 * Snowflake generator.
 * Generates Snowflake-compatible SQL with:
 * - Double-quote identifiers
 * - Snowflake-specific data types
 * - Snowflake functions and operators
 * - Semi-structured data handling
 */
public class SnowflakeGenerator extends Generator {
    public SnowflakeGenerator() {
        this(GeneratorConfig.defaultConfig());
    }

    public SnowflakeGenerator(GeneratorConfig config) {
        super(config);
    }
}
