/*
 * MIT License
 *
 * Copyright (c) 2026 GTK Cyber
 */
package com.gtkcyber.sqlglot.dialects.snowflake;

import com.gtkcyber.sqlglot.tokens.Tokenizer;

/**
 * Snowflake tokenizer.
 * Extends base tokenizer with Snowflake-specific features:
 * - Double-quote identifiers
 * - Semi-colon terminators
 * - Snowflake functions and operators
 */
public class SnowflakeTokenizer extends Tokenizer {
    public SnowflakeTokenizer() {
        super();
    }
}
