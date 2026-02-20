# Template Usage Example: Implementing SQLite Dialect

This document shows a step-by-step example of implementing the SQLite dialect using the template scaffold system.

## Overview

SQLite is a simple, embedded SQL database with these characteristics:

- **Identifier Quoting**: Supports double quotes, backticks, and square brackets
- **String Literals**: Single quotes with doubled quote escaping
- **Key Features**: PRAGMA statements, AUTOINCREMENT, ATTACH DATABASE
- **Functions**: IFNULL, TYPEOF, RANDOM, DATETIME functions
- **Data Types**: NULL, INTEGER, REAL, TEXT, BLOB, NUMERIC

## Step 1: Generate Template Files

```bash
cd /path/to/sqlglot_java
java com.gtkcyber.sqlglot.dialects.template.DialectScaffold \
  SQLite sqlite \
  sqlglot-dialects/src/main/java/com/gtkcyber/sqlglot/dialects/
```

This creates:
- `sqlglot-dialects/src/main/java/com/gtkcyber/sqlglot/dialects/sqlite/SqliteDialect.java`
- `sqlglot-dialects/src/main/java/com/gtkcyber/sqlglot/dialects/sqlite/SqliteParser.java`
- `sqlglot-dialects/src/main/java/com/gtkcyber/sqlglot/dialects/sqlite/SqliteGenerator.java`
- `sqlglot-dialects/src/main/java/com/gtkcyber/sqlglot/dialects/sqlite/SqliteTokenizer.java`

## Step 2: Implement SqliteTokenizer

**File**: `SqliteTokenizer.java`

The tokenizer needs to recognize SQLite-specific keywords and identifier quoting styles.

```java
@Override
protected Map<String, Character> getIdentifierQuotes() {
    Map<String, Character> quotes = new HashMap<>();
    // SQLite supports three quoting styles for identifiers
    quotes.put("\"", '"');   // "column_name"
    quotes.put("`", '`');    // `column_name`
    quotes.put("[", ']');    // [column_name]
    return quotes;
}

@Override
protected Map<String, Character> getStringQuotes() {
    Map<String, Character> quotes = new HashMap<>();
    // SQLite uses single quotes for strings, with doubled quotes for escaping
    quotes.put("'", '\'');   // 'string' with '' for escaped quote
    return quotes;
}

@Override
protected Trie buildKeywordTrie() {
    Map<String, TokenType> keywords = new HashMap<>();

    // Add all standard SQL keywords
    for (TokenType type : TokenType.values()) {
        if (type.isKeyword() && type.getText() != null) {
            keywords.put(type.getText(), type);
        }
    }

    // Add SQLite-specific keywords
    keywords.put("PRAGMA", TokenType.IDENTIFIER);           // PRAGMA table_info(...)
    keywords.put("ATTACH", TokenType.IDENTIFIER);           // ATTACH DATABASE
    keywords.put("DATABASE", TokenType.IDENTIFIER);         // ATTACH DATABASE
    keywords.put("DETACH", TokenType.IDENTIFIER);           // DETACH DATABASE
    keywords.put("AUTOINCREMENT", TokenType.IDENTIFIER);    // Primary key option
    keywords.put("COLLATE", TokenType.IDENTIFIER);          // Collation
    keywords.put("CONFLICT", TokenType.IDENTIFIER);         // Conflict resolution
    keywords.put("GLOB", TokenType.IDENTIFIER);             // Pattern matching
    keywords.put("REGEXP", TokenType.IDENTIFIER);           // Regex matching
    keywords.put("VACUUM", TokenType.IDENTIFIER);           // Database optimization
    keywords.put("ANALYZE", TokenType.IDENTIFIER);          // Table analysis
    keywords.put("EXPLAIN", TokenType.IDENTIFIER);          // Query plan
    keywords.put("QUERY", TokenType.IDENTIFIER);            // EXPLAIN QUERY PLAN
    keywords.put("PLAN", TokenType.IDENTIFIER);

    // SQLite-specific functions
    keywords.put("IFNULL", TokenType.IDENTIFIER);
    keywords.put("TYPEOF", TokenType.IDENTIFIER);
    keywords.put("RANDOM", TokenType.IDENTIFIER);
    keywords.put("ABS", TokenType.IDENTIFIER);
    keywords.put("ROUND", TokenType.IDENTIFIER);
    keywords.put("TRIM", TokenType.IDENTIFIER);
    keywords.put("LTRIM", TokenType.IDENTIFIER);
    keywords.put("RTRIM", TokenType.IDENTIFIER);
    keywords.put("UPPER", TokenType.IDENTIFIER);
    keywords.put("LOWER", TokenType.IDENTIFIER);
    keywords.put("SUBSTR", TokenType.IDENTIFIER);
    keywords.put("LENGTH", TokenType.IDENTIFIER);
    keywords.put("INSTR", TokenType.IDENTIFIER);
    keywords.put("REPLACE", TokenType.IDENTIFIER);
    keywords.put("CAST", TokenType.IDENTIFIER);
    keywords.put("COALESCE", TokenType.IDENTIFIER);
    keywords.put("NULLIF", TokenType.IDENTIFIER);
    keywords.put("MAX", TokenType.IDENTIFIER);
    keywords.put("MIN", TokenType.IDENTIFIER);
    keywords.put("COUNT", TokenType.IDENTIFIER);
    keywords.put("SUM", TokenType.IDENTIFIER);
    keywords.put("AVG", TokenType.IDENTIFIER);
    keywords.put("TOTAL", TokenType.IDENTIFIER);
    keywords.put("GROUP_CONCAT", TokenType.IDENTIFIER);

    // Date/time functions
    keywords.put("DATE", TokenType.IDENTIFIER);
    keywords.put("TIME", TokenType.IDENTIFIER);
    keywords.put("DATETIME", TokenType.IDENTIFIER);
    keywords.put("JULIANDAY", TokenType.IDENTIFIER);
    keywords.put("STRFTIME", TokenType.IDENTIFIER);
    keywords.put("STRPTIME", TokenType.IDENTIFIER);

    return Trie.build(keywords);
}
```

## Step 3: Implement SqliteGenerator

**File**: `SqliteGenerator.java`

The generator formats the AST back to valid SQLite SQL.

```java
@Override
protected String formatIdentifier(String name) {
    // Use double quotes (SQL standard style, works in SQLite)
    return "\"" + name.replace("\"", "\"\"") + "\"";
}

