# SQL Dialect Implementation Guide

This guide provides a comprehensive walkthrough for implementing new SQL dialects in sqlglot-java using the template scaffold system.

## Table of Contents

1. [Quick Start (15-20 minutes)](#quick-start)
2. [Template System Overview](#template-system-overview)
3. [Dialect Components](#dialect-components)
4. [Implementation Steps](#implementation-steps)
5. [Example: Implementing SQLite](#example-implementing-sqlite)
6. [Testing](#testing)
7. [Common Dialect-Specific Features](#common-dialect-specific-features)
8. [Best Practices](#best-practices)

## Quick Start

### Using the DialectScaffold Tool

The fastest way to implement a new dialect is using the `DialectScaffold` utility:

```bash
# Generate scaffold for a new dialect
java com.gtkcyber.sqlglot.dialects.template.DialectScaffold SQLite sqlite \
  sqlglot-dialects/src/main/java/com/gtkcyber/sqlglot/dialects/
```

This creates:
- `SqliteDialect.java` - Main dialect class
- `SqliteParser.java` - Dialect-specific parser
- `SqliteGenerator.java` - Dialect-specific code generator
- `SqliteTokenizer.java` - Dialect-specific tokenizer

### Manual Template Replacement

If you prefer manual setup:

1. Copy template files to your dialect directory
2. Replace placeholders with dialect-specific values
3. Implement dialect-specific behavior

## Template System Overview

The scaffold system uses template placeholders that are automatically replaced:

| Placeholder | Example | Purpose |
|------------|---------|---------|
| `{DIALECT_NAME}` | `SQLite` | Display name |
| `{DIALECT_NAME_UPPER}` | `SQLITE` | Uppercase for constants |
| `{DIALECT_NAME_LOWER}` | `sqlite` | Lowercase for paths |
| `{PACKAGE_PATH}` | `sqlite` | Java package component |
| `{DIALECT_CLASS_NAME}` | `SqliteDialect` | Main dialect class name |
| `{PARSER_CLASS_NAME}` | `SqliteParser` | Parser class name |
| `{GENERATOR_CLASS_NAME}` | `SqliteGenerator` | Generator class name |
| `{TOKENIZER_CLASS_NAME}` | `SqliteTokenizer` | Tokenizer class name |

## Dialect Components

### 1. Dialect Class

**Purpose**: Entry point for the dialect. Creates instances of Parser, Generator, and Tokenizer.

**Key Methods**:
- `createTokenizer()` - Returns tokenizer instance
- `createParser()` - Returns parser instance
- `createGenerator(GeneratorConfig)` - Returns generator instance

**Constructor Parameters**:
- Dialect name (used for registration)
- `NormalizationStrategy` - How identifiers are normalized:
  - `UPPERCASE` - Convert to uppercase (most common)
  - `LOWERCASE` - Convert to lowercase
  - `PRESERVE` - Keep original case

**Example**:
```java
public class SqliteDialect extends Dialect {
    public SqliteDialect() {
        super("SQLITE", NormalizationStrategy.UPPERCASE);
    }
}
```

### 2. Tokenizer Class

**Purpose**: Breaks SQL text into tokens (keywords, identifiers, literals, operators).

**Key Methods to Override**:
- `getIdentifierQuotes()` - Define identifier quoting style
- `getStringQuotes()` - Define string literal quoting style
- `buildKeywordTrie()` - Define dialect keywords

**Identifier Quoting Styles**:
```java
// Double quotes (SQL standard)
quotes.put("\"", '"');

// Backticks (MySQL, Drill style)
quotes.put("`", '`');

// Square brackets (SQL Server style)
quotes.put("[", ']');
```

**String Quoting Styles**:
```java
// Single quotes with doubled quote escaping
quotes.put("'", '\'');  // 'string' -> ''string''

// Backslash escaping
quotes.put("'", '\\');  // 'string' -> \'string\'
```

**Example Keywords**:
```java
keywords.put("SELECT", TokenType.SELECT);
keywords.put("FROM", TokenType.FROM);
keywords.put("WHERE", TokenType.WHERE);
keywords.put("PRAGMA", TokenType.IDENTIFIER);  // SQLite-specific
keywords.put("ATTACH", TokenType.IDENTIFIER);  // SQLite-specific
```

### 3. Parser Class

**Purpose**: Converts tokens into an Abstract Syntax Tree (AST).

**Key Methods to Override**:
- `parseStatement()` - Parse SQL statements (SELECT, INSERT, etc.)
- `parseExpression()` - Parse expressions
- `parseFunction()` - Parse function calls
- `parseDataType()` - Parse data type declarations
- `parsePrimary()` - Parse primary expressions (literals, identifiers, etc.)

**Common Parsing Patterns**:

```java
// Check for specific keyword
if (match("PRAGMA")) {
    return parsePragmaStatement();
}

// Parse conditional expressions
if (peek("WHEN")) {
    advance();
    Expression condition = parseExpression();
    // ... parse THEN clause
}

// Build composite nodes
List<Expression> args = new ArrayList<>();
while (!match(")")) {
    args.add(parseExpression());
    if (!match(",")) break;
}

return new Nodes.Function("name", args);
```

### 4. Generator Class

**Purpose**: Converts AST back to SQL text.

**Key Methods to Override**:
- `formatIdentifier(String)` - Quote identifiers with dialect style
- `transforms()` - Custom AST node transformations
- `visitFunction()` - Generate function calls
- `visitCast()` - Generate CAST expressions
- `visitDataType()` - Generate data type syntax

**Identifier Formatting**:
```java
// Double quotes
protected String formatIdentifier(String name) {
    return "\"" + name.replace("\"", "\"\"") + "\"";
}

// Backticks
protected String formatIdentifier(String name) {
    return "`" + name.replace("`", "``") + "`";
}
```

**Custom Transformations**:
```java
protected Map<Class<? extends Expression>, Function<Expression, String>> transforms() {
    Map<Class<? extends Expression>, Function<Expression, String>> map = new HashMap<>();

    map.put(Nodes.Function.class, expr -> {
        Nodes.Function func = (Nodes.Function) expr;
        if ("CUSTOM_FUNC".equalsIgnoreCase(func.getName())) {
            return "CUSTOM_FUNC(" + sql(func.getArgs().get(0)) + ")";
        }
        return null;  // Fall back to default
    });

    return map;
}
```

## Implementation Steps

### Step 1: Generate Scaffold

```bash
java com.gtkcyber.sqlglot.dialects.template.DialectScaffold \
  YourDialect yourdialect /path/to/output
```

### Step 2: Configure Tokenizer

In `YourDialectTokenizer.java`:

1. Override `getIdentifierQuotes()` to define how identifiers are quoted
2. Override `getStringQuotes()` to define how strings are quoted
3. Override `buildKeywordTrie()` to add dialect-specific keywords

**Checklist**:
- [ ] Identifier quoting style correct
- [ ] String quoting style correct
- [ ] All dialect keywords added
- [ ] Case sensitivity handled correctly

### Step 3: Implement Parser (if needed)

Most dialects can use the base Parser. Override methods only if you need dialect-specific syntax.

Common overrides:
- Statement types (e.g., `PRAGMA`, `ATTACH DATABASE`)
- Function syntax (e.g., different argument patterns)
- Operators (e.g., custom operators like `@@` in PostgreSQL)

**Checklist**:
- [ ] Custom statement types parsed
- [ ] Dialect-specific functions recognized
- [ ] Operator precedence correct
- [ ] Error messages clear

### Step 4: Implement Generator (if needed)

Most dialects need custom identifier formatting. Override methods for:
- Identifier quoting
- Function-specific syntax
- Type-specific syntax

**Checklist**:
- [ ] Identifiers formatted correctly
- [ ] Functions generate valid syntax
- [ ] Data types formatted correctly
- [ ] Generated SQL is valid

### Step 5: Create Tests

Create test files in `sqlglot-dialects/src/test/java/com/gtkcyber/sqlglot/dialects/`

**Test Template**:
```java
public class YourDialectTest {
    private YourDialect dialect = new YourDialect();

    @Test
    public void testBasicSelect() {
        String sql = "SELECT * FROM table";
        List<Expression> statements = dialect.parse(sql);
        assertEquals(1, statements.size());

        String generated = dialect.generate(statements.get(0));
        // Verify generated SQL is correct
    }

    @Test
    public void testDialectSpecificFeature() {
        String sql = "PRAGMA table_info(my_table)";  // SQLite example
        List<Expression> statements = dialect.parse(sql);
        assertNotNull(statements.get(0));
    }
}
```

## Example: Implementing SQLite

### 1. Generate Scaffold

```bash
java com.gtkcyber.sqlglot.dialects.template.DialectScaffold \
  SQLite sqlite \
  sqlglot-dialects/src/main/java/com/gtkcyber/sqlglot/dialects/
```

### 2. Configure Tokenizer

**File**: `SqliteTokenizer.java`

```java
@Override
protected Map<String, Character> getIdentifierQuotes() {
    Map<String, Character> quotes = new HashMap<>();
    quotes.put("\"", '"');
    quotes.put("`", '`');
    quotes.put("[", ']');
    return quotes;
}

@Override
protected Map<String, Character> getStringQuotes() {
    Map<String, Character> quotes = new HashMap<>();
    quotes.put("'", '\'');
    return quotes;
}

@Override
protected Trie buildKeywordTrie() {
    Map<String, TokenType> keywords = new HashMap<>();

    // Add standard keywords
    for (TokenType type : TokenType.values()) {
        if (type.isKeyword() && type.getText() != null) {
            keywords.put(type.getText(), type);
        }
    }

    // SQLite-specific keywords
    keywords.put("PRAGMA", TokenType.IDENTIFIER);
    keywords.put("ATTACH", TokenType.IDENTIFIER);
    keywords.put("AUTOINCREMENT", TokenType.IDENTIFIER);
    keywords.put("COLLATE", TokenType.IDENTIFIER);
    keywords.put("GLOB", TokenType.IDENTIFIER);
    keywords.put("REGEXP", TokenType.IDENTIFIER);

    return Trie.build(keywords);
}
```

### 3. Configure Generator

**File**: `SqliteGenerator.java`

```java
@Override
protected String formatIdentifier(String name) {
    return "\"" + name.replace("\"", "\"\"") + "\"";
}

@Override
protected Map<Class<? extends Expression>, Function<Expression, String>> transforms() {
    Map<Class<? extends Expression>, Function<Expression, String>> map = new HashMap<>();

    // SQLite-specific CAST syntax
    map.put(Nodes.Cast.class, expr -> {
        Nodes.Cast cast = (Nodes.Cast) expr;
        String type = sql(cast.getDataType());

        // SQLite: CAST (expr AS type)
        return formatKeyword("CAST") + "(" + sql(cast.getExpression()) +
               " " + formatKeyword("AS") + " " + type + ")";
    });

    return map;
}
```

### 4. Create Tests

**File**: `SqliteDialectTest.java`

```java
public class SqliteDialectTest {
    private SqliteDialect dialect = new SqliteDialect();

    @Test
    public void testBasicSelect() {
        String sql = "SELECT * FROM users WHERE id > 0";
        List<Expression> stmts = dialect.parse(sql);
        assertEquals(1, stmts.size());
    }

    @Test
    public void testAutoincrement() {
        String sql = "CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT)";
        List<Expression> stmts = dialect.parse(sql);
        assertNotNull(stmts.get(0));
    }

    @Test
    public void testPragma() {
        String sql = "PRAGMA table_info(users)";
        List<Expression> stmts = dialect.parse(sql);
        assertNotNull(stmts.get(0));
    }
}
```

## Testing

### Unit Tests

Create comprehensive tests for your dialect:

```java
// Test basic SQL parsing and generation
@Test
public void testRoundTrip() {
    String sql = "SELECT col1, col2 FROM table WHERE id = 123";
    List<Expression> stmts = dialect.parse(sql);
    String generated = dialect.generate(stmts.get(0));

    // Should parse correctly
    assertNotNull(stmts.get(0));

    // Generated SQL should be valid
    List<Expression> reparsed = dialect.parse(generated);
    assertNotNull(reparsed.get(0));
}

// Test dialect-specific features
@Test
public void testDialectFeature() {
    String sql = "DIALECT-SPECIFIC SQL";
    List<Expression> stmts = dialect.parse(sql);
    assertNotNull(stmts.get(0));
}

// Test error handling
@Test(expected = ParseException.class)
public void testInvalidSql() {
    String sql = "INVALID SQL SYNTAX";
    dialect.parse(sql);
}
```

### Test Patterns

```java
// Test identifier formatting
@Test
public void testIdentifierQuoting() {
    String sql = "SELECT \"column name\" FROM \"my table\"";
    List<Expression> stmts = dialect.parse(sql);
    String generated = dialect.generate(stmts.get(0));
    assertTrue(generated.contains("\"column name\""));
}

// Test function generation
@Test
public void testFunctionGeneration() {
    String sql = "SELECT CUSTOM_FUNC(arg1, arg2) FROM table";
    List<Expression> stmts = dialect.parse(sql);
    String generated = dialect.generate(stmts.get(0));
    assertTrue(generated.contains("CUSTOM_FUNC"));
}

// Test data type formatting
@Test
public void testDataTypes() {
    String sql = "CREATE TABLE t (id INTEGER, name TEXT, data BLOB)";
    List<Expression> stmts = dialect.parse(sql);
    assertNotNull(stmts.get(0));
}
```

## Common Dialect-Specific Features

### 1. Identifier Quoting

| Dialect | Style | Example |
|---------|-------|---------|
| PostgreSQL | Double quotes | `"column_name"` |
| MySQL | Backticks | `` `column_name` `` |
| SQL Server | Square brackets | `[column_name]` |
| SQLite | Double quotes, backticks, brackets | `"col"`, `` `col` ``, `[col]` |
| BigQuery | Backticks | `` `table.column` `` |

### 2. Keywords

Track dialect-specific keywords:

```java
// SQLite
PRAGMA, ATTACH, AUTOINCREMENT, COLLATE, GLOB, REGEXP

// PostgreSQL
ARRAY, JSONB, ENUM, RETURNS, LANGUAGE, IMMUTABLE, STABLE

// MySQL
ENGINE, CHARSET, COLLATE, LIMIT (in different positions)

// SQL Server
GO, EXEC, DECLARE, TRY, CATCH, OUTPUT

// Snowflake
STAGE, CLUSTER, TIME_ZONE, ACCOUNT, REGION
```

### 3. Functions

Add dialect-specific functions:

```java
// SQLite
IFNULL(), NULLIF(), TYPEOF(), RANDOM(), GLOB(), REGEXP()
DATE(), TIME(), DATETIME(), STRFTIME()

// PostgreSQL
ARRAY_AGG(), JSON_BUILD_OBJECT(), UNNEST()

// BigQuery
CURRENT_DATE(), CURRENT_TIMESTAMP(), ARRAY(), STRUCT()

// Snowflake
TRY_PARSE_JSON(), FLATTEN(), LATERAL FLATTEN()
```

### 4. Data Types

Support dialect-specific types:

```java
// SQLite
NULL, INTEGER, REAL, TEXT, BLOB, NUMERIC

// PostgreSQL
TEXT[], JSONB, UUID, BYTEA, INT4, INT8

// BigQuery
STRING, BYTES, STRUCT<>, ARRAY<>

// Snowflake
VARIANT, OBJECT, ARRAY
```

### 5. Operators

Handle dialect-specific operators:

```java
// PostgreSQL
||  (string concatenation)
::  (cast operator)
@>  (JSON containment)
@@  (full-text search)

// BigQuery
@>  (in struct)
~   (regex match)

// SQL Server
+   (string concatenation)
@   (variable prefix)
```

### 6. Special Syntax

Implement dialect-specific syntax:

```java
// SQLite
PRAGMA table_info(table_name)
ATTACH DATABASE 'file.db' AS alias
CREATE TABLE table AS SELECT ...

// PostgreSQL
CREATE TABLE IF NOT EXISTS
RETURNING clause
OVER clause for window functions

// BigQuery
EXCEPT(col1, col2) to exclude columns
REPLACE(col)
OFFSET n ROWS

// Snowflake
LATERAL FLATTEN
TABLESAMPLE(n ROWS)
```

## Best Practices

### 1. Follow Existing Patterns

Study existing dialect implementations (PostgreSQL, MySQL, Drill) to understand patterns.

### 2. Start Simple

Begin with minimal overrides. Add complexity only when needed for dialect-specific features.

**Good approach**:
```java
// Start with just identifier formatting
@Override
protected String formatIdentifier(String name) {
    return "`" + name.replace("`", "``") + "`";
}
```

**Avoid over-engineering**:
```java
// Don't override everything at once
// Only override what's needed for your dialect
```

### 3. Document Dialect Characteristics

Add clear documentation comments explaining:
- Identifier quoting style
- String literal style
- Key keywords and functions
- Special syntax features

### 4. Test Thoroughly

Create tests for:
- Basic SQL statements (SELECT, INSERT, UPDATE, DELETE)
- Dialect-specific keywords and functions
- Identifier quoting edge cases
- Error conditions

### 5. Use Standard Naming

Follow Java naming conventions:
- `XxxDialect` for dialect class
- `XxxParser` for parser class
- `XxxGenerator` for generator class
- `XxxTokenizer` for tokenizer class
- Package: `com.gtkcyber.sqlglot.dialects.xxx`

### 6. Handle Edge Cases

Test for edge cases:
- Quoted identifiers with special characters
- Escaped quotes in string literals
- Reserved words used as identifiers
- Case sensitivity variations
- Nested functions and expressions

### 7. Performance Considerations

For large SQL files:
- Tokenization is usually not the bottleneck
- Parser recursion depth matters (capped at 100)
- Generator string concatenation (consider StringBuilder)

## Troubleshooting

### Common Issues

**Problem**: Identifiers not quoted correctly
- **Solution**: Override `formatIdentifier()` with correct quoting style

**Problem**: Keyword not recognized
- **Solution**: Add to `buildKeywordTrie()` in tokenizer

**Problem**: Function syntax not parsed
- **Solution**: Override `parseFunction()` in parser

**Problem**: Generated SQL has wrong format
- **Solution**: Override `visitFunction()` or appropriate method in generator

**Problem**: Tests failing with ParseException
- **Solution**: Check keyword trie and parser overrides

## File Structure

After implementing a dialect, your directory structure should look like:

```
sqlglot-dialects/
├── src/main/java/com/gtkcyber/sqlglot/dialects/
│   └── yourdialect/
│       ├── YourDialect.java
│       ├── YourDialectParser.java
│       ├── YourDialectGenerator.java
│       └── YourDialectTokenizer.java
└── src/test/java/com/gtkcyber/sqlglot/dialects/
    └── YourDialectTest.java
```

## Resources

- Base Classes: `sqlglot-core/src/main/java/com/gtkcyber/sqlglot/`
- Example Dialects: `sqlglot-dialects/src/main/java/com/gtkcyber/sqlglot/dialects/`
- Test Examples: `sqlglot-dialects/src/test/java/com/gtkcyber/sqlglot/dialects/`

## Summary

To implement a new dialect in 15-20 minutes:

1. Run `DialectScaffold` to generate template files
2. Implement `SqliteTokenizer.getIdentifierQuotes()`
3. Implement `SqliteTokenizer.buildKeywordTrie()` with dialect keywords
4. Implement `SqliteGenerator.formatIdentifier()` for identifier quoting
5. Create basic test cases
6. Test parsing and generation of sample queries
7. Add dialect-specific features as needed

The templates provide clear instructions and examples for each step!
