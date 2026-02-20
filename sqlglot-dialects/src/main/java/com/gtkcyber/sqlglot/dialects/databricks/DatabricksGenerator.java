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
package com.gtkcyber.sqlglot.dialects.databricks;

import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;

/**
 * Databricks generator.
 * Generates Databricks-compatible SQL with:
 * - Backtick identifiers for identifiers requiring quoting
 * - Delta Lake table operations and MERGE statements
 * - COPY INTO syntax for data loading
 * - Python/Scala UDF support with proper escaping
 * - Unity Catalog three-part naming conventions
 * - Complex data type handling (ARRAY, STRUCT, MAP)
 * - Advanced partitioning and clustering syntax
 * - Liquid clustering specifications
 * - OPTIMIZE and VACUUM operations
 * - Table properties and location specifications
 */
public class DatabricksGenerator extends Generator {
    /**
     * Creates a new Databricks generator with default configuration.
     */
    public DatabricksGenerator() {
        this(GeneratorConfig.defaultConfig());
    }

    /**
     * Creates a new Databricks generator with custom configuration.
     *
     * @param config Generator configuration options
     */
    public DatabricksGenerator(GeneratorConfig config) {
        super(config);
    }

    @Override
    protected String formatIdentifier(String name) {
        // Databricks uses backticks for identifiers
        if (name == null || name.isEmpty()) {
            return name;
        }
        // Check if identifier needs quoting (contains special characters, spaces, or is a reserved word)
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