@Override
protected Map<Class<? extends Expression>, Function<Expression, String>> transforms() {
    Map<Class<? extends Expression>, Function<Expression, String>> map = new HashMap<>();

    // SQLite-specific CAST syntax
    map.put(Nodes.Cast.class, expr -> {
        Nodes.Cast cast = (Nodes.Cast) expr;
        String type = sql(cast.getDataType());
        String expression = sql(cast.getExpression());

        // SQLite: CAST (expr AS type)
        return formatKeyword("CAST") + "(" + expression +
               " " + formatKeyword("AS") + " " + type + ")";
    });

    // Handle PRAGMA statements
    map.put(Nodes.Pragma.class, expr -> {
        Nodes.Pragma pragma = (Nodes.Pragma) expr;
        StringBuilder sb = new StringBuilder();
        sb.append(formatKeyword("PRAGMA")).append(" ");
        sb.append(pragma.getName());

        if (!pragma.getArgs().isEmpty()) {
            sb.append("(");
            boolean first = true;
            for (Expression arg : pragma.getArgs()) {
                if (!first) sb.append(", ");
                sb.append(sql(arg));
                first = false;
            }
            sb.append(")");
        }

        return sb.toString();
    });

    return map;
}

// Optional: Override to handle PRAGMA statements specifically
// public String visitPragma(Nodes.Pragma node) {
//     // Custom PRAGMA handling if needed
//     return super.visitFunction(node);
// }
```

## Step 4: Implement SqliteParser

**File**: `SqliteParser.java`

For basic SQLite, you can often use the default parser. Here's an enhanced version:

```java
public class SqliteParser extends Parser {
    public SqliteParser() {
        super(ParserConfig.defaultConfig());
    }

    public SqliteParser(ParserConfig config) {
        super(config);
    }

    // Most SQLite SQL can be parsed with the base parser
    // Only override if you need special handling for:
    // 1. PRAGMA statements
    // 2. ATTACH DATABASE syntax
    // 3. Window function OVER clauses (SQLite 3.25+)

