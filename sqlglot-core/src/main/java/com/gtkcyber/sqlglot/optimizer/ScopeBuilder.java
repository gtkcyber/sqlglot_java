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

import java.util.*;

/**
 * Factory for building scope trees from SQL expressions.
 * Extracts FROM, JOIN, CTE, and subquery information to build a scope hierarchy.
 */
public class ScopeBuilder {
    private ScopeBuilder() { /* utility class */ }

    /**
     * Builds a scope tree from a SQL expression.
     * The root scope is always the main SELECT statement.
     *
     * @param expression the SQL expression to analyze
     * @return the root scope of the scope tree
     */
    public static Scope build(Expression expression) {
        return build(expression, null);
    }

    private static Scope build(Expression expression, Scope parent) {
        if (expression instanceof Nodes.With with) {
            return buildWithScope(with, parent);
        } else if (expression instanceof Nodes.Select select) {
            return buildSelectScope(select, parent);
        } else {
            // For non-SELECT expressions, create a simple scope
            Scope scope = new Scope(ScopeType.ROOT, expression, parent);
            return scope;
        }
    }

    private static Scope buildWithScope(Nodes.With with, Scope parent) {
        Scope scope = new Scope(ScopeType.ROOT, with, parent);

        // Extract CTEs from WITH clause
        if (with.getCtes() != null) {
            extractCTEs(with.getCtes(), scope);
        }

        // Extract sources from the main SELECT
        Nodes.Select select = with.getSelect();
        if (select.getFrom() != null) {
            extractFromSources(select.getFrom(), scope);
        }

        // Extract sources from JOIN clauses
        if (select.getJoins() != null && !select.getJoins().isEmpty()) {
            extractJoinSources(select.getJoins(), scope);
        }

        // Count CTE references
        countCTEReferences(select, scope);

        return scope;
    }

    private static Scope buildSelectScope(Nodes.Select select, Scope parent) {
        Scope scope = new Scope(ScopeType.ROOT, select, parent);

        // Extract sources from FROM clause
        if (select.getFrom() != null) {
            extractFromSources(select.getFrom(), scope);
        }

        // Extract sources from JOIN clauses
        if (select.getJoins() != null && !select.getJoins().isEmpty()) {
            extractJoinSources(select.getJoins(), scope);
        }

        return scope;
    }

    private static void extractCTEs(List<Nodes.CTE> ctes, Scope scope) {
        for (Nodes.CTE cte : ctes) {
            String cteName = cte.getName();
            if (cteName != null) {
                // Create a scope for the CTE's SELECT
                Nodes.Select cteSelect = cte.getSelect();
                Scope cteScope = new Scope(ScopeType.CTE, cteSelect, scope);

                // Add CTE as a ScopeSource
                Source cteSource = new Source.ScopeSource(cteName, cteScope);
                scope.addSource(cteName, cteSource);

                // Initialize reference count to 0
                scope.setCTERefCount(cteName, 0);
                scope.addChild(cteScope);
            }
        }
    }

    private static void extractFromSources(Nodes.From fromClause, Scope scope) {
        Expression expression = fromClause.getTable();
        extractTableSource(expression, scope);
    }

    private static void extractTableSource(Expression expression, Scope scope) {
        if (expression instanceof Nodes.Table table) {
            // Table has name and schema, no alias
            String tableName = table.getName();
            String sourceAlias = tableName;

            // Only add as TableSource if not already defined (e.g., as a CTE)
            if (sourceAlias != null && !scope.getSource(sourceAlias).isPresent()) {
                Source source = new Source.TableSource(sourceAlias, tableName);
                scope.addSource(sourceAlias, source);
            }
        } else if (expression instanceof Nodes.Subquery subquery) {
            // Subquery doesn't have an alias directly - it would be wrapped in an Alias
            Scope subqueryScope = build(subquery.getSelect(), scope);
            subqueryScope = new Scope(ScopeType.SUBQUERY, subquery.getSelect(), scope);
            // Note: The alias for this subquery should come from a wrapping Alias node
            scope.addChild(subqueryScope);
        } else if (expression instanceof Nodes.Alias aliasExpr) {
            String aliasName = aliasExpr.getAlias();
            Expression aliasedExpr = aliasExpr.getExpression();

            if (aliasedExpr instanceof Nodes.Subquery subquery) {
                Scope subqueryScope = build(subquery.getSelect(), scope);
                subqueryScope = new Scope(ScopeType.SUBQUERY, subquery.getSelect(), scope);
                Source source = new Source.ScopeSource(aliasName, subqueryScope);
                scope.addSource(aliasName, source);
                scope.addChild(subqueryScope);
            } else if (aliasedExpr instanceof Nodes.Table table) {
                // Table with explicit alias
                Source source = new Source.TableSource(aliasName, table.getName());
                scope.addSource(aliasName, source);
            } else {
                extractTableSource(aliasedExpr, scope);
            }
        }
    }

    private static void extractJoinSources(List<Nodes.Join> joins, Scope scope) {
        for (Nodes.Join join : joins) {
            Expression expression = join.getTable();
            extractTableSource(expression, scope);
        }
    }

    private static void countCTEReferences(Nodes.Select select, Scope scope) {
        // Find all table references in FROM/JOIN to CTEs
        select.findAll(Nodes.Table.class).forEach(table -> {
            // Table.getName() returns a String directly
            String tableName = table.getName();
            if (tableName != null) {
                Optional<Source> source = scope.getSource(tableName);
                if (source.isPresent() && source.get() instanceof Source.ScopeSource) {
                    scope.incrementCTERefCount(tableName);
                }
            }
        });
    }

    /**
     * Extracts the name from a Table reference
     */
    private static String getTableName(Expression expr) {
        if (expr == null) {
            return null;
        }
        if (expr instanceof Nodes.Identifier id) {
            return id.getName();
        }
        return null;
    }

    /**
     * Extracts the name from an Identifier or string
     */
    private static String getIdentifierName(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Nodes.Identifier id) {
            return id.getName();
        }
        if (obj instanceof String str) {
            return str;
        }
        return null;
    }
}
