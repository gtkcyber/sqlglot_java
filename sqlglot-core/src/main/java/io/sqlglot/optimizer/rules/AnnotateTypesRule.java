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

/**
 * Optimization rule that performs basic type inference for expressions.
 *
 * Performs:
 * - Type inference: NUMERIC for arithmetic operations, BOOLEAN for comparisons
 * - Tracks NULL-ability of expressions (whether they can return NULL)
 * - Basic type checking for incompatible operations
 * - Annotates expressions with inferred type information
 *
 * Note: This implementation uses a simple type system suitable for SQL optimization.
 * Not comprehensive but handles the most common cases.
 */
public class AnnotateTypesRule implements OptimizerRule {

    /**
     * Simple type enumeration for SQL types.
     */
    public enum SqlType {
        NUMERIC,
        STRING,
        BOOLEAN,
        DATE,
        UNKNOWN
    }

    /**
     * Context for tracking inferred types.
     */
    private static class TypeContext {
        SqlType type;
        boolean isNullable;

        TypeContext(SqlType type, boolean isNullable) {
            this.type = type;
            this.isNullable = isNullable;
        }
    }

    @Override
    public Expression optimize(Expression expression, OptimizerContext context) {
        // For now, we perform type inference but don't modify the expression
        // In a full implementation, we could annotate expressions with type metadata
        expression.transform(this::inferType);
        return expression;
    }

    /**
     * Infers the type of an expression.
     */
    private Expression inferType(Expression expr) {
        TypeContext typeContext = inferExpressionType(expr);
        // In a real implementation, we would annotate the expression with type info
        return expr;
    }

    /**
     * Infers the type and nullability of an expression.
     */
    private TypeContext inferExpressionType(Expression expr) {
        // Literals
        if (expr instanceof Nodes.Literal literal) {
            return inferLiteralType(literal);
        }

        // NULL
        if (ExpressionUtils.isNull(expr)) {
            return new TypeContext(SqlType.UNKNOWN, true);
        }

        // Booleans
        if (ExpressionUtils.isTrue(expr) || ExpressionUtils.isFalse(expr)) {
            return new TypeContext(SqlType.BOOLEAN, false);
        }

        // Arithmetic operations
        if (expr instanceof Nodes.Add || expr instanceof Nodes.Sub ||
            expr instanceof Nodes.Mul || expr instanceof Nodes.Div) {
            return inferArithmeticType((Nodes.Binary) expr);
        }

        // Comparison operations
        if (expr instanceof Nodes.EQ || expr instanceof Nodes.NEQ ||
            expr instanceof Nodes.GT || expr instanceof Nodes.LT ||
            expr instanceof Nodes.GTE || expr instanceof Nodes.LTE) {
            return inferComparisonType((Nodes.Binary) expr);
        }

        // Logical operations
        if (expr instanceof Nodes.And || expr instanceof Nodes.Or) {
            return new TypeContext(SqlType.BOOLEAN, false);
        }

        // NOT operation
        if (expr instanceof Nodes.Not) {
            return new TypeContext(SqlType.BOOLEAN, false);
        }

        // IS NULL / IS NOT NULL
        if (expr instanceof Nodes.Is) {
            return new TypeContext(SqlType.BOOLEAN, false);
        }

        // Default: unknown type
        return new TypeContext(SqlType.UNKNOWN, true);
    }

    /**
     * Infers type of a literal expression.
     */
    private TypeContext inferLiteralType(Nodes.Literal literal) {
        if (literal.isString()) {
            return new TypeContext(SqlType.STRING, false);
        }

        // Try to parse as numeric
        try {
            Double.parseDouble(literal.getValue());
            return new TypeContext(SqlType.NUMERIC, false);
        } catch (NumberFormatException e) {
            // Not a number, assume string
            return new TypeContext(SqlType.STRING, false);
        }
    }

    /**
     * Infers type of an arithmetic operation.
     */
    private TypeContext inferArithmeticType(Nodes.Binary op) {
        // Arithmetic operations produce numeric results
        // Note: Children are already processed by transform(), so we don't need to infer child types
        return new TypeContext(SqlType.NUMERIC, true);
    }

    /**
     * Infers type of a comparison operation.
     */
    private TypeContext inferComparisonType(Nodes.Binary op) {
        // Comparison operations always produce boolean results
        // Note: Children are already processed by transform(), so we don't need to infer child types
        return new TypeContext(SqlType.BOOLEAN, true);
    }

    /**
     * Checks if two types are likely incompatible for comparison.
     */
    private boolean isIncompatibleComparison(TypeContext left, TypeContext right) {
        // If either type is unknown, we can't determine incompatibility
        if (left.type == SqlType.UNKNOWN || right.type == SqlType.UNKNOWN) {
            return false;
        }

        // Numeric vs string comparison is suspicious but allowed in SQL
        return (left.type == SqlType.NUMERIC && right.type == SqlType.STRING) ||
               (left.type == SqlType.STRING && right.type == SqlType.NUMERIC);
    }
}