    // Example: Override for PRAGMA statements
    // @Override
    // public Optional<Expression> parseStatement() {
    //     if (match("PRAGMA")) {
    //         return Optional.of(parsePragmaStatement());
    //     }
    //     return super.parseStatement();
    // }
    //
    // private Expression parsePragmaStatement() {
    //     String pragmaName = match().text();
    //     List<Expression> args = new ArrayList<>();
    //     if (match("(")) {
    //         args.add(parseExpression());
    //         while (match(",")) {
    //             args.add(parseExpression());
    //         }
    //         expect(")");
    //     }
    //     return new Nodes.Pragma(pragmaName, args);
    // }
}
```

## Step 5: Create SqliteDialect

**File**: `SqliteDialect.java`

The main dialect class ties everything together:

```java
public class SqliteDialect extends Dialect {
    /**
     * SQLite dialect using UPPERCASE normalization.
     */
    public SqliteDialect() {
        super("SQLITE", NormalizationStrategy.UPPERCASE);
    }

    @Override
    public Tokenizer createTokenizer() {
        return new SqliteTokenizer();
    }

    @Override
    public Parser createParser() {
        return new SqliteParser();
    }

    @Override
    public Generator createGenerator(GeneratorConfig config) {
        return new SqliteGenerator(config);
    }
}
```

## Step 6: Create Test Cases

**File**: `SqliteDialectTest.java`

Create comprehensive tests in `sqlglot-dialects/src/test/java/com/gtkcyber/sqlglot/dialects/`

```java
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.gtkcyber.sqlglot.expressions.Expression;
import com.gtkcyber.sqlglot.dialects.sqlite.SqliteDialect;

import java.util.List;

public class SqliteDialectTest {
    private SqliteDialect dialect;

    @Before
    public void setUp() {
        dialect = new SqliteDialect();
    }

    // Basic SQL Tests
    @Test
    public void testBasicSelect() {
        String sql = "SELECT * FROM users";
        List<Expression> statements = dialect.parse(sql);
        assertEquals(1, statements.size());
        assertNotNull(statements.get(0));
    }

    @Test
    public void testSelectWithWhere() {
        String sql = "SELECT id, name FROM users WHERE id > 0";
        List<Expression> statements = dialect.parse(sql);
        assertEquals(1, statements.size());
    }

    @Test
    public void testSelectWithJoin() {
        String sql = "SELECT u.id, o.amount FROM users u JOIN orders o ON u.id = o.user_id";
        List<Expression> statements = dialect.parse(sql);
        assertEquals(1, statements.size());
    }

    // Identifier Quoting Tests
    @Test
    public void testDoubleQuotedIdentifiers() {
        String sql = "SELECT \"column_name\" FROM \"table_name\"";
        List<Expression> statements = dialect.parse(sql);
        String generated = dialect.generate(statements.get(0));
        assertTrue(generated.contains("\"column_name\""));
    }

    @Test
    public void testBacktickIdentifiers() {
        String sql = "SELECT `column_name` FROM `table_name`";
        List<Expression> statements = dialect.parse(sql);
        String generated = dialect.generate(statements.get(0));
        // Should be normalized to double quotes
        assertTrue(generated.contains("column_name"));
    }

    @Test
    public void testBracketIdentifiers() {
        String sql = "SELECT [column_name] FROM [table_name]";
        List<Expression> statements = dialect.parse(sql);
        String generated = dialect.generate(statements.get(0));
        assertTrue(generated.contains("column_name"));
    }

    // SQLite-Specific Tests
    @Test
    public void testAutoincrement() {
        String sql = "CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)";
        List<Expression> statements = dialect.parse(sql);
        assertEquals(1, statements.size());
    }

    @Test
    public void testIfnullFunction() {
        String sql = "SELECT IFNULL(column_name, 0) FROM table";
        List<Expression> statements = dialect.parse(sql);
        assertEquals(1, statements.size());
    }

    @Test
    public void testTypeofFunction() {
        String sql = "SELECT TYPEOF(column) FROM table";
        List<Expression> statements = dialect.parse(sql);
        assertEquals(1, statements.size());
    }

    @Test
    public void testRandomFunction() {
        String sql = "SELECT RANDOM() AS random_number";
        List<Expression> statements = dialect.parse(sql);
        assertEquals(1, statements.size());
    }

    @Test
    public void testDatetimeFunction() {
        String sql = "SELECT DATETIME('now')";
        List<Expression> statements = dialect.parse(sql);
        assertEquals(1, statements.size());
    }

    @Test
    public void testStrftimeFunction() {
        String sql = "SELECT STRFTIME('%Y-%m-%d', 'now')";
        List<Expression> statements = dialect.parse(sql);
        assertEquals(1, statements.size());
    }

    @Test
    public void testGroupConcatFunction() {
        String sql = "SELECT GROUP_CONCAT(name, ',') FROM users";
        List<Expression> statements = dialect.parse(sql);
        assertEquals(1, statements.size());
    }

