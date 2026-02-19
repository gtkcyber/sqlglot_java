/*
 * MIT License
 *
 * Copyright (c) 2026 GTK Cyber
 */
package io.sqlglot.dialects.bigquery;

import io.sqlglot.tokens.Tokenizer;

/**
 * BigQuery tokenizer.
 * Extends base tokenizer with BigQuery-specific features:
 * - Project ID qualified tables: project.dataset.table
 * - Backtick identifiers
 * - Standard SQL compliance
 */
public class BigQueryTokenizer extends Tokenizer {
    public BigQueryTokenizer() {
        super();
    }
}
