/*
 * MIT License
 *
 * Copyright (c) 2026 GTK Cyber
 */
package io.sqlglot.dialects.snowflake;

import io.sqlglot.generator.Generator;
import io.sqlglot.generator.GeneratorConfig;

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
