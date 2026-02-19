/*
 * MIT License
 *
 * Copyright (c) 2026 GTK Cyber
 */
package io.sqlglot.dialects.bigquery;

import io.sqlglot.generator.Generator;
import io.sqlglot.generator.GeneratorConfig;

/**
 * BigQuery generator.
 * Generates BigQuery-compatible SQL with:
 * - Project-qualified identifiers (project.dataset.table)
 * - Backtick escaping for identifiers
 * - BigQuery data types and functions
 */
public class BigQueryGenerator extends Generator {
    public BigQueryGenerator() {
        this(GeneratorConfig.defaultConfig());
    }

    public BigQueryGenerator(GeneratorConfig config) {
        super(config);
    }
}
