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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Main optimizer orchestrator that applies a sequence of optimization rules to expressions.
 * Rules are applied sequentially in the order they are registered.
 */
public class Optimizer {
    private final List<OptimizerRule> rules;
    private final OptimizerContext context;

    /**
     * Creates an optimizer with no rules
     */
    public Optimizer(OptimizerContext context) {
        this.context = Objects.requireNonNull(context, "context cannot be null");
        this.rules = new ArrayList<>();
    }

    /**
     * Creates an optimizer with initial rules
     */
    public Optimizer(OptimizerContext context, List<OptimizerRule> rules) {
        this.context = Objects.requireNonNull(context, "context cannot be null");
        this.rules = new ArrayList<>(Objects.requireNonNull(rules, "rules cannot be null"));
    }

    /**
     * Adds a rule to the optimizer
     */
    public Optimizer addRule(OptimizerRule rule) {
        rules.add(Objects.requireNonNull(rule, "rule cannot be null"));
        return this;
    }

    /**
     * Optimizes an expression by applying all registered rules in sequence
     */
    public Expression optimize(Expression expression) {
        Objects.requireNonNull(expression, "expression cannot be null");

        Expression result = expression;
        for (OptimizerRule rule : rules) {
            result = rule.optimize(result, context);
        }
        return result;
    }

    /**
     * Gets the current list of rules
     */
    public List<OptimizerRule> getRules() {
        return new ArrayList<>(rules);
    }

    /**
     * Gets the optimization context
     */
    public OptimizerContext getContext() {
        return context;
    }

    /**
     * Clears all rules
     */
    public void clearRules() {
        rules.clear();
    }
}
