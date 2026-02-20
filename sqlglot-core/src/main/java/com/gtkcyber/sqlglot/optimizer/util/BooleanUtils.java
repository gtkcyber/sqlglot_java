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
package com.gtkcyber.sqlglot.optimizer.util;

import com.gtkcyber.sqlglot.expressions.Expression;
import com.gtkcyber.sqlglot.expressions.Nodes;

/**
 * Utility functions for boolean algebra simplification.
 */
public class BooleanUtils {
    private BooleanUtils() { /* utility class */ }

    /**
     * Simplifies NOT(NOT(x)) -> x
     */
    public static Expression simplifyDoubleNegation(Nodes.Not not) {
        if (not.getExpression() instanceof Nodes.Not innerNot) {
            return innerNot.getExpression();
        }
        return not;
    }

    /**
     * Simplifies NOT(TRUE) -> FALSE
     */
    public static Expression simplifyNotTrue(Nodes.Not not) {
        if (ExpressionUtils.isTrue(not.getExpression())) {
            return ExpressionUtils.createFalse();
        }
        return not;
    }

    /**
     * Simplifies NOT(FALSE) -> TRUE
     */
    public static Expression simplifyNotFalse(Nodes.Not not) {
        if (ExpressionUtils.isFalse(not.getExpression())) {
            return ExpressionUtils.createTrue();
        }
        return not;
    }

    /**
     * Simplifies AND expressions with identity/absorbing elements
     * TRUE AND x -> x
     * FALSE AND x -> FALSE
     * x AND x -> x
     */
    public static Expression simplifyAnd(Nodes.And and) {
        Expression left = and.getLeft();
        Expression right = and.getRight();

        // FALSE AND x -> FALSE
        if (ExpressionUtils.isFalse(left) || ExpressionUtils.isFalse(right)) {
            return ExpressionUtils.createFalse();
        }

        // TRUE AND x -> x
        if (ExpressionUtils.isTrue(left)) {
            return right;
        }
        if (ExpressionUtils.isTrue(right)) {
            return left;
        }

        // x AND x -> x
        if (ExpressionUtils.expressionsEqual(left, right)) {
            return left;
        }

        // NULL AND FALSE -> FALSE
        if (ExpressionUtils.isNull(left) && ExpressionUtils.isFalse(right)) {
            return ExpressionUtils.createFalse();
        }
        if (ExpressionUtils.isFalse(left) && ExpressionUtils.isNull(right)) {
            return ExpressionUtils.createFalse();
        }

        return and;
    }

    /**
     * Simplifies OR expressions with identity/absorbing elements
     * TRUE OR x -> TRUE
     * FALSE OR x -> x
     * x OR x -> x
     */
    public static Expression simplifyOr(Nodes.Or or) {
        Expression left = or.getLeft();
        Expression right = or.getRight();

        // TRUE OR x -> TRUE
        if (ExpressionUtils.isTrue(left) || ExpressionUtils.isTrue(right)) {
            return ExpressionUtils.createTrue();
        }

        // FALSE OR x -> x
        if (ExpressionUtils.isFalse(left)) {
            return right;
        }
        if (ExpressionUtils.isFalse(right)) {
            return left;
        }

        // x OR x -> x
        if (ExpressionUtils.expressionsEqual(left, right)) {
            return left;
        }

        // NULL OR TRUE -> TRUE
        if (ExpressionUtils.isNull(left) && ExpressionUtils.isTrue(right)) {
            return ExpressionUtils.createTrue();
        }
        if (ExpressionUtils.isTrue(left) && ExpressionUtils.isNull(right)) {
            return ExpressionUtils.createTrue();
        }

        return or;
    }

    /**
     * Applies De Morgan's law: NOT(a AND b) -> NOT(a) OR NOT(b)
     */
    public static Expression applyDeMorgan(Nodes.Not not) {
        Expression inner = not.getExpression();

        if (inner instanceof Nodes.And and) {
            Nodes.Not notLeft = new Nodes.Not(and.getLeft());
            Nodes.Not notRight = new Nodes.Not(and.getRight());
            return new Nodes.Or(notLeft, notRight);
        }

        if (inner instanceof Nodes.Or or) {
            Nodes.Not notLeft = new Nodes.Not(or.getLeft());
            Nodes.Not notRight = new Nodes.Not(or.getRight());
            return new Nodes.And(notLeft, notRight);
        }

        return not;
    }
}