    // Type Tests
    @Test
    public void testDataTypes() {
        String sql = "CREATE TABLE test (id INTEGER, text TEXT, float REAL, data BLOB, num NUMERIC)";
        List<Expression> statements = dialect.parse(sql);
        assertEquals(1, statements.size());
    }

    @Test
    public void testCastFunction() {
        String sql = "SELECT CAST(value AS TEXT) FROM table";
        List<Expression> statements = dialect.parse(sql);
        assertEquals(1, statements.size());
    }

    // String Literal Tests
    @Test
    public void testSingleQuotedStrings() {
        String sql = "SELECT 'hello world' FROM table";
        List<Expression> statements = dialect.parse(sql);
        assertEquals(1, statements.size());
    }

    @Test
    public void testEscapedQuotesInStrings() {
        String sql = "SELECT 'It''s a string with quotes' FROM table";
        List<Expression> statements = dialect.parse(sql);
        assertEquals(1, statements.size());
    }

    // Round Trip Tests
    @Test
    public void testRoundTrip() {
        String sql = "SELECT col1, col2 FROM table WHERE id = 123";
        List<Expression> statements = dialect.parse(sql);
        String generated = dialect.generate(statements.get(0));

        // Should be able to parse the generated SQL
        List<Expression> reparsed = dialect.parse(generated);
        assertEquals(1, reparsed.size());
    }

    // Complex Query Tests
    @Test
    public void testComplexQuery() {
        String sql = "SELECT u.id, u.name, COUNT(o.id) as order_count " +
                     "FROM users u " +
                     "LEFT JOIN orders o ON u.id = o.user_id " +
                     "WHERE u.active = 1 " +
                     "GROUP BY u.id, u.name " +
                     "HAVING COUNT(o.id) > 5 " +
                     "ORDER BY order_count DESC";
        List<Expression> statements = dialect.parse(sql);
        assertEquals(1, statements.size());
    }

    // Error Handling (optional)
    @Test
    public void testInvalidSqlHandling() {
        // SQLite might not throw on all invalid SQL, depends on implementation
        String sql = "SELECT * FROM";
        try {
            dialect.parse(sql);
            // May or may not throw depending on error handling strategy
        } catch (Exception e) {
            // Expected for truly invalid SQL
        }
    }
}
```

## Step 7: Test and Verify

Run the tests to ensure everything works:

```bash
mvn test -Dtest=SqliteDialectTest
```

## Complete File Checklist

After following these steps, you should have:

- [ ] `SqliteDialect.java` - Main dialect class
- [ ] `SqliteParser.java` - Parser with SQLite-specific extensions
- [ ] `SqliteGenerator.java` - Generator with SQLite-specific formatting
- [ ] `SqliteTokenizer.java` - Tokenizer with SQLite keywords
- [ ] `SqliteDialectTest.java` - Comprehensive test cases
- [ ] Documentation comments explaining SQLite specifics

## Sample Generated Code

When you run the scaffold tool, you get template files with placeholders replaced:

```java
// Before (template with placeholders)
public class {DIALECT_CLASS_NAME} extends Dialect {
    public {DIALECT_CLASS_NAME}() {
        super("{DIALECT_NAME_UPPER}", NormalizationStrategy.UPPERCASE);
    }
}

// After (with SQLite placeholders replaced)
public class SqliteDialect extends Dialect {
    public SqliteDialect() {
        super("SQLITE", NormalizationStrategy.UPPERCASE);
    }
}
```

## Time Estimate

Following this example should take approximately:
- Template generation: 1 minute
- Tokenizer implementation: 3-4 minutes
- Generator implementation: 2-3 minutes
- Parser implementation: 2-3 minutes (usually minimal for simple dialects)
- Test creation: 3-5 minutes
- Testing and debugging: 3-5 minutes

**Total: 14-21 minutes**

## Next Steps

Once you have a working SQLite implementation:

1. **Extend Parser**: Add support for SQLite-specific statements like PRAGMA
2. **Add More Tests**: Test edge cases and complex queries
3. **Performance Testing**: Test with large SQL files
4. **Documentation**: Add detailed comments explaining dialect features
5. **Integration**: Register the dialect in the main Dialect registry

## Resources

- SQLite Documentation: https://www.sqlite.org/docs.html
- SQL Syntax Guide: https://www.sqlite.org/lang.html
- Data Types: https://www.sqlite.org/datatype3.html
- Functions: https://www.sqlite.org/lang_corefunc.html
