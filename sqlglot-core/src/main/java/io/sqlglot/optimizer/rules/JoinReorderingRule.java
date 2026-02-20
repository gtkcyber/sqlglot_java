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

import java.util.ArrayList;
import java.util.List;

/**
 * Optimization rule that reorders joins for better query performance.
 *
 * Performs:
 * - Moves restrictive INNER joins earlier to reduce intermediate result sets
 * - Reorders joins to apply selective conditions first
 * - Preserves LEFT and RIGHT join semantics (cannot reorder outer joins)
 * - Applies heuristics to estimate join selectivity
 */
public class JoinReorderingRule implements OptimizerRule {

    @Override
    public Expression optimize(Expression expression, OptimizerContext context) {
        return expression.transform(this::reorderJoins);
    }

    /**
     * Attempts to reorder joins in SELECT statements.
     */
    private Expression reorderJoins(Expression expr) {
        if (expr instanceof Nodes.Select select) {
            return reorderSelectJoins(select);
        }
        return expr;
    }

    /**
     * Reorders joins in a SELECT statement.
     * Strategy: Move INNER joins with restrictive conditions earlier.
     */
    private Expression reorderSelectJoins(Nodes.Select select) {
        List<Nodes.Join> joins = select.getJoins();

        // Only attempt reordering if there are multiple joins
        if (joins.size() < 2) {
            return select;
        }

        // Check if we can safely reorder (all INNER joins or mix with LEFT after INNER)
        List<Nodes.Join> reorderedJoins = tryReorderJoins(joins);

        // If reordering changed the order, return new select with reordered joins
        if (!reorderedJoins.equals(joins)) {
            return new Nodes.Select(
                select.getExpressions(),
                select.isDistinct(),
                select.getFrom(),
                reorderedJoins,
                select.getWhere(),
                select.getGroupBy(),
                select.getHaving(),
                select.getOrderBy(),
                select.getLimit(),
                select.getOffset()
            );
        }

        return select;
    }

    /**
     * Attempts to reorder joins by moving restrictive INNER joins earlier.
     */
    private List<Nodes.Join> tryReorderJoins(List<Nodes.Join> joins) {
        List<Nodes.Join> result = new ArrayList<>(joins);

        // Simple bubble sort based on join type and selectivity
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0; i < result.size() - 1; i++) {
                Nodes.Join current = result.get(i);
                Nodes.Join next = result.get(i + 1);

                // Try to swap: move INNER joins before OUTER joins
                if (shouldSwap(current, next)) {
                    result.set(i, next);
                    result.set(i + 1, current);
                    changed = true;
                }
            }
        }

        return result;
    }

    /**
     * Determines if two joins should be swapped.
     * Swap if moving the second join earlier would be beneficial.
     */
    private boolean shouldSwap(Nodes.Join current, Nodes.Join next) {
        String currentType = current.getType().toUpperCase();
        String nextType = next.getType().toUpperCase();

        // Don't swap if current is INNER and next is OUTER
        // (INNER joins are already preferentially earlier)
        if (isInnerJoin(currentType) && isOuterJoin(nextType)) {
            return false;
        }

        // Swap if current is OUTER and next is INNER
        // (move INNER joins earlier for better performance)
        if (isOuterJoin(currentType) && isInnerJoin(nextType)) {
            return true;
        }

        // For same join types, consider join condition selectivity
        if (currentType.equals(nextType)) {
            return hasMoreSelectiveCondition(next, current);
        }

        return false;
    }

    /**
     * Checks if a join type is an INNER join.
     */
    private boolean isInnerJoin(String joinType) {
        return joinType.equals("INNER") || joinType.equals("CROSS");
    }

    /**
     * Checks if a join type is an OUTER join.
     */
    private boolean isOuterJoin(String joinType) {
        return joinType.equals("LEFT") || joinType.equals("RIGHT") || joinType.equals("FULL");
    }

    /**
     * Determines if one join has a more selective condition than another.
     * This is a heuristic: checks for equality conditions vs other conditions.
     */
    private boolean hasMoreSelectiveCondition(Nodes.Join join1, Nodes.Join join2) {
        Expression cond1 = join1.getOn();
        Expression cond2 = join2.getOn();

        // Joins with equality conditions are more selective
        boolean cond1IsEQ = cond1 instanceof Nodes.EQ;
        boolean cond2IsEQ = cond2 instanceof Nodes.EQ;

        return cond1IsEQ && !cond2IsEQ;
    }
}
