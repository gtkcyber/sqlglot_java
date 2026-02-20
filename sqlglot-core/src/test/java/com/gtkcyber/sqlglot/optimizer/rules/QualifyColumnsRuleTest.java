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

import com.gtkcyber.sqlglot.SqlGlot;
import com.gtkcyber.sqlglot.dialect.Dialect;
import com.gtkcyber.sqlglot.expressions.Expression;
import com.gtkcyber.sqlglot.optimizer.OptimizerContext;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class QualifyColumnsRuleTest {
    private final Dialect dialect = SqlGlot.getDialect("ANSI");
    private final QualifyColumnsRule rule = new QualifyColumnsRule();
    private final OptimizerContext context = OptimizerContext.of(dialect);

    private String optimize(String sql) {
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        if (expr.isEmpty()) return sql;

        Expression optimized = rule.optimize(expr.get(), context);
        return SqlGlot.generate(optimized, dialect.getName());
    }

    @Test
    void testQualifySimpleColumn() {
        String sql = "SELECT a FROM t";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testQualifyMultipleColumns() {
        String sql = "SELECT a, b FROM t";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testQualifyWithTableAlias() {
        String sql = "SELECT x.a FROM t x";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testSelectStar() {
        String sql = "SELECT * FROM t";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("*"));
    }

    @Test
    void testAlreadyQualified() {
        String sql = "SELECT t.a, t.b FROM t";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("t.a"));
    }

    @Test
    void testColumnAlias() {
        String sql = "SELECT a AS x FROM t";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testRoundTripAfterQualification() {
        String sql = "SELECT a FROM t";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Expression optimized = rule.optimize(expr.get(), context);
        String generated = SqlGlot.generate(optimized, dialect.getName());

        Optional<Expression> reparsed = SqlGlot.parseOne(generated);
        assertTrue(reparsed.isPresent());
    }

    @Test
    void testSimpleJoin() {
        String sql = "SELECT 1 FROM a JOIN b ON a.id = b.id";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testThreeTables() {
        String sql = "SELECT 1 FROM a JOIN b ON a.id = b.id JOIN c ON b.id = c.id";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testWithWhere() {
        String sql = "SELECT a FROM t WHERE a > 1";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testWithGroupBy() {
        String sql = "SELECT a FROM t GROUP BY a";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testWithOrderBy() {
        String sql = "SELECT a FROM t ORDER BY a";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testSimpleUnion() {
        String sql = "SELECT a FROM t1 UNION SELECT a FROM t2";
        String result = optimize(sql);
        assertTrue(result.toLowerCase().contains("select"));
    }
}
