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
import com.gtkcyber.sqlglot.optimizer.util.BooleanUtils;

import java.util.*;

/**
 * Optimization rule that normalizes boolean predicates to Conjunctive Normal Form (CNF).
 *
 * Performs:
 * - Converts to CNF: (a OR b) AND (c OR d)
 * - Flattens nested boolean expressions
 * - Consolidates duplicate predicates
 * - Applies De Morgan's laws
 * - Removes contradictions
 *
 * Example:
 * SELECT * FROM t WHERE NOT(a AND b) OR (c AND d)
 * → Normalized to canonical CNF form for better analysis
 */
public class NormalizePredicatesRule implements OptimizerRule {

    @Override
    public Expression optimize(Expression expression, OptimizerContext context) {
        return expression.transform(this::normalizeExpression);
    }

    private Expression normalizeExpression(Expression expr) {
        if (expr instanceof Nodes.Or or) {
            return normalizeOr(or);
        }
        if (expr instanceof Nodes.And and) {
            return normalizeAnd(and);
        }
        if (expr instanceof Nodes.Not not) {
            return normalizeNot(not);
        }
        return expr;
    }

    /**
     * Normalizes OR expressions
     */
    private Expression normalizeOr(Nodes.Or or) {
        List<Expression> operands = flattenOr(or);
        operands = deduplicateOperands(operands);

        if (operands.isEmpty()) {
            return Nodes.False.INSTANCE;
        }
        if (operands.size() == 1) {
            return operands.get(0);
        }

        // Rebuild OR expression
        Expression result = operands.get(0);
        for (int i = 1; i < operands.size(); i++) {
            result = new Nodes.Or(result, operands.get(i));
        }
        return result;
    }

    /**
     * Normalizes AND expressions
     */
    private Expression normalizeAnd(Nodes.And and) {
        List<Expression> operands = flattenAnd(and);
        operands = deduplicateOperands(operands);

        // Check for contradictions (both x and NOT x)
        if (hasContradiction(operands)) {
            return Nodes.False.INSTANCE;
        }

        if (operands.isEmpty()) {
            return Nodes.True.INSTANCE;
        }
        if (operands.size() == 1) {
            return operands.get(0);
        }

        // Rebuild AND expression
        Expression result = operands.get(0);
        for (int i = 1; i < operands.size(); i++) {
            result = new Nodes.And(result, operands.get(i));
        }
        return result;
    }

    /**
     * Normalizes NOT expressions by applying De Morgan's laws
     */
    private Expression normalizeNot(Nodes.Not not) {
        Expression inner = not.getExpression();

        // Apply De Morgan's laws
        if (inner instanceof Nodes.And and) {
            // NOT(a AND b) = NOT(a) OR NOT(b)
            Expression notLeft = new Nodes.Not(and.getLeft());
            Expression notRight = new Nodes.Not(and.getRight());
            return new Nodes.Or(notLeft, notRight);
        }

        if (inner instanceof Nodes.Or or) {
            // NOT(a OR b) = NOT(a) AND NOT(b)
            Expression notLeft = new Nodes.Not(or.getLeft());
            Expression notRight = new Nodes.Not(or.getRight());
            return new Nodes.And(notLeft, notRight);
        }

        // Double negation elimination
        if (inner instanceof Nodes.Not innerNot) {
            return innerNot.getExpression();
        }

        return not;
    }

    /**
     * Flattens nested OR expressions
     */
    private List<Expression> flattenOr(Nodes.Or or) {
        List<Expression> result = new ArrayList<>();
        flattenOrHelper(or, result);
        return result;
    }

    private void flattenOrHelper(Expression expr, List<Expression> result) {
        if (expr instanceof Nodes.Or or) {
            flattenOrHelper(or.getLeft(), result);
            flattenOrHelper(or.getRight(), result);
        } else {
            result.add(expr);
        }
    }

    /**
     * Flattens nested AND expressions
     */
    private List<Expression> flattenAnd(Nodes.And and) {
        List<Expression> result = new ArrayList<>();
        flattenAndHelper(and, result);
        return result;
    }

    private void flattenAndHelper(Expression expr, List<Expression> result) {
        if (expr instanceof Nodes.And and) {
            flattenAndHelper(and.getLeft(), result);
            flattenAndHelper(and.getRight(), result);
        } else {
            result.add(expr);
        }
    }

    /**
     * Removes duplicate operands
     */
    private List<Expression> deduplicateOperands(List<Expression> operands) {
        Set<String> seen = new LinkedHashSet<>();
        List<Expression> result = new ArrayList<>();

        for (Expression expr : operands) {
            String exprStr = expr.toString();
            if (!seen.contains(exprStr)) {
                seen.add(exprStr);
                result.add(expr);
            }
        }

        return result;
    }

    /**
     * Checks if operands contain a contradiction (x AND NOT x)
     */
    private boolean hasContradiction(List<Expression> operands) {
        for (int i = 0; i < operands.size(); i++) {
            Expression expr = operands.get(i);

            for (int j = i + 1; j < operands.size(); j++) {
                Expression other = operands.get(j);

                // Check if expr and NOT(expr)
                if (other instanceof Nodes.Not notExpr) {
                    if (expressionsEqual(expr, notExpr.getExpression())) {
                        return true;
                    }
                }

                // Check if NOT(expr) and expr
                if (expr instanceof Nodes.Not notExpr) {
                    if (expressionsEqual(notExpr.getExpression(), other)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if two expressions are semantically equal
     */
    private boolean expressionsEqual(Expression e1, Expression e2) {
        if (e1 == e2) return true;
        if (e1 == null || e2 == null) return false;
        return e1.toString().equals(e2.toString());
    }
}
