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
import com.gtkcyber.sqlglot.optimizer.Scope;
import com.gtkcyber.sqlglot.optimizer.ScopeBuilder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Optimization rule that eliminates unused CTEs (Common Table Expressions).
 *
 * Performs:
 * - Analysis of CTE reference counts
 * - Removal of CTEs with zero references
 * - Cascading removal of CTEs only used by other unused CTEs
 */
public class EliminateCTEsRule implements OptimizerRule {

    @Override
    public Expression optimize(Expression expression, OptimizerContext context) {
        if (!(expression instanceof Nodes.With)) {
            return expression;
        }

        Nodes.With withExpr = (Nodes.With) expression;
        if (withExpr.getCtes() == null || withExpr.getCtes().isEmpty()) {
            return expression;
        }

        // Build scope to track CTE references
        Scope scope = ScopeBuilder.build(expression);

        // Find unused CTEs
        Set<String> unusedCtes = findUnusedCtes(withExpr, scope);

        // If no unused CTEs, return unchanged
        if (unusedCtes.isEmpty()) {
            return expression;
        }

        // Remove unused CTEs from the WITH clause
        return removeUnusedCtes(withExpr, unusedCtes);
    }

    /**
     * Finds all unused CTEs in the WITH clause
     */
    private Set<String> findUnusedCtes(Nodes.With withExpr, Scope scope) {
        Set<String> unused = new HashSet<>();

        if (withExpr.getCtes() == null) {
            return unused;
        }

        // Get all CTE names and their reference counts
        for (Nodes.CTE cte : withExpr.getCtes()) {
            String cteName = cte.getName();
            if (cteName != null) {
                int refCount = scope.getCTERefCount(cteName);
                if (refCount == 0) {
                    unused.add(cteName);
                }
            }
        }

        // Cascade: if a CTE is only used by unused CTEs, it's also unused
        boolean changed = true;
        while (changed) {
            changed = false;
            Set<String> newlyUnused = findNewlyUnused(withExpr, unused);
            for (String name : newlyUnused) {
                if (!unused.contains(name)) {
                    unused.add(name);
                    changed = true;
                }
            }
        }

        return unused;
    }

    /**
     * Finds CTEs that are only used by already-unused CTEs
     */
    private Set<String> findNewlyUnused(Nodes.With withExpr, Set<String> alreadyUnused) {
        Set<String> newlyUnused = new HashSet<>(alreadyUnused);

        if (withExpr.getCtes() == null) {
            return newlyUnused;
        }

        for (Nodes.CTE cte : withExpr.getCtes()) {
            String cteName = cte.getName();
            if (cteName != null && !alreadyUnused.contains(cteName)) {
                // Check if this CTE is referenced
                boolean isReferenced = isCtdReferencedByNonUnused(cte, withExpr, alreadyUnused);
                if (!isReferenced) {
                    newlyUnused.add(cteName);
                }
            }
        }

        return newlyUnused;
    }

    /**
     * Checks if a CTE is referenced by any non-unused CTE or the main query
     */
    private boolean isCtdReferencedByNonUnused(
            Nodes.CTE cte,
            Nodes.With withExpr,
            Set<String> unusedCtes
    ) {
        String cteName = cte.getName();

        // Check if referenced in the main SELECT
        long mainRefCount = withExpr.getSelect().findAll(Nodes.Table.class)
                .filter(table -> {
                    String tableName = getTableName(table);
                    return tableName != null && tableName.equals(cteName);
                })
                .count();

        if (mainRefCount > 0) {
            return true;
        }

        // Check if referenced in other CTEs (that are not unused)
        if (withExpr.getCtes() != null) {
            for (Nodes.CTE otherCte : withExpr.getCtes()) {
                String otherCteName = otherCte.getName();
                if (otherCteName != null && !otherCteName.equals(cteName) &&
                    !unusedCtes.contains(otherCteName)) {

                    long refCount = otherCte.getSelect().findAll(Nodes.Table.class)
                            .filter(table -> {
                                String tableName = getTableName(table);
                                return tableName != null && tableName.equals(cteName);
                            })
                            .count();

                    if (refCount > 0) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Removes unused CTEs from a WITH statement
     */
    private Expression removeUnusedCtes(Nodes.With withExpr, Set<String> unusedCtes) {
        if (withExpr.getCtes() == null) {
            return withExpr;
        }

        List<Nodes.CTE> remainingCtes = withExpr.getCtes().stream()
                .filter(cte -> {
                    String cteName = cte.getName();
                    return cteName == null || !unusedCtes.contains(cteName);
                })
                .collect(Collectors.toList());

        // If all CTEs are removed, return just the SELECT without WITH
        if (remainingCtes.isEmpty()) {
            return withExpr.getSelect();
        }

        // Create new WITH clause with remaining CTEs
        return new Nodes.With(remainingCtes, withExpr.getSelect());
    }

    /**
     * Extracts the name from a Table
     */
    private String getTableName(Nodes.Table table) {
        // Table.getName() returns a String directly
        return table.getName();
    }
}
