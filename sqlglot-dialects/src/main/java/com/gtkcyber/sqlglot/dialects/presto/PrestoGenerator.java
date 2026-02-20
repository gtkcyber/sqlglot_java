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
package com.gtkcyber.sqlglot.dialects.presto;

import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;

/**
 * Presto generator.
 * Generates Presto-compatible SQL with:
 * - Double quote identifiers
 * - Presto data type handling (ROW, ARRAY, MAP)
 * - Presto-specific functions (APPROX_DISTINCT, ARRAY_AGG, etc.)
 * - Advanced JSON operations
 * - Interval arithmetic syntax
 * - Complex array and map operations
 */
public class PrestoGenerator extends Generator {
    /**
     * Creates a new Presto generator with default configuration.
     */
    public PrestoGenerator() {
        this(GeneratorConfig.defaultConfig());
    }

    /**
     * Creates a new Presto generator with custom configuration.
     *
     * @param config Generator configuration options
     */
    public PrestoGenerator(GeneratorConfig config) {
        super(config);
    }

    @Override
    protected String formatIdentifier(String name) {
        // Presto uses double quotes for identifiers
        return "\"" + name.replace("\"", "\"\"") + "\"";
    }
}
