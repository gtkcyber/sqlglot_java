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
package io.sqlglot.optimizer.util;

import io.sqlglot.expressions.Expression;
import io.sqlglot.expressions.Nodes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Utility functions for expression analysis and transformation.
 */
public class ExpressionUtils {
    private ExpressionUtils() { /* utility class */ }

    /**
     * Checks if an expression is a literal value
     */
    public static boolean isLiteral(Expression expr) {
        return expr instanceof Nodes.Literal;
    }

    /**
     * Checks if an expression is the boolean value TRUE
     */
    public static boolean isTrue(Expression expr) {
        return expr instanceof Nodes.True;
    }

    /**
     * Checks if an expression is the boolean value FALSE
     */
    public static boolean isFalse(Expression expr) {
        return expr instanceof Nodes.False;
    }

    /**
     * Checks if an expression is NULL
     */
    public static boolean isNull(Expression expr) {
        return expr instanceof Nodes.Null;
    }

    /**
     * Gets the numeric value of a literal, if possible
     */
    public static Optional<Double> getNumericValue(Expression expr) {
        if (expr instanceof Nodes.Literal literal) {
            try {
                String value = literal.getValue();
                if (value != null && !value.isEmpty()) {
                    return Optional.of(Double.parseDouble(value));
                }
            } catch (NumberFormatException e) {
                // Not a numeric literal
            }
        }
        return Optional.empty();
    }

    /**
     * Gets the string value of a literal, if possible
     */
    public static Optional<String> getStringValue(Expression expr) {
        if (expr instanceof Nodes.Literal literal) {
            return Optional.ofNullable(literal.getValue());
        }
        return Optional.empty();
    }

    /**
     * Checks if an expression is zero
     */
    public static boolean isZero(Expression expr) {
        return getNumericValue(expr).map(v -> v == 0.0).orElse(false);
    }

    /**
     * Checks if an expression is one
     */
    public static boolean isOne(Expression expr) {
        return getNumericValue(expr).map(v -> v == 1.0).orElse(false);
    }

    /**
     * Checks if two expressions are equal (by reference or value)
     */
    public static boolean expressionsEqual(Expression e1, Expression e2) {
        if (e1 == e2) return true;
        if (e1 == null || e2 == null) return false;

        // Compare by type and value
        if (e1.getClass() != e2.getClass()) return false;

        if (isTrue(e1) && isTrue(e2)) return true;
        if (isFalse(e1) && isFalse(e2)) return true;
        if (isNull(e1) && isNull(e2)) return true;

        // Compare by structure: compare all arguments recursively
        Map<String, Object> args1 = e1.args();
        Map<String, Object> args2 = e2.args();

        if (!args1.keySet().equals(args2.keySet())) return false;

        for (String key : args1.keySet()) {
            Object val1 = args1.get(key);
            Object val2 = args2.get(key);

            if (!argsEqual(val1, val2)) return false;
        }

        return true;
    }

    private static boolean argsEqual(Object val1, Object val2) {
        if (val1 == val2) return true;
        if (val1 == null || val2 == null) return false;

        // Recursively compare Expression arguments
        if (val1 instanceof Expression && val2 instanceof Expression) {
            return expressionsEqual((Expression) val1, (Expression) val2);
        }

        // Compare lists of expressions
        if (val1 instanceof List && val2 instanceof List) {
            List<?> list1 = (List<?>) val1;
            List<?> list2 = (List<?>) val2;
            if (list1.size() != list2.size()) return false;
            for (int i = 0; i < list1.size(); i++) {
                if (!argsEqual(list1.get(i), list2.get(i))) return false;
            }
            return true;
        }

        // Default comparison
        return val1.equals(val2);
    }

    /**
     * Creates a new TRUE literal
     */
    public static Expression createTrue() {
        return Nodes.True.INSTANCE;
    }

    /**
     * Creates a new FALSE literal
     */
    public static Expression createFalse() {
        return Nodes.False.INSTANCE;
    }

    /**
     * Creates a new NULL literal
     */
    public static Expression createNull() {
        return Nodes.Null.INSTANCE;
    }

    /**
     * Creates a numeric literal
     */
    public static Expression createNumericLiteral(double value) {
        String stringValue = (value == (long) value) ?
                String.format("%d", (long) value) :
                String.format("%f", value);
        return new Nodes.Literal(stringValue, false);
    }

    /**
     * Creates a string literal
     */
    public static Expression createStringLiteral(String value) {
        return new Nodes.Literal(value, true);
    }
}
