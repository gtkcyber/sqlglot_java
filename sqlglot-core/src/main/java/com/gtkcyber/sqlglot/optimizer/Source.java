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

import com.gtkcyber.sqlglot.expressions.Expression;
import com.gtkcyber.sqlglot.expressions.Nodes;

import java.util.Objects;

/**
 * Represents a data source within a scope (table or subquery).
 * This is a sealed interface to support pattern matching.
 */
public sealed interface Source {
    /**
     * Gets the name/alias of this source
     */
    String getName();

    /**
     * Source from a table
     */
    record TableSource(String name, String qualifiedName) implements Source {
        public TableSource {
            Objects.requireNonNull(name, "name cannot be null");
        }

        @Override
        public String getName() {
            return name;
        }

        public String getQualifiedName() {
            return qualifiedName != null ? qualifiedName : name;
        }
    }

    /**
     * Source from a scope (subquery/CTE/derived table)
     */
    record ScopeSource(String name, Scope scope) implements Source {
        public ScopeSource {
            Objects.requireNonNull(name, "name cannot be null");
            Objects.requireNonNull(scope, "scope cannot be null");
        }

        @Override
        public String getName() {
            return name;
        }

        public Scope getScope() {
            return scope;
        }
    }
}
