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

/**
 * Optimization rule that canonicalizes expressions to normal forms.
 *
 * Performs:
 * - Comparison normalization: 5 < x -> x > 5 (puts variable on left)
 * - NOT negation simplification: NOT(x > 5) -> x <= 5
 * - Operator commutative reordering when beneficial
 */
public class CanonicalizeRule implements OptimizerRule {

    @Override
    public Expression optimize(Expression expression, OptimizerContext context) {
        return expression.transform(this::canonicalize);
    }

    private Expression canonicalize(Expression expr) {
        // Comparison normalization
        if (expr instanceof Nodes.LT lt) {
            return canonicalizeLessThan(lt);
        }
        if (expr instanceof Nodes.GT gt) {
            return canonicalizeGreaterThan(gt);
        }
        if (expr instanceof Nodes.LTE lte) {
            return canonicalizeLessEqual(lte);
        }
        if (expr instanceof Nodes.GTE gte) {
            return canonicalizeGreaterEqual(gte);
        }

        // NOT simplification
        if (expr instanceof Nodes.Not not) {
            return canonicalizeNot(not);
        }
        if (expr instanceof Nodes.Not2 not2) {
            // Also handle Not2 variant
            Expression inner = not2.getExpression();
            if (inner instanceof Nodes.GT gt) {
                return new Nodes.LTE(gt.getLeft(), gt.getRight());
            }
            if (inner instanceof Nodes.LT lt) {
                return new Nodes.GTE(lt.getLeft(), lt.getRight());
            }
            if (inner instanceof Nodes.GTE gte) {
                return new Nodes.LT(gte.getLeft(), gte.getRight());
            }
            if (inner instanceof Nodes.LTE lte) {
                return new Nodes.GT(lte.getLeft(), lte.getRight());
            }
            if (inner instanceof Nodes.EQ eq) {
                return new Nodes.NEQ(eq.getLeft(), eq.getRight());
            }
            if (inner instanceof Nodes.NEQ neq) {
                return new Nodes.EQ(neq.getLeft(), neq.getRight());
            }
        }

        return expr;
    }

    /**
     * Canonicalizes LT: if right is literal and left is not, swap to GT
     * e.g., 5 < x -> x > 5
     */
    private Expression canonicalizeLessThan(Nodes.LT lt) {
        Expression left = lt.getLeft();
        Expression right = lt.getRight();

        // If left is a literal/constant and right is a column/expression, flip
        if (isConstant(left) && !isConstant(right)) {
            return new Nodes.GT(right, left);
        }

        return lt;
    }

    /**
     * Canonicalizes GT: if left is literal and right is not, swap to LT
     * e.g., 5 > x -> x < 5
     */
    private Expression canonicalizeGreaterThan(Nodes.GT gt) {
        Expression left = gt.getLeft();
        Expression right = gt.getRight();

        // If left is a literal/constant and right is a column/expression, flip
        if (isConstant(left) && !isConstant(right)) {
            return new Nodes.LT(right, left);
        }

        return gt;
    }

    /**
     * Canonicalizes LTE: if left is literal and right is not, swap to GTE
     * e.g., 5 <= x -> x >= 5
     */
    private Expression canonicalizeLessEqual(Nodes.LTE lte) {
        Expression left = lte.getLeft();
        Expression right = lte.getRight();

        // If left is a literal/constant and right is a column/expression, flip
        if (isConstant(left) && !isConstant(right)) {
            return new Nodes.GTE(right, left);
        }

        return lte;
    }

    /**
     * Canonicalizes GTE: if left is literal and right is not, swap to LTE
     * e.g., 5 >= x -> x <= 5
     */
    private Expression canonicalizeGreaterEqual(Nodes.GTE gte) {
        Expression left = gte.getLeft();
        Expression right = gte.getRight();

        // If left is a literal/constant and right is a column/expression, flip
        if (isConstant(left) && !isConstant(right)) {
            return new Nodes.LTE(right, left);
        }

        return gte;
    }

    /**
     * Canonicalizes NOT expressions by pushing negation inward when possible
     * e.g., NOT(x > 5) -> x <= 5
     */
    private Expression canonicalizeNot(Nodes.Not not) {
        Expression inner = not.getExpression();

        // Unwrap parentheses if present
        if (inner instanceof Nodes.Paren paren) {
            inner = paren.getExpression();
        }

        if (inner instanceof Nodes.GT gt) {
            // NOT(x > y) -> x <= y
            return new Nodes.LTE(gt.getLeft(), gt.getRight());
        }
        if (inner instanceof Nodes.LT lt) {
            // NOT(x < y) -> x >= y
            return new Nodes.GTE(lt.getLeft(), lt.getRight());
        }
        if (inner instanceof Nodes.GTE gte) {
            // NOT(x >= y) -> x < y
            return new Nodes.LT(gte.getLeft(), gte.getRight());
        }
        if (inner instanceof Nodes.LTE lte) {
            // NOT(x <= y) -> x > y
            return new Nodes.GT(lte.getLeft(), lte.getRight());
        }
        if (inner instanceof Nodes.EQ eq) {
            // NOT(x = y) -> x != y
            return new Nodes.NEQ(eq.getLeft(), eq.getRight());
        }
        if (inner instanceof Nodes.NEQ neq) {
            // NOT(x != y) -> x = y
            return new Nodes.EQ(neq.getLeft(), neq.getRight());
        }

        return not;
    }

    /**
     * Determines if an expression is a constant/literal value
     */
    private boolean isConstant(Expression expr) {
        return expr instanceof Nodes.Literal ||
                expr instanceof Nodes.True ||
                expr instanceof Nodes.False ||
                expr instanceof Nodes.Null;
    }
}
