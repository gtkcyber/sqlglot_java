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
import io.sqlglot.optimizer.util.BooleanUtils;
import io.sqlglot.optimizer.util.ExpressionUtils;

import java.util.Optional;

/**
 * Optimization rule that simplifies boolean and arithmetic expressions.
 *
 * Performs:
 * - Boolean simplification: TRUE AND x -> x, FALSE OR x -> x, NOT NOT x -> x
 * - Arithmetic simplification: 1+2 -> 3, x+0 -> x, x*1 -> x
 * - Comparison simplification: x=x -> TRUE, x IS NULL -> TRUE for nulls
 * - Constant folding for arithmetic operations
 */
public class SimplifyRule implements OptimizerRule {

    @Override
    public Expression optimize(Expression expression, OptimizerContext context) {
        return expression.transform(this::simplifyNode);
    }

    private Expression simplifyNode(Expression expr) {
        // Boolean simplification
        if (expr instanceof Nodes.Not not) {
            return simplifyNot(not);
        }
        if (expr instanceof Nodes.And and) {
            return BooleanUtils.simplifyAnd(and);
        }
        if (expr instanceof Nodes.Or or) {
            return BooleanUtils.simplifyOr(or);
        }

        // Comparison simplification
        if (expr instanceof Nodes.EQ eq) {
            return simplifyEquality(eq);
        }
        if (expr instanceof Nodes.NEQ neq) {
            return simplifyInequality(neq);
        }
        if (expr instanceof Nodes.Is is) {
            return simplifyIs(is);
        }

        // Arithmetic simplification
        if (expr instanceof Nodes.Add add) {
            return simplifyAdd(add);
        }
        if (expr instanceof Nodes.Sub sub) {
            return simplifySub(sub);
        }
        if (expr instanceof Nodes.Mul mul) {
            return simplifyMul(mul);
        }
        if (expr instanceof Nodes.Div div) {
            return simplifyDiv(div);
        }

        return expr;
    }

    private Expression simplifyNot(Nodes.Not not) {
        // NOT(NOT(x)) -> x
        Expression simplified = BooleanUtils.simplifyDoubleNegation(not);
        if (simplified != not) {
            return simplified;
        }

        // NOT(TRUE) -> FALSE
        simplified = BooleanUtils.simplifyNotTrue(not);
        if (simplified != not) {
            return simplified;
        }

        // NOT(FALSE) -> TRUE
        simplified = BooleanUtils.simplifyNotFalse(not);
        if (simplified != not) {
            return simplified;
        }

        // Check for comparison operators to negate
        Expression inner = not.getExpression();
        if (inner instanceof Nodes.GT gt) {
            // NOT(x > y) -> x <= y
            return new Nodes.LTE(gt.getLeft(), gt.getRight());
        }
        if (inner instanceof Nodes.LT lt) {
            // NOT(x < y) -> x >= y
            return new Nodes.GTE(lt.getLeft(), lt.getRight());
        }

        // Apply De Morgan's laws if beneficial
        return not;
    }

    private Expression simplifyEquality(Nodes.EQ eq) {
        Expression left = eq.getLeft();
        Expression right = eq.getRight();

        // x = x -> TRUE
        if (ExpressionUtils.expressionsEqual(left, right)) {
            return ExpressionUtils.createTrue();
        }

        // NULL = x -> NULL (should not simplify to FALSE, NULL != FALSE)
        if (ExpressionUtils.isNull(left) || ExpressionUtils.isNull(right)) {
            return ExpressionUtils.createNull();
        }

        // Constant folding
        return tryConstantFolding(left, right, (l, r) -> l == r ? 1 : 0)
                .map(result -> result == 1 ? ExpressionUtils.createTrue() : ExpressionUtils.createFalse())
                .orElse(eq);
    }

    private Expression simplifyInequality(Nodes.NEQ neq) {
        Expression left = neq.getLeft();
        Expression right = neq.getRight();

        // x != x -> FALSE
        if (ExpressionUtils.expressionsEqual(left, right)) {
            return ExpressionUtils.createFalse();
        }

        // NULL != x -> NULL
        if (ExpressionUtils.isNull(left) || ExpressionUtils.isNull(right)) {
            return ExpressionUtils.createNull();
        }

        // Constant folding
        return tryConstantFolding(left, right, (l, r) -> l != r ? 1 : 0)
                .map(result -> result == 1 ? ExpressionUtils.createTrue() : ExpressionUtils.createFalse())
                .orElse(neq);
    }

    private Expression simplifyIs(Nodes.Is is) {
        Expression left = is.getLeft();
        Expression right = is.getRight();

        // x IS NULL where x is NULL -> TRUE
        if (ExpressionUtils.isNull(left) && ExpressionUtils.isNull(right)) {
            return ExpressionUtils.createTrue();
        }

        // x IS NOT NULL where x is not NULL -> TRUE (heuristic)
        if (is.getRight() != null && is.getRight() instanceof Nodes.Not) {
            if (ExpressionUtils.isNull(left)) {
                return ExpressionUtils.createFalse();
            }
        }

        return is;
    }

