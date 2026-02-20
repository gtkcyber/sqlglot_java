/*
 * MIT License
 *
 * Copyright (c) 2026 GTK Cyber
 */
package com.gtkcyber.sqlglot.dialects.snowflake;

import com.gtkcyber.sqlglot.dialect.*;
import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;
import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.tokens.Tokenizer;

/**
 * Snowflake SQL dialect.
 *
 * Snowflake is a cloud-native data platform built for data warehousing,
 * data lakes, and data sharing.
 * Key features:
 * - Separation of compute and storage
 * - Time travel and zero-copy cloning
 * - Scalable performance
 * - Semi-structured data support (JSON, Parquet, Avro)
 * - Programmable features (UDFs, stored procedures)
 * - Data sharing
 * - QUALIFY clause
 * - PIVOT and UNPIVOT
 */
public class SnowflakeDialect extends Dialect {
    public SnowflakeDialect() {
        super("SNOWFLAKE", NormalizationStrategy.UPPERCASE);
    }

    @Override
    public Tokenizer createTokenizer() {
        return new SnowflakeTokenizer();
    }

    @Override
    public Parser createParser() {
        return new SnowflakeParser();
    }

    @Override
    public Generator createGenerator(GeneratorConfig config) {
        return new SnowflakeGenerator(config);
    }
}
