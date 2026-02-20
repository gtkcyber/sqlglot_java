/*
 * MIT License
 *
 * Copyright (c) 2026 GTK Cyber
 */
package com.gtkcyber.sqlglot.dialects.bigquery;

import com.gtkcyber.sqlglot.dialect.*;
import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;
import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.tokens.Tokenizer;

/**
 * BigQuery SQL dialect.
 *
 * BigQuery is Google Cloud's fully managed, serverless data warehouse.
 * Key features:
 * - Petabyte-scale data analytics
 * - Machine learning integration
 * - Standard SQL support
 * - Nested and repeated fields
 * - ARRAY and STRUCT data types
 * - Project-qualified tables
 * - Partitioning and clustering
 */
public class BigQueryDialect extends Dialect {
    public BigQueryDialect() {
        super("BIGQUERY", NormalizationStrategy.UPPERCASE);
    }

    @Override
    public Tokenizer createTokenizer() {
        return new BigQueryTokenizer();
    }

    @Override
    public Parser createParser() {
        return new BigQueryParser();
    }

    @Override
    public Generator createGenerator(GeneratorConfig config) {
        return new BigQueryGenerator(config);
    }
}
