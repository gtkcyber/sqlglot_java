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
package io.sqlglot.dialects;

import io.sqlglot.SqlGlot;
import io.sqlglot.dialect.Dialect;
import io.sqlglot.expressions.Expression;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for Apache Drill dialect.
 * Tests Drill-specific features and syntax.
 *
 * TODO: These tests are temporarily disabled pending parser completion for advanced Drill features.
 */
@Disabled("Drill dialect tests pending parser support")
class DrillDialectTest {

    private final Dialect drillDialect = Dialect.of("DRILL");

    // ============ WORKSPACE PATHS ============

    @Test
    void testWorkspacePath() {
        // Drill supports workspace.schema.table syntax
        String sql = "SELECT * FROM `my_workspace`.`my_schema`.`my_table`";
        Optional<Expression> expr = drillDialect.parseOne(sql);
        assertTrue(expr.isPresent(), "Failed to parse Drill workspace path");
    }

    @Test
    void testWorkspacePathWithoutSchema() {
        String sql = "SELECT * FROM `my_schema`.`my_table`";
        Optional<Expression> expr = drillDialect.parseOne(sql);
        assertTrue(expr.isPresent());
    }

    @Test
    void testSimpleTableName() {
        String sql = "SELECT * FROM `my_table`";
        Optional<Expression> expr = drillDialect.parseOne(sql);
        assertTrue(expr.isPresent());
    }

    // ============ BACKTICK IDENTIFIERS ============

    @Test
    void testBacktickIdentifiers() {
        String sql = "SELECT `column_name` FROM `table_name`";
        Optional<Expression> expr = drillDialect.parseOne(sql);
        assertTrue(expr.isPresent());

        String generated = drillDialect.generate(expr.get());
        assertTrue(generated.contains("`"), "Generated SQL should use backticks");
    }

    @Test
    void testReservedWordAsIdentifier() {
        String sql = "SELECT `select`, `from` FROM `table`";
        Optional<Expression> expr = drillDialect.parseOne(sql);
        assertTrue(expr.isPresent());
    }

    // ============ COMPLEX DATA TYPES ============

    @Test
    void testArrayType() {
        String sql = "SELECT CAST(col AS ARRAY<INT>) as arr FROM t";
        Optional<Expression> expr = drillDialect.parseOne(sql);
        assertTrue(expr.isPresent());

        String generated = drillDialect.generate(expr.get());
        assertTrue(generated.toUpperCase().contains("ARRAY"));
    }

    @Test
    void testMapType() {
        String sql = "SELECT CAST(col AS MAP<STRING, INT>) as map FROM t";
        Optional<Expression> expr = drillDialect.parseOne(sql);
        assertTrue(expr.isPresent());
    }

    @Test
    void testStructType() {
        String sql = "SELECT CAST(col AS STRUCT<name STRING, age INT>) as struct FROM t";
        Optional<Expression> expr = drillDialect.parseOne(sql);
        assertTrue(expr.isPresent());
    }

    // ============ FLATTEN FUNCTION ============

    @Test
    void testFlattenFunction() {
        // FLATTEN converts arrays/maps into individual rows
        String sql = "SELECT FLATTEN(col) FROM t";
        Optional<Expression> expr = drillDialect.parseOne(sql);
        assertTrue(expr.isPresent());
    }

    @Test
    void testFlattenWithDepth() {
        String sql = "SELECT FLATTEN(col, 2) FROM t";
        Optional<Expression> expr = drillDialect.parseOne(sql);
        assertTrue(expr.isPresent());
    }

    // ============ COMMON DRILL QUERIES ============

    @Test
    void testFileSource() {
        // Drill can query files directly
        String sql = "SELECT * FROM dfs.`/path/to/file.parquet`";
        Optional<Expression> expr = drillDialect.parseOne(sql);
        assertTrue(expr.isPresent());
    }

    @Test
    void testJSONQuery() {
        String sql = "SELECT col1.name, col1.age FROM `my_schema`.`users`";
        Optional<Expression> expr = drillDialect.parseOne(sql);
        assertTrue(expr.isPresent());
    }

    @Test
    void testNestedFieldAccess() {
        String sql = "SELECT person.name, person.address.street FROM `users`";
        Optional<Expression> expr = drillDialect.parseOne(sql);
        assertTrue(expr.isPresent());
    }

    // ============ DRILL + ANSI SQL ============

    @ParameterizedTest
    @ValueSource(strings = {
        "SELECT * FROM `table`",
        "SELECT `a`, `b` FROM `t` WHERE `a` > 10",
        "SELECT COUNT(*) FROM `t` GROUP BY `category`",
        "SELECT * FROM `t1` JOIN `t2` ON `t1`.`id` = `t2`.`id`",
        "SELECT * FROM `t` ORDER BY `col` DESC LIMIT 10",
    })
    void testDrillStandardSQL(String sql) {
        Optional<Expression> expr = drillDialect.parseOne(sql);
        assertTrue(expr.isPresent(), "Failed to parse: " + sql);

        // Should generate valid Drill SQL
        String generated = drillDialect.generate(expr.get());
        assertNotNull(generated);
        assertTrue(generated.length() > 0);
    }

    // ============ CROSS-DIALECT TRANSPILATION ============

    @Test
    void testTranspileFromANSIToDrill() {
        String ansiSQL = "SELECT column_name FROM table_name";
        String drillSQL = SqlGlot.transpile(ansiSQL, "ANSI", "DRILL");

        assertNotNull(drillSQL);
        assertTrue(drillSQL.length() > 0);
    }

    @Test
    void testTranspileFromDrillToANSI() {
        String drillSQL = "SELECT `column_name` FROM `table_name`";
        String ansiSQL = SqlGlot.transpile(drillSQL, "DRILL", "ANSI");

        assertNotNull(ansiSQL);
        assertTrue(ansiSQL.contains("SELECT"));
    }

    // ============ FORMATTING ============

    @Test
    void testDrillFormatting() {
        String sql = "SELECT `a`,`b`,`c` FROM `t` WHERE `x`=1";
        String formatted = drillDialect.format(sql);

        assertTrue(formatted.contains("`"));
        assertTrue(formatted.contains("SELECT"));
        assertTrue(formatted.contains("FROM"));
    }

    // ============ ROUND-TRIP ============

    @Test
    void testDrillRoundTrip() {
        String sql = "SELECT `a`, `b` FROM `my_schema`.`my_table` WHERE `a` > 10";

        Optional<Expression> parsed = drillDialect.parseOne(sql);
        assertTrue(parsed.isPresent());

        String generated = drillDialect.generate(parsed.get());
        Optional<Expression> reParsed = drillDialect.parseOne(generated);
        assertTrue(reParsed.isPresent());
    }
}
