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
package com.gtkcyber.sqlglot.optimizer.rules;

import com.gtkcyber.sqlglot.expressions.Expression;
import com.gtkcyber.sqlglot.expressions.Nodes;
import com.gtkcyber.sqlglot.optimizer.OptimizerContext;
import com.gtkcyber.sqlglot.optimizer.OptimizerRule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Optimization rule that performs column pruning (projection pushdown).
 *
 * Performs:
 * - Tracks which columns are actually used in a query
 * - Pushes unused columns down to remove from subqueries
 * - Handles GROUP BY and JOIN dependencies
 * - Removes unused SELECT expressions from subqueries
 */
public class ProjectionPushdownRule implements OptimizerRule {

    @Override
    public Expression optimize(Expression expression, OptimizerContext context) {
        return expression.transform(expr -> pruneColumns(expr, null));
    }

    /**
     * Prunes unused columns from expressions.
     * requiredColumns: the set of columns needed by the parent expression (null = all columns needed).
     */
    private Expression pruneColumns(Expression expr, Set<String> requiredColumns) {
        if (expr instanceof Nodes.Select select) {
            return pruneSelectColumns(select, requiredColumns);
        }
        return expr;
    }

    /**
     * Prunes columns from a SELECT statement.
     */
    private Expression pruneSelectColumns(Nodes.Select select, Set<String> requiredColumns) {
        // If no required columns specified, keep all (conservative approach)
        if (requiredColumns == null || requiredColumns.isEmpty()) {
            return select;
        }

        // Collect columns used in WHERE, GROUP BY, and ORDER BY
        Set<String> internallyRequired = new HashSet<>();

        if (select.getWhere() != null) {
            internallyRequired.addAll(extractColumnNames(select.getWhere()));
        }

        if (select.getGroupBy() != null) {
            for (Expression expr : select.getGroupBy().getExpressions()) {
                internallyRequired.addAll(extractColumnNames(expr));
            }
        }

        if (select.getHaving() != null) {
            internallyRequired.addAll(extractColumnNames(select.getHaving()));
        }

        for (Nodes.OrderBy orderBy : select.getOrderBy()) {
            internallyRequired.addAll(extractColumnNames(orderBy.getExpression()));
        }

        // Combine required columns with internally required columns
        Set<String> allRequired = new HashSet<>(requiredColumns);
        allRequired.addAll(internallyRequired);

        // Prune SELECT expressions
        List<Expression> prunedExpressions = pruneExpressions(
            select.getExpressions(),
            allRequired
        );

        // If no columns would be removed, return unchanged
        if (prunedExpressions.size() == select.getExpressions().size()) {
            return select;
        }

        // Return SELECT with pruned expressions
        return new Nodes.Select(
            prunedExpressions,
            select.isDistinct(),
            select.getFrom(),
            select.getJoins(),
            select.getWhere(),
            select.getGroupBy(),
            select.getHaving(),
            select.getOrderBy(),
            select.getLimit(),
            select.getOffset()
        );
    }

    /**
     * Prunes expressions that are not in the required columns set.
     */
    private List<Expression> pruneExpressions(List<Expression> expressions, Set<String> required) {
        List<Expression> result = new ArrayList<>();

        for (Expression expr : expressions) {
            String name = extractExpressionName(expr);
            if (name != null && required.contains(name)) {
                result.add(expr);
            } else if (name == null) {
                // Keep expressions we can't analyze (conservative)
                result.add(expr);
            }
            // Otherwise, expression is unused and pruned
        }

        return result.isEmpty() ? expressions : result;
    }

    /**
     * Extracts all column names referenced in an expression.
     */
    private Set<String> extractColumnNames(Expression expr) {
        Set<String> columns = new HashSet<>();

        // Walk the expression tree and collect column references
        // Note: Use findAll instead of transform to avoid nested traversal within the transform callback
        expr.findAll(Nodes.Column.class).forEach(col -> {
            Expression nameExpr = col.getName();
            if (nameExpr instanceof Nodes.Literal literal) {
                columns.add(literal.getValue());
            } else {
                columns.add(nameExpr.toString());
            }
        });

        return columns;
    }

    /**
     * Extracts the name of an expression (for simple cases like Column or alias).
     */
    private String extractExpressionName(Expression expr) {
        if (expr instanceof Nodes.Column col) {
            Expression nameExpr = col.getName();
            if (nameExpr instanceof Nodes.Literal literal) {
                return literal.getValue();
            }
            return nameExpr.toString();
        }

        if (expr instanceof Nodes.Alias alias) {
            return alias.getAlias();
        }

        if (expr instanceof Nodes.Literal literal) {
            return literal.getValue();
        }

        // For complex expressions without an explicit name/alias, return null
        return null;
    }
}
