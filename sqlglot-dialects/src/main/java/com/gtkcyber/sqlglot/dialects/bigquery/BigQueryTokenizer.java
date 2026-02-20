/*
 * MIT License
 *
 * Copyright (c) 2026 GTK Cyber
 */
package com.gtkcyber.sqlglot.dialects.bigquery;

import com.gtkcyber.sqlglot.tokens.Tokenizer;

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
