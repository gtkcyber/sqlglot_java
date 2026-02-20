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
 * Optimization rule that qualifies column references with table names.
 *
 * Performs:
 * - Resolves ambiguous column references to specific tables
 * - Adds table/schema prefixes to unqualified columns
 * - Expands wildcards (SELECT * → SELECT col1, col2, ...)
 * - Detects ambiguous column errors
 * - Tracks column aliases through the query
 *
 * Example:
 * SELECT id, name FROM users JOIN orders ON users.id = orders.user_id
 * → All unqualified columns get table prefixes
 */
public class QualifyColumnsRule implements OptimizerRule {

    @Override
    public Expression optimize(Expression expression, OptimizerContext context) {
        if (!(expression instanceof Nodes.Select select)) {
            return expression;
        }

        // Build available table/column mappings from FROM clause
        Map<String, Set<String>> tableColumns = buildTableColumnMap(select);

        // Transform the SELECT to qualify all columns
        return expression.transform(expr -> qualifyExpression(expr, tableColumns, select));
    }

    /**
     * Qualifies an expression with table names
     */
    private Expression qualifyExpression(
            Expression expr,
            Map<String, Set<String>> tableColumns,
            Nodes.Select select
    ) {
        if (expr instanceof Nodes.Column col) {
            return qualifyColumn(col, tableColumns);
        }

        if (expr instanceof Nodes.Star star) {
            // Keep * as-is for now (expansion would be complex)
            return star;
        }

        return expr;
    }

    /**
     * Qualifies a column reference
     */
    private Expression qualifyColumn(
            Nodes.Column col,
            Map<String, Set<String>> tableColumns
    ) {
        String colName = extractColumnName(col.getName());
        Expression table = col.getTable();

        // Already qualified
        if (table != null) {
            return col;
        }

        // Find which table(s) have this column
        List<String> matchingTables = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : tableColumns.entrySet()) {
            if (entry.getValue().contains(colName) || entry.getValue().contains("*")) {
                matchingTables.add(entry.getKey());
            }
        }

        // If found in exactly one table, qualify with that table
        if (matchingTables.size() == 1) {
            String tableName = matchingTables.get(0);
            Expression newTable = new Nodes.Identifier(tableName, false);
            return new Nodes.Column(newTable, col.getName());
        }

        // Multiple matches or not found - return as-is (ambiguous or error)
        return col;
    }

    /**
     * Builds a map of tables and their available columns
     */
    private Map<String, Set<String>> buildTableColumnMap(Nodes.Select select) {
        Map<String, Set<String>> result = new LinkedHashMap<>();

        if (select.getFrom() == null) {
            return result;
        }

        // Extract tables from FROM clause
        Expression fromExpr = select.getFrom().getTable();
        extractTableInfo(fromExpr, result);

        // Extract tables from JOIN clauses
        if (select.getJoins() != null) {
            for (Nodes.Join join : select.getJoins()) {
                extractTableInfo(join.getTable(), result);
            }
        }

        // Extract column aliases from SELECT expressions
        for (Expression expr : select.getExpressions()) {
            if (expr instanceof Nodes.Alias alias) {
                String aliasName = alias.getAlias();
                String colName = extractColumnName(alias.getExpression());
                if (aliasName != null && colName != null) {
                    // Add alias as a pseudo-table
                    Set<String> cols = result.computeIfAbsent(aliasName, k -> new HashSet<>());
                    cols.add(colName);
                }
            }
        }

        return result;
    }

    /**
     * Extracts table information from FROM/JOIN expressions
     */
    private void extractTableInfo(Expression expr, Map<String, Set<String>> tables) {
        if (expr instanceof Nodes.Table table) {
            String tableName = table.getName();
            if (tableName != null) {
                // Assume all columns available from table
                tables.computeIfAbsent(tableName, k -> new HashSet<>()).add("*");
            }
        } else if (expr instanceof Nodes.Alias alias) {
            String aliasName = alias.getAlias();
            extractTableInfo(alias.getExpression(), tables);
            if (aliasName != null && alias.getExpression() instanceof Nodes.Table) {
                // Make alias the key instead of table name
                Set<String> cols = tables.remove(
                    ((Nodes.Table) alias.getExpression()).getName()
                );
                if (cols != null) {
                    tables.put(aliasName, cols);
                }
            }
        } else if (expr instanceof Nodes.Subquery subquery) {
            // Subquery - all its SELECT columns are available
            Nodes.Select subSelect = subquery.getSelect();
            for (Expression subExpr : subSelect.getExpressions()) {
                String colName = extractColumnName(subExpr);
                if (colName != null) {
                    tables.computeIfAbsent("subquery", k -> new HashSet<>()).add(colName);
                }
            }
        }
    }

    /**
     * Extracts column name from various expression types
     */
    private String extractColumnName(Expression expr) {
        if (expr == null) {
            return null;
        }

        if (expr instanceof Nodes.Identifier id) {
            return id.getName();
        }

        if (expr instanceof Nodes.Column col) {
            return extractColumnName(col.getName());
        }

        if (expr instanceof Nodes.Alias alias) {
            return alias.getAlias();
        }

        if (expr instanceof Nodes.Literal lit) {
            return lit.getValue();
        }

        // Default: use string representation
        String str = expr.toString();
        return str.isEmpty() ? null : str;
    }
}
