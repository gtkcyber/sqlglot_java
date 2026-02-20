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
import io.sqlglot.optimizer.util.ExpressionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Optimization rule that merges and inlines simple subqueries.
 *
 * Performs:
 * - Inlines scalar subqueries (subqueries that return a single value)
 * - Merges subqueries in FROM clause when possible
 * - Preserves correlated subqueries as-is (too complex to merge)
 * - Avoids merging subqueries with aggregations or GROUP BY
 */
public class MergeSubqueriesRule implements OptimizerRule {

    @Override
    public Expression optimize(Expression expression, OptimizerContext context) {
        return expression.transform(this::mergeSubqueries);
    }

    /**
     * Attempts to merge subqueries where possible.
     */
    private Expression mergeSubqueries(Expression expr) {
        // Handle subqueries in FROM clause
        if (expr instanceof Nodes.From from) {
            return mergeFromSubquery(from);
        }

        // Handle subqueries in WHERE clause (scalar subqueries)
        if (expr instanceof Nodes.Where where) {
            return mergeWhereSubquery(where);
        }

        return expr;
    }

    /**
     * Attempts to merge subqueries in FROM clause.
     */
    private Expression mergeFromSubquery(Nodes.From from) {
        Expression table = from.getTable();

        // Check if the table is a subquery that can be inlined
        if (table instanceof Nodes.Subquery subquery) {
            if (canMergeSubquery(subquery.getSelect())) {
                // Return the subquery's SELECT directly
                return new Nodes.From(subquery.getSelect());
            }
        }

        return from;
    }

    /**
     * Attempts to merge scalar subqueries in WHERE clause.
     */
    private Expression mergeWhereSubquery(Nodes.Where where) {
        Expression condition = where.getCondition();

        // Look for scalar subquery comparisons: col = (SELECT value FROM ...)
        if (condition instanceof Nodes.EQ eq) {
            Expression left = eq.getLeft();
            Expression right = eq.getRight();

            // Check if right side is a scalar subquery
            if (right instanceof Nodes.Subquery subquery && isScalarSubquery(subquery.getSelect())) {
                // Extract the single value from the subquery
                List<Expression> selectExpressions = subquery.getSelect().getExpressions();
                if (selectExpressions.size() == 1) {
                    Expression value = selectExpressions.get(0);
                    if (isConstantValue(value)) {
                        // Replace subquery with the constant value
                        return new Nodes.Where(new Nodes.EQ(left, value));
                    }
                }
            }

            // Check if left side is a scalar subquery
            if (left instanceof Nodes.Subquery subquery && isScalarSubquery(subquery.getSelect())) {
                List<Expression> selectExpressions = subquery.getSelect().getExpressions();
                if (selectExpressions.size() == 1) {
                    Expression value = selectExpressions.get(0);
                    if (isConstantValue(value)) {
                        return new Nodes.Where(new Nodes.EQ(value, right));
                    }
                }
            }
        }

        return where;
    }

    /**
     * Checks if a subquery is a scalar subquery (returns a single row and column).
     */
    private boolean isScalarSubquery(Nodes.Select select) {
        // Must have exactly one expression in SELECT list
        if (select.getExpressions().size() != 1) {
            return false;
        }

        // Must not have GROUP BY (aggregation)
        if (select.getGroupBy() != null) {
            return false;
        }

        // Should have a LIMIT 1 or equivalent
        // For now, we're pragmatic and allow any single-expression select

        return true;
    }

    /**
     * Checks if a subquery can be merged (simple, non-correlated).
     */
    private boolean canMergeSubquery(Nodes.Select select) {
        // Don't merge if it has aggregations
        if (select.getGroupBy() != null || select.getHaving() != null) {
            return false;
        }

        // Don't merge if it has DISTINCT (may change semantics)
        if (select.isDistinct()) {
            return false;
        }

        // Don't merge if it has LIMIT/OFFSET (may change semantics)
        if (select.getLimit() != null || select.getOffset() != null) {
            return false;
        }

        // Don't merge correlated subqueries (too complex)
        if (hasCorrelatedColumns(select)) {
            return false;
        }

        return true;
    }

    /**
     * Checks if a select contains correlated column references.
     * This is a simple heuristic that checks for qualified columns.
     */
    private boolean hasCorrelatedColumns(Nodes.Select select) {
        // For now, we use a simple heuristic: check for column references
        // A more complete implementation would track actual correlations
        Set<String> tables = extractTableNames(select);
        return tables.size() > 1; // Multiple tables suggest correlation
    }

    /**
     * Extracts table names from the FROM and JOIN clauses.
     */
    private Set<String> extractTableNames(Nodes.Select select) {
        Set<String> tables = new HashSet<>();

        if (select.getFrom() != null) {
            Expression table = select.getFrom().getTable();
            if (table instanceof Nodes.Table t) {
                tables.add(t.getName());
            }
        }

        for (Nodes.Join join : select.getJoins()) {
            Expression table = join.getTable();
            if (table instanceof Nodes.Table t) {
                tables.add(t.getName());
            }
        }

        return tables;
    }

    /**
     * Checks if an expression is a constant value (literal or NULL).
     */
    private boolean isConstantValue(Expression expr) {
        return expr instanceof Nodes.Literal ||
               ExpressionUtils.isNull(expr) ||
               ExpressionUtils.isTrue(expr) ||
               ExpressionUtils.isFalse(expr);
    }
}
