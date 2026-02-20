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
package com.gtkcyber.sqlglot.optimizer;

import com.gtkcyber.sqlglot.dialect.Dialect;

import java.util.Objects;
import java.util.Optional;

/**
 * Context passed to optimizer rules with dialect and schema information.
 *
 * @param dialect the SQL dialect for optimization
 * @param schema optional schema information for analysis
 * @param config optimization configuration with feature flags
 * @param database optional database name for schema qualification
 * @param catalog optional catalog name for schema qualification
 */
public record OptimizerContext(
        Dialect dialect,
        Optional<Schema> schema,
        OptimizerConfig config,
        Optional<String> database,
        Optional<String> catalog
) {
    /**
     * Constructor with validation
     */
    public OptimizerContext {
        Objects.requireNonNull(dialect, "dialect cannot be null");
        Objects.requireNonNull(schema, "schema cannot be null");
        Objects.requireNonNull(config, "config cannot be null");
        Objects.requireNonNull(database, "database cannot be null");
        Objects.requireNonNull(catalog, "catalog cannot be null");
    }

    /**
     * Creates context with dialect and default configuration
     */
    public static OptimizerContext of(Dialect dialect) {
        return new OptimizerContext(
                dialect,
                Optional.empty(),
                OptimizerConfig.DEFAULT,
                Optional.empty(),
                Optional.empty()
        );
    }

    /**
     * Creates context with dialect and custom configuration
     */
    public static OptimizerContext of(Dialect dialect, OptimizerConfig config) {
        return new OptimizerContext(
                dialect,
                Optional.empty(),
                config,
                Optional.empty(),
                Optional.empty()
        );
    }

    /**
     * Creates context with all parameters
     */
    public static OptimizerContext of(
            Dialect dialect,
            Schema schema,
            OptimizerConfig config,
            String database,
            String catalog
    ) {
        return new OptimizerContext(
                dialect,
                Optional.of(schema),
                config,
                Optional.of(database),
                Optional.of(catalog)
        );
    }
}
