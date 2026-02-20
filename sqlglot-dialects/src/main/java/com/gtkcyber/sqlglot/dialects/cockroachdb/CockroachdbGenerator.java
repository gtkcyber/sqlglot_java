/*
 * MIT License
 *
 * Copyright (c) 2026 GTK Cyber
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.gtkcyber.sqlglot.dialects.cockroachdb;

import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;

/**
 * CockroachDB generator.
 *
 * Generates CockroachDB-compatible SQL with:
 * - Double-quote identifiers for reserved words (PostgreSQL-compatible)
 * - PostgreSQL-compatible data type handling
 * - Distributed transaction support
 * - Standard SQL formatting
 */
public class CockroachdbGenerator extends Generator {
    /**
     * Constructs a CockroachDB generator with default configuration.
     */
    public CockroachdbGenerator() {
        this(GeneratorConfig.defaultConfig());
    }

    /**
     * Constructs a CockroachDB generator with the specified configuration.
     *
     * @param config the generator configuration
     */
    public CockroachdbGenerator(GeneratorConfig config) {
        super(config != null ? config : GeneratorConfig.defaultConfig());
    }
}