    private Expression simplifyAdd(Nodes.Add add) {
        Expression left = add.getLeft();
        Expression right = add.getRight();

        // x + 0 -> x
        if (ExpressionUtils.isZero(right)) {
            return left;
        }
        // 0 + x -> x
        if (ExpressionUtils.isZero(left)) {
            return right;
        }

        // NULL + x -> NULL
        if (ExpressionUtils.isNull(left) || ExpressionUtils.isNull(right)) {
            return ExpressionUtils.createNull();
        }

        // Constant folding: 1 + 2 -> 3
        return tryConstantFolding(left, right, (l, r) -> (int) (l + r))
                .map(ExpressionUtils::createNumericLiteral)
                .orElse(add);
    }

    private Expression simplifySub(Nodes.Sub sub) {
        Expression left = sub.getLeft();
        Expression right = sub.getRight();

        // x - 0 -> x
        if (ExpressionUtils.isZero(right)) {
            return left;
        }

        // x - x -> 0
        if (ExpressionUtils.expressionsEqual(left, right)) {
            return ExpressionUtils.createNumericLiteral(0);
        }

        // NULL - x -> NULL
        if (ExpressionUtils.isNull(left) || ExpressionUtils.isNull(right)) {
            return ExpressionUtils.createNull();
        }

        // Constant folding: 5 - 2 -> 3
        return tryConstantFolding(left, right, (l, r) -> (int) (l - r))
                .map(ExpressionUtils::createNumericLiteral)
                .orElse(sub);
    }

    private Expression simplifyMul(Nodes.Mul mul) {
        Expression left = mul.getLeft();
        Expression right = mul.getRight();

        // 0 * x -> 0 or x * 0 -> 0
        if (ExpressionUtils.isZero(left) || ExpressionUtils.isZero(right)) {
            return ExpressionUtils.createNumericLiteral(0);
        }

        // 1 * x -> x
        if (ExpressionUtils.isOne(right)) {
            return left;
        }
        // x * 1 -> x
        if (ExpressionUtils.isOne(left)) {
            return right;
        }

        // NULL * x -> NULL (with NULL propagation)
        // In SQL, NULL * 0 = NULL (not 0), so we don't simplify this case
        if (ExpressionUtils.isNull(left) || ExpressionUtils.isNull(right)) {
            return ExpressionUtils.createNull();
        }

        // Constant folding: 3 * 4 -> 12
        return tryConstantFolding(left, right, (l, r) -> (int) (l * r))
                .map(ExpressionUtils::createNumericLiteral)
                .orElse(mul);
    }

    private Expression simplifyDiv(Nodes.Div div) {
        Expression left = div.getLeft();
        Expression right = div.getRight();

        // x / 1 -> x
        if (ExpressionUtils.isOne(right)) {
            return left;
        }

        // 0 / x -> 0 (where x != 0)
        if (ExpressionUtils.isZero(left) && !ExpressionUtils.isZero(right)) {
            return ExpressionUtils.createNumericLiteral(0);
        }

        // x / 0 -> NULL (division by zero, SQL behavior)
        if (ExpressionUtils.isZero(right)) {
            return ExpressionUtils.createNull();
        }

        // x / x -> 1 (where x != 0)
        if (ExpressionUtils.expressionsEqual(left, right) &&
                !ExpressionUtils.isZero(left)) {
            return ExpressionUtils.createNumericLiteral(1);
        }

        // NULL / x -> NULL
        if (ExpressionUtils.isNull(left) || ExpressionUtils.isNull(right)) {
            return ExpressionUtils.createNull();
        }

        // Constant folding: 6 / 2 -> 3
        return tryConstantFolding(left, right, (l, r) -> r != 0 ? (int) (l / r) : null)
                .map(ExpressionUtils::createNumericLiteral)
                .orElse(div);
    }

    /**
     * Attempts constant folding with the given operation.
     * Returns Optional.empty() if either operand is not a numeric literal.
     */
    private Optional<Double> tryConstantFolding(
            Expression left,
            Expression right,
            ArithmeticOp op
    ) {
        Optional<Double> leftVal = ExpressionUtils.getNumericValue(left);
        Optional<Double> rightVal = ExpressionUtils.getNumericValue(right);

        if (leftVal.isPresent() && rightVal.isPresent()) {
            try {
                Integer result = op.apply(leftVal.get(), rightVal.get());
                return result != null ? Optional.of(result.doubleValue()) : Optional.empty();
            } catch (Exception e) {
                // Operation failed (e.g., division by zero)
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    @FunctionalInterface
    private interface ArithmeticOp {
        Integer apply(double left, double right);
    }
}
