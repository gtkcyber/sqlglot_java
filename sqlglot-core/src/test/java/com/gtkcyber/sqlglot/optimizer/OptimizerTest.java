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

import com.gtkcyber.sqlglot.SqlGlot;
import com.gtkcyber.sqlglot.dialect.Dialect;
import com.gtkcyber.sqlglot.expressions.Expression;
import com.gtkcyber.sqlglot.expressions.Nodes;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OptimizerTest {

    @Test
    void testOptimizerCreation() {
        Dialect dialect = SqlGlot.getDialect("ANSI");
        OptimizerContext context = OptimizerContext.of(dialect);
        Optimizer optimizer = new Optimizer(context);

        assertNotNull(optimizer);
        assertEquals(0, optimizer.getRules().size());
    }

    @Test
    void testAddRule() {
        Dialect dialect = SqlGlot.getDialect("ANSI");
        OptimizerContext context = OptimizerContext.of(dialect);
        Optimizer optimizer = new Optimizer(context);

        OptimizerRule dummyRule = (expr, ctx) -> expr;
        optimizer.addRule(dummyRule);

        assertEquals(1, optimizer.getRules().size());
    }

    @Test
    void testAddMultipleRules() {
        Dialect dialect = SqlGlot.getDialect("ANSI");
        OptimizerContext context = OptimizerContext.of(dialect);
        Optimizer optimizer = new Optimizer(context);

        optimizer.addRule((expr, ctx) -> expr);
        optimizer.addRule((expr, ctx) -> expr);
        optimizer.addRule((expr, ctx) -> expr);

        assertEquals(3, optimizer.getRules().size());
    }

    @Test
    void testOptimizeWithNoRules() {
        Dialect dialect = SqlGlot.getDialect("ANSI");
        OptimizerContext context = OptimizerContext.of(dialect);
        Optimizer optimizer = new Optimizer(context);

        String sql = "SELECT * FROM users";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Expression result = optimizer.optimize(expr.get());
        assertEquals(expr.get(), result);
    }

    @Test
    void testOptimizeWithSingleRule() {
        Dialect dialect = SqlGlot.getDialect("ANSI");
        OptimizerContext context = OptimizerContext.of(dialect);
        Optimizer optimizer = new Optimizer(context);

        // Rule that always returns a new Select
        optimizer.addRule((expr, ctx) -> new Nodes.Select(java.util.List.of()));

        String sql = "SELECT * FROM users";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Expression result = optimizer.optimize(expr.get());
        assertTrue(result instanceof Nodes.Select);
    }

    @Test
    void testOptimizeRuleSequence() {
        Dialect dialect = SqlGlot.getDialect("ANSI");
        OptimizerContext context = OptimizerContext.of(dialect);
        Optimizer optimizer = new Optimizer(context);

        // Track rule execution order
        java.util.List<Integer> order = new java.util.ArrayList<>();

        optimizer.addRule((expr, ctx) -> {
            order.add(1);
            return expr;
        });
        optimizer.addRule((expr, ctx) -> {
            order.add(2);
            return expr;
        });
        optimizer.addRule((expr, ctx) -> {
            order.add(3);
            return expr;
        });

        String sql = "SELECT * FROM users";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        optimizer.optimize(expr.get());

        assertEquals(java.util.List.of(1, 2, 3), order);
    }

    @Test
    void testOptimizerContext() {
        Dialect dialect = SqlGlot.getDialect("ANSI");
        OptimizerContext context = OptimizerContext.of(dialect);

        assertEquals(dialect, context.dialect());
        assertTrue(context.schema().isEmpty());
        assertTrue(context.database().isEmpty());
        assertTrue(context.catalog().isEmpty());
    }

    @Test
    void testClearRules() {
        Dialect dialect = SqlGlot.getDialect("ANSI");
        OptimizerContext context = OptimizerContext.of(dialect);
        Optimizer optimizer = new Optimizer(context);

        optimizer.addRule((expr, ctx) -> expr);
        optimizer.addRule((expr, ctx) -> expr);
        assertEquals(2, optimizer.getRules().size());

        optimizer.clearRules();
        assertEquals(0, optimizer.getRules().size());
    }

    @Test
    void testOptimizerConfigDefault() {
        OptimizerConfig config = OptimizerConfig.DEFAULT;

        assertTrue(config.simplify());
        assertTrue(config.canonicalize());
        assertTrue(config.quoteIdentifiers());
        assertTrue(config.eliminateCtes());
    }

    @Test
    void testOptimizerContextWithConfig() {
        Dialect dialect = SqlGlot.getDialect("ANSI");
        OptimizerConfig customConfig = new OptimizerConfig(
                true, false, true, false, false, false, false, false, false, false, false
        );
        OptimizerContext context = OptimizerContext.of(dialect, customConfig);

        assertTrue(context.config().simplify());
        assertFalse(context.config().canonicalize());
        assertTrue(context.config().quoteIdentifiers());
        assertFalse(context.config().eliminateCtes());
    }
}
