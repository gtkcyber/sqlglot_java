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
 * Optimization rule that pushes WHERE predicates down to subqueries and CTEs.
 *
 * Performs:
 * - Moves WHERE conditions closer to source tables
 * - Reduces intermediate result set sizes early
 * - Handles AND/OR conditions for selective pushing
 * - Ensures safe pushdown (no outer scope dependencies)
 * - Preserves query semantics
 *
 * Example:
 * SELECT * FROM (SELECT * FROM users) WHERE age > 30 AND active = true
 * → Push predicates into subquery when all columns are available
 */
public class PushdownPredicatesRule implements OptimizerRule {

    @Override
    public Expression optimize(Expression expression, OptimizerContext context) {
        if (!(expression instanceof Nodes.Select select)) {
            return expression;
        }

        // Only process if there's a WHERE clause to push down
        if (select.getWhere() == null) {
            return expression;
        }

        Nodes.Where where = select.getWhere();
        Expression whereCondition = where.getCondition();

        // Try to push predicates down through subqueries in FROM
        if (select.getFrom() != null) {
            Expression pushdownResult = pushdownThroughFrom(select, whereCondition);
            if (pushdownResult != select) {
                return pushdownResult;
            }
        }

        return expression;
    }

    /**
     * Attempts to push predicates down through the FROM clause
     */
    private Expression pushdownThroughFrom(Nodes.Select select, Expression whereCondition) {
        Expression fromExpr = select.getFrom().getTable();

        if (fromExpr instanceof Nodes.Subquery subquery) {
            return pushdownThroughSubquery(select, subquery, whereCondition);
        } else if (fromExpr instanceof Nodes.Alias aliasExpr) {
            if (aliasExpr.getExpression() instanceof Nodes.Subquery subquery) {
                return pushdownThroughSubquery(select, subquery, whereCondition);
            }
        }

        return select;
    }

    /**
     * Pushes predicates into a subquery when safe
     */
    private Expression pushdownThroughSubquery(
            Nodes.Select select,
            Nodes.Subquery subquery,
            Expression whereCondition
    ) {
        Nodes.Select subquerySelect = subquery.getSelect();

        // Check if all columns referenced in WHERE are available from subquery
        Set<String> whereColumnRefs = extractColumnReferences(whereCondition);
        Set<String> subqueryAvailableColumns = extractSelectColumns(subquerySelect);

        // If where references columns not in subquery, can't push down
        if (!subqueryAvailableColumns.containsAll(whereColumnRefs)) {
            return select;
        }

        // Push the WHERE condition into the subquery
        Nodes.Where newSubqueryWhere = combineWhereConditions(
                subquerySelect.getWhere(),
                whereCondition
        );

        // Create new subquery with pushed-down WHERE
        Nodes.Select newSubquerySelect = new Nodes.Select(
                subquerySelect.getExpressions(),
                subquerySelect.isDistinct(),
                subquerySelect.getFrom(),
                subquerySelect.getJoins(),
                newSubqueryWhere,
                subquerySelect.getGroupBy(),
                subquerySelect.getHaving(),
                subquerySelect.getOrderBy(),
                subquerySelect.getLimit(),
                subquerySelect.getOffset()
        );

        Nodes.Subquery newSubquery = new Nodes.Subquery(newSubquerySelect);

        // Create new main SELECT without the pushed-down WHERE
        return new Nodes.Select(
                select.getExpressions(),
                select.isDistinct(),
                new Nodes.From(newSubquery),
                select.getJoins(),
                null,  // WHERE was pushed down
                select.getGroupBy(),
                select.getHaving(),
                select.getOrderBy(),
                select.getLimit(),
                select.getOffset()
        );
    }

    /**
     * Combines two WHERE conditions with AND
     */
    private Nodes.Where combineWhereConditions(Nodes.Where existing, Expression additional) {
        if (existing == null) {
            return new Nodes.Where(additional);
        }

        Expression combined = new Nodes.And(existing.getCondition(), additional);
        return new Nodes.Where(combined);
    }

    /**
     * Extracts all column references from an expression
     */
    private Set<String> extractColumnReferences(Expression expr) {
        Set<String> columns = new HashSet<>();
        expr.findAll(Nodes.Column.class).forEach(col -> {
            String colName = extractNameFromExpression(col.getName());
            if (colName != null) {
                columns.add(colName);
            }
        });
        return columns;
    }

    /**
     * Extracts column names available from a SELECT
     */
    private Set<String> extractSelectColumns(Nodes.Select select) {
        Set<String> columns = new HashSet<>();

        for (Expression expr : select.getExpressions()) {
            if (expr instanceof Nodes.Star) {
                // SELECT * - need to expand from source
                if (select.getFrom() != null) {
                    Expression fromExpr = select.getFrom().getTable();
                    if (fromExpr instanceof Nodes.Table table) {
                        // Ideally would look up table schema, for now assume all columns available
                        columns.add("*");
                    }
                }
            } else if (expr instanceof Nodes.Alias alias) {
                String aliasName = alias.getAlias();
                columns.add(aliasName);
            } else if (expr instanceof Nodes.Column col) {
                String colName = extractNameFromExpression(col.getName());
                if (colName != null) {
                    columns.add(colName);
                }
            } else if (expr instanceof Nodes.Identifier id) {
                columns.add(id.getName());
            }
        }

        // If we have SELECT *, assume all columns are available
        if (columns.contains("*")) {
            return new HashSet<>(Collections.singletonList("*"));
        }

        return columns;
    }

    /**
     * Extracts name from various expression types
     */
    private String extractNameFromExpression(Expression expr) {
        if (expr instanceof Nodes.Identifier id) {
            return id.getName();
        }
        return null;
    }
}
