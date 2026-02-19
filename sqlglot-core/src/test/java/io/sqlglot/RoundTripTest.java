package io.sqlglot;

import io.sqlglot.expressions.Expression;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Round-trip tests: SQL → Parse → Generate → SQL should be similar.
 */
class RoundTripTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "SELECT a FROM t",
        "SELECT a, b FROM t",
        "SELECT * FROM t",
        "SELECT a FROM t WHERE x = 1",
        "SELECT a FROM t WHERE x = 1 AND y > 2",
        "SELECT a FROM t WHERE a IS NULL",
        "SELECT a FROM t WHERE a IN (1, 2, 3)",
        "SELECT a FROM t GROUP BY a",
        "SELECT a FROM t ORDER BY a",
        "SELECT a FROM t ORDER BY a DESC",
        "SELECT a FROM t LIMIT 10",
        "SELECT COUNT(*) FROM t GROUP BY a",
        "SELECT CAST(a AS INT) FROM t",
        "SELECT CASE WHEN a > 1 THEN 'yes' ELSE 'no' END FROM t",
        "SELECT 1 + 2 * 3 FROM t",
        "INSERT INTO t (a, b) (1, 2)",
        "UPDATE t SET a = 1 WHERE b = 2",
        "DELETE FROM t WHERE a = 1",
    })
    void testRoundTrip(String sql) {
        // Parse the SQL
        Optional<Expression> parsed = SqlGlot.parseOne(sql);
        assertTrue(parsed.isPresent(), "Failed to parse: " + sql);

        // Generate it back to SQL
        String generated = SqlGlot.generate(parsed.get());

        // Parse the generated SQL to ensure it's valid
        Optional<Expression> reParsed = SqlGlot.parseOne(generated);
        assertTrue(reParsed.isPresent(), "Failed to parse generated SQL: " + generated);
    }

    @Test
    void testComplexQuery() {
        String sql = "SELECT a, b, COUNT(*) FROM t WHERE x = 1 AND y > 2 GROUP BY a, b ORDER BY a DESC LIMIT 10";

        Optional<Expression> parsed = SqlGlot.parseOne(sql);
        assertTrue(parsed.isPresent());

        String generated = SqlGlot.generate(parsed.get());
        assertTrue(generated.contains("SELECT"));
        assertTrue(generated.contains("FROM"));
        assertTrue(generated.contains("WHERE"));
        assertTrue(generated.contains("GROUP BY"));
        assertTrue(generated.contains("ORDER BY"));
        assertTrue(generated.contains("LIMIT"));
    }

    @Test
    void testTranspilation() {
        String sql = "SELECT a FROM t";
        String transpiled = SqlGlot.transpile(sql, "ANSI", "ANSI");
        assertTrue(transpiled.contains("SELECT"));
        assertTrue(transpiled.contains("FROM"));
    }

    @Test
    void testFormatting() {
        String sql = "SELECT a FROM t WHERE x=1";
        String formatted = SqlGlot.format(sql);
        assertTrue(formatted.contains("SELECT"));
    }
}
