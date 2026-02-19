/*
 * MIT License
 *
 * Copyright (c) 2026 GTK Cyber
 */
package io.sqlglot.dialects.bigquery;

import io.sqlglot.dialect.*;
import io.sqlglot.generator.Generator;
import io.sqlglot.generator.GeneratorConfig;
import io.sqlglot.parser.Parser;
import io.sqlglot.tokens.Tokenizer;

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
