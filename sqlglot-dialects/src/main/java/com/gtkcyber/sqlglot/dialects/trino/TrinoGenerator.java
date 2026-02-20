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
package com.gtkcyber.sqlglot.dialects.trino;

import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;

/**
 * Trino generator.
 * Generates Trino-compatible SQL with:
 * - Double quote identifiers (primary quoting mechanism)
 * - Support for backtick identifiers as alternative
 * - Complex data type handling (ROW, ARRAY, MAP)
 * - Interval arithmetic with precision specifications
 * - UUID and IPADDRESS type operations
 * - Advanced JSON operations with JSONPath expressions
 * - APPROX functions with proper syntax
 * - Lambda expressions and higher-order function syntax
 * - GROUPING SETS, CUBE, ROLLUP syntax
 * - Recursive CTE support
 * - Advanced window function specifications
 * - Multi-catalog naming conventions
 * - TRY_CAST for safe type conversions
 */
public class TrinoGenerator extends Generator {
    /**
     * Creates a new Trino generator with default configuration.
     */
    public TrinoGenerator() {
        this(GeneratorConfig.defaultConfig());
    }

    /**
     * Creates a new Trino generator with custom configuration.
     *
     * @param config Generator configuration options
     */
    public TrinoGenerator(GeneratorConfig config) {
        super(config);
    }

    @Override
    protected String formatIdentifier(String name) {
        // Trino uses double quotes for identifiers (primary)
        if (name == null || name.isEmpty()) {
            return name;
        }
        // Always quote identifiers to be safe
        return "\"" + name.replace("\"", "\"\"") + "\"";
    }
}
