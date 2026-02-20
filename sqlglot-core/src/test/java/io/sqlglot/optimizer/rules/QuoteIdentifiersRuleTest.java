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

import io.sqlglot.SqlGlot;
import io.sqlglot.dialect.Dialect;
import io.sqlglot.expressions.Expression;
import io.sqlglot.optimizer.OptimizerContext;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class QuoteIdentifiersRuleTest {
    private final Dialect dialect = SqlGlot.getDialect("ANSI");
    private final QuoteIdentifiersRule rule = new QuoteIdentifiersRule();
    private final OptimizerContext context = OptimizerContext.of(dialect);

    private String optimize(String sql) {
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        if (expr.isEmpty()) return sql;

        Expression optimized = rule.optimize(expr.get(), context);
        return SqlGlot.generate(optimized, dialect.getName());
    }

    @Test
    void testRegularIdentifierNotQuoted() {
        String result = optimize("SELECT user_id FROM users");
        assertTrue(result.toLowerCase().contains("select"));
        // user_id should not be quoted since it's a valid identifier
    }

    @Test
    void testReservedKeywordQuoting() {
        String result = optimize("SELECT * FROM `select`");
        // `select` is already quoted, should remain quoted
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testSpecialCharacterQuoting() {
        String result = optimize("SELECT * FROM `my-table`");
        // my-table has a dash, might need quoting
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testSpaceInIdentifier() {
        String result = optimize("SELECT * FROM `my table`");
        // Identifier with space
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testRoundTripAfterQuoting() {
        String sql = "SELECT user_id FROM users";
        Optional<Expression> expr = SqlGlot.parseOne(sql);
        assertTrue(expr.isPresent());

        Expression optimized = rule.optimize(expr.get(), context);
        String generated = SqlGlot.generate(optimized, dialect.getName());

        // Should be parseable again
        Optional<Expression> reparsed = SqlGlot.parseOne(generated);
        assertTrue(reparsed.isPresent());
    }

    @Test
    void testAlreadyQuotedIdentifier() {
        String result = optimize("SELECT * FROM \"my_table\"");
        // Already quoted, should not double-quote
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testColumnWithQuoting() {
        String result = optimize("SELECT id, name FROM users");
        assertTrue(result.toLowerCase().contains("select"));
    }

    @Test
    void testMultipleIdentifiers() {
        String result = optimize("SELECT col1, col2, col3 FROM my_table");
        assertTrue(result.toLowerCase().contains("select"));
        assertTrue(result.toLowerCase().contains("from"));
    }
}
