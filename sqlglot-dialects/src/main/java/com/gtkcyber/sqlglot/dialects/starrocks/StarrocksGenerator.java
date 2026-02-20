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
package com.gtkcyber.sqlglot.dialects.starrocks;

import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;

/**
 * StarRocks generator.
 * Generates StarRocks-compatible SQL with:
 * - Backtick identifiers for identifiers requiring quoting
 * - Table model specifications (DUPLICATE KEY, PRIMARY KEY, UNIQUE KEY, AGGREGATE KEY)
 * - Bitmap index definitions in CREATE TABLE statements
 * - HyperLogLog data type support for approximate cardinality
 * - DISTRIBUTED BY and PARTITION BY specifications
 * - BUCKET specifications for data distribution
 * - AGGREGATE function declarations with type specifications
 * - ROLLUP materialized view definitions
 * - Complex data type handling (ARRAY, STRUCT, MAP)
 * - Colocate group specifications
 * - Replication and placement configurations
 * - Properties and settings management
 * - Column constraints and default values
 * - Engine specifications
 */
public class StarrocksGenerator extends Generator {
    /**
     * Creates a new StarRocks generator with default configuration.
     */
    public StarrocksGenerator() {
        this(GeneratorConfig.defaultConfig());
    }

    /**
     * Creates a new StarRocks generator with custom configuration.
     *
     * @param config Generator configuration options
     */
    public StarrocksGenerator(GeneratorConfig config) {
        super(config);
    }

    @Override
    protected String formatIdentifier(String name) {
        // StarRocks uses backticks for identifiers (for reserved words and special chars)
        if (name == null || name.isEmpty()) {
            return name;
        }
        // Check if identifier needs quoting
        if (requiresQuoting(name)) {
            return "`" + name.replace("`", "``") + "`";
        }
        return name;
    }

    /**
     * Determines if an identifier requires quoting based on content.
     *
     * @param name The identifier name
     * @return True if the identifier should be quoted
     */
    private boolean requiresQuoting(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        // Require quoting for names with special characters or spaces
        for (char c : name.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '_') {
                return true;
            }
        }
        // Always quote if it starts with a digit
        if (Character.isDigit(name.charAt(0))) {
            return true;
        }
        return false;
    }
}
