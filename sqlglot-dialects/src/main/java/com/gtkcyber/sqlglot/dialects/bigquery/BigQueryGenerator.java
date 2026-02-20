/*
 * MIT License
 *
 * Copyright (c) 2026 GTK Cyber
 */
package com.gtkcyber.sqlglot.dialects.bigquery;

import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;

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
