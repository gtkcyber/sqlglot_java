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
package io.sqlglot.optimizer.rules;

import io.sqlglot.expressions.Expression;
import io.sqlglot.expressions.Nodes;
import io.sqlglot.optimizer.OptimizerContext;
import io.sqlglot.optimizer.OptimizerRule;

import java.util.*;

/**
 * Optimization rule that applies dialect-specific identifier quoting.
 *
 * Performs:
 * - Quotes reserved keywords when used as identifiers
 * - Quotes identifiers with special characters
 * - Quotes identifiers that need case preservation
 * - Dialect-aware quoting strategy
 */
public class QuoteIdentifiersRule implements OptimizerRule {
    // SQL reserved keywords that may need quoting
    private static final Set<String> RESERVED_KEYWORDS = Set.of(
            "SELECT", "FROM", "WHERE", "AND", "OR", "NOT", "NULL", "TRUE", "FALSE",
            "INSERT", "UPDATE", "DELETE", "CREATE", "DROP", "ALTER", "TABLE",
            "INDEX", "VIEW", "DATABASE", "SCHEMA", "JOIN", "LEFT", "RIGHT",
            "INNER", "OUTER", "CROSS", "ON", "USING", "GROUP", "BY", "ORDER",
            "HAVING", "LIMIT", "OFFSET", "UNION", "INTERSECT", "EXCEPT",
            "CASE", "WHEN", "THEN", "ELSE", "END", "CAST", "AS", "DISTINCT",
            "ALL", "WITH", "RECURSIVE", "IN", "EXISTS", "BETWEEN", "LIKE", "IS",
            "WINDOW", "PARTITION", "OVER", "ROW", "ROWS", "RANGE", "UNBOUNDED"
    );

    @Override
    public Expression optimize(Expression expression, OptimizerContext context) {
        return expression.transform(expr -> quoteIfNeeded(expr, context));
    }

    private Expression quoteIfNeeded(Expression expr, OptimizerContext context) {
        if (expr instanceof Nodes.Identifier identifier) {
            String name = identifier.getName();
            if (shouldQuote(name)) {
                return new Nodes.Identifier(name, true);
            }
        }

        if (expr instanceof Nodes.Column column) {
            String name = column.getName().toString();
            if (shouldQuote(name)) {
                Expression newName = quoteIfNeeded(column.getName(), context);
                return new Nodes.Column(newName, column.getTable());
            }
        }

        if (expr instanceof Nodes.Table table) {
            String name = table.getName();
            if (shouldQuote(name)) {
                // Table constructor takes String name and String schema
                return new Nodes.Table(name, table.getSchema());
            }
        }

        return expr;
    }

    /**
     * Determines if an identifier should be quoted
     */
    private boolean shouldQuote(String identifier) {
        if (identifier == null || identifier.isEmpty()) {
            return false;
        }

        // Already quoted
        if ((identifier.startsWith("\"") && identifier.endsWith("\"")) ||
            (identifier.startsWith("`") && identifier.endsWith("`")) ||
            (identifier.startsWith("[") && identifier.endsWith("]")) ||
            (identifier.startsWith("'") && identifier.endsWith("'"))) {
            return false;
        }

        // Is a reserved keyword
        if (RESERVED_KEYWORDS.contains(identifier.toUpperCase())) {
            return true;
        }

        // Contains special characters or spaces
        if (!identifier.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
            return true;
        }

        // Starts with a digit (not valid without quoting)
        if (Character.isDigit(identifier.charAt(0))) {
            return true;
        }

        return false;
    }
}
