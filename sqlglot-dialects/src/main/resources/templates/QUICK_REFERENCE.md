# Dialect Implementation Quick Reference

Fast lookup guide for implementing SQL dialects.

## Placeholder Quick Reference

| Placeholder | Example Value | Usage |
|------------|---------------|-------|
| `{DIALECT_NAME}` | `SQLite` | Display name in documentation |
| `{DIALECT_NAME_UPPER}` | `SQLITE` | Constant names, registration |
| `{DIALECT_NAME_LOWER}` | `sqlite` | Package paths, file names |
| `{PACKAGE_PATH}` | `sqlite` | Java package component |
| `{DIALECT_CLASS_NAME}` | `SqliteDialect` | Main dialect class |
| `{PARSER_CLASS_NAME}` | `SqliteParser` | Parser class |
| `{GENERATOR_CLASS_NAME}` | `SqliteGenerator` | Generator class |
| `{TOKENIZER_CLASS_NAME}` | `SqliteTokenizer` | Tokenizer class |

## Common Identifier Quoting Styles

### Double Quotes (SQL Standard)
```java
@Override
protected Map<String, Character> getIdentifierQuotes() {
    Map<String, Character> quotes = new HashMap<>();
    quotes.put("\"", '"');  // "column_name" with "" for escaping
    return quotes;
}
```

### Backticks (MySQL, Drill)
```java
@Override
protected Map<String, Character> getIdentifierQuotes() {
    Map<String, Character> quotes = new HashMap<>();
    quotes.put("`", '`');  // `column_name` with `` for escaping
    return quotes;
}
```

### Square Brackets (SQL Server)
```java
@Override
protected Map<String, Character> getIdentifierQuotes() {
    Map<String, Character> quotes = new HashMap<>();
    quotes.put("[", ']');  // [column_name] with ]] for escaping
    return quotes;
}
```

### Multiple Styles (SQLite)
```java
@Override
protected Map<String, Character> getIdentifierQuotes() {
    Map<String, Character> quotes = new HashMap<>();
    quotes.put("\"", '"');   // Double quotes
    quotes.put("`", '`');    // Backticks
    quotes.put("[", ']');    // Square brackets
    return quotes;
}
```

## Common String Quoting Styles

### Single Quotes with Doubled Quote Escaping (Most Common)
```java
@Override
protected Map<String, Character> getStringQuotes() {
    Map<String, Character> quotes = new HashMap<>();
    quotes.put("'", '\'');  // 'string' with '' for escaping
    return quotes;
}
```

### Backslash Escaping
```java
@Override
protected Map<String, Character> getStringQuotes() {
    Map<String, Character> quotes = new HashMap<>();
    quotes.put("'", '\\');  // 'string' with \' for escaping
    return quotes;
}
```

### Multiple Styles
```java
@Override
protected Map<String, Character> getStringQuotes() {
    Map<String, Character> quotes = new HashMap<>();
    quotes.put("'", '\'');   // Single quotes
    quotes.put("\"", '\\');  // Double quotes with backslash
    return quotes;
}
```

## Identifier Formatting in Generator

### Double Quotes
```java
@Override
protected String formatIdentifier(String name) {
    return "\"" + name.replace("\"", "\"\"") + "\"";
}
```

### Backticks
```java
@Override
protected String formatIdentifier(String name) {
    return "`" + name.replace("`", "``") + "`";
}
```

### Square Brackets
```java
@Override
protected String formatIdentifier(String name) {
    return "[" + name.replace("]", "]]") + "]";
}
```

### No Quoting
```java
@Override
protected String formatIdentifier(String name) {
    return name;
}
```

## Normalization Strategies

### UPPERCASE (Default for most dialects)
```java
public class SqliteDialect extends Dialect {
    public SqliteDialect() {
        super("SQLITE", NormalizationStrategy.UPPERCASE);
    }
}
```

### LOWERCASE
```java
public class PostgresqlDialect extends Dialect {
    public PostgresqlDialect() {
        super("POSTGRESQL", NormalizationStrategy.LOWERCASE);
    }
}
```

### PRESERVE (Keep original case)
```java
public class CustomDialect extends Dialect {
    public CustomDialect() {
        super("CUSTOM", NormalizationStrategy.PRESERVE);
    }
}
```

## Adding Keywords to Tokenizer

### Add Single Keyword
```java
keywords.put("PRAGMA", TokenType.IDENTIFIER);
```

### Add Multiple Keywords
```java
String[] sqliteKeywords = {"PRAGMA", "ATTACH", "AUTOINCREMENT", "COLLATE", "GLOB"};
for (String keyword : sqliteKeywords) {
    keywords.put(keyword, TokenType.IDENTIFIER);
}
```

### Add All Standard Keywords
```java
for (TokenType type : TokenType.values()) {
    if (type.isKeyword() && type.getText() != null) {
        keywords.put(type.getText(), type);
    }
}
```

## Generator Transforms

### Basic Transform
```java
@Override
protected Map<Class<? extends Expression>, Function<Expression, String>> transforms() {
    Map<Class<? extends Expression>, Function<Expression, String>> map = new HashMap<>();

    map.put(Nodes.Function.class, expr -> {
        Nodes.Function func = (Nodes.Function) expr;
        if ("CUSTOM_FUNC".equalsIgnoreCase(func.getName())) {
            return "CUSTOM(" + sql(func.getArgs().get(0)) + ")";
        }
        return null;  // Fall back to default
    });

    return map;
}
```

### Multiple Transforms
```java
@Override
protected Map<Class<? extends Expression>, Function<Expression, String>> transforms() {
    Map<Class<? extends Expression>, Function<Expression, String>> map = new HashMap<>();

    // Transform 1: Function
    map.put(Nodes.Function.class, expr -> {
        Nodes.Function func = (Nodes.Function) expr;
        if ("FUNC1".equalsIgnoreCase(func.getName())) {
            return "CUSTOM_FUNC1(...)";
        }
        return null;
    });

    // Transform 2: Cast
    map.put(Nodes.Cast.class, expr -> {
        Nodes.Cast cast = (Nodes.Cast) expr;
        return "CAST(" + sql(cast.getExpression()) + " AS " +
               sql(cast.getDataType()) + ")";
    });

    return map;
}
```

## Parser Override Patterns

### Override parseStatement()
```java
@Override
public Optional<Expression> parseStatement() {
    if (match("CUSTOM_KEYWORD")) {
        return Optional.of(parseCustomStatement());
    }
    return super.parseStatement();
}
```

### Override parseExpression()
```java
@Override
public Expression parseExpression() {
    if (peek("SPECIAL_KEYWORD")) {
        return parseSpecialExpression();
    }
    return super.parseExpression();
}
```

### Override parseFunction()
```java
@Override
public Expression parseFunction() {
    if (peek("CUSTOM_FUNC")) {
        advance();
        Expression arg = parseExpression();
        expect(")");
        return new Nodes.Function("CUSTOM_FUNC", Collections.singletonList(arg));
    }
    return super.parseFunction();
}
```

## Generator Override Patterns

### Override visitFunction()
```java
@Override
public String visitFunction(Nodes.Function node) {
    if ("CUSTOM_FUNC".equalsIgnoreCase(node.getName())) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatKeyword("CUSTOM_FUNC")).append("(");
        sb.append(sql(node.getArgs().get(0)));
        sb.append(")");
        return sb.toString();
    }
    return super.visitFunction(node);
}
```

### Override visitCast()
```java
@Override
public String visitCast(Nodes.Cast node) {
    String expr = sql(node.getExpression());
    String type = sql(node.getDataType());
    return formatKeyword("CAST") + "(" + expr + " " +
           formatKeyword("AS") + " " + type + ")";
}
```

## Testing Patterns

### Basic Test
```java
@Test
public void testBasicSelect() {
    String sql = "SELECT * FROM table";
    List<Expression> statements = dialect.parse(sql);
    assertEquals(1, statements.size());
    assertNotNull(statements.get(0));
}
```

### Round Trip Test
```java
@Test
public void testRoundTrip() {
    String original = "SELECT col1, col2 FROM table";
    List<Expression> statements = dialect.parse(original);
    String generated = dialect.generate(statements.get(0));
    List<Expression> reparsed = dialect.parse(generated);
    assertNotNull(reparsed.get(0));
}
```

### Feature Test
```java
@Test
public void testDialectFeature() {
    String sql = "SELECT DIALECT_SPECIFIC_FUNC(col) FROM table";
    List<Expression> statements = dialect.parse(sql);
    assertEquals(1, statements.size());
}
```

## File Structure

```
sqlglot-dialects/src/main/java/com/gtkcyber/sqlglot/dialects/
└── {PACKAGE_PATH}/
    ├── {DIALECT_CLASS_NAME}.java
    ├── {PARSER_CLASS_NAME}.java
    ├── {GENERATOR_CLASS_NAME}.java
    └── {TOKENIZER_CLASS_NAME}.java

sqlglot-dialects/src/test/java/com/gtkcyber/sqlglot/dialects/
└── {DIALECT_CLASS_NAME}Test.java
```

## Keyword Categories

### DDL (Data Definition Language)
```java
keywords.put("CREATE", TokenType.CREATE);
keywords.put("DROP", TokenType.DROP);
keywords.put("ALTER", TokenType.ALTER);
keywords.put("TABLE", TokenType.TABLE);
keywords.put("VIEW", TokenType.VIEW);
keywords.put("INDEX", TokenType.INDEX);
keywords.put("DATABASE", TokenType.DATABASE);
keywords.put("SCHEMA", TokenType.SCHEMA);
```

### DML (Data Manipulation Language)
```java
keywords.put("SELECT", TokenType.SELECT);
keywords.put("INSERT", TokenType.INSERT);
keywords.put("UPDATE", TokenType.UPDATE);
keywords.put("DELETE", TokenType.DELETE);
keywords.put("FROM", TokenType.FROM);
keywords.put("WHERE", TokenType.WHERE);
keywords.put("VALUES", TokenType.VALUES);
keywords.put("SET", TokenType.SET);
```

### Common Keywords
```java
keywords.put("AND", TokenType.AND);
keywords.put("OR", TokenType.OR);
keywords.put("NOT", TokenType.NOT);
keywords.put("IN", TokenType.IN);
keywords.put("IS", TokenType.IS);
keywords.put("NULL", TokenType.NULL);
keywords.put("TRUE", TokenType.TRUE);
keywords.put("FALSE", TokenType.FALSE);
keywords.put("LIKE", TokenType.LIKE);
keywords.put("BETWEEN", TokenType.BETWEEN);
keywords.put("CASE", TokenType.CASE);
keywords.put("WHEN", TokenType.WHEN);
keywords.put("THEN", TokenType.THEN);
keywords.put("ELSE", TokenType.ELSE);
keywords.put("END", TokenType.END);
```

### Join Keywords
```java
keywords.put("JOIN", TokenType.JOIN);
keywords.put("LEFT", TokenType.LEFT);
keywords.put("RIGHT", TokenType.RIGHT);
keywords.put("INNER", TokenType.INNER);
keywords.put("OUTER", TokenType.OUTER);
keywords.put("FULL", TokenType.FULL);
keywords.put("CROSS", TokenType.CROSS);
keywords.put("ON", TokenType.ON);
```

### Group/Order Keywords
```java
keywords.put("GROUP", TokenType.GROUP);
keywords.put("BY", TokenType.BY);
keywords.put("HAVING", TokenType.HAVING);
keywords.put("ORDER", TokenType.ORDER);
keywords.put("ASC", TokenType.ASC);
keywords.put("DESC", TokenType.DESC);
keywords.put("LIMIT", TokenType.LIMIT);
keywords.put("OFFSET", TokenType.OFFSET);
```

## Debugging Tips

### Enable Debug Output
```java
// Add println statements in your parser/tokenizer
System.out.println("Parsing: " + currentToken());
System.out.println("Keyword trie contains: " + keywordName);
```

### Test Keywords
```java
SqliteTokenizer tokenizer = new SqliteTokenizer();
List<Token> tokens = tokenizer.tokenize("SELECT * FROM table");
for (Token token : tokens) {
    System.out.println(token.type() + ": " + token.text());
}
```

### Test Generated SQL
```java
String sql = "SELECT col FROM table";
List<Expression> statements = dialect.parse(sql);
String generated = dialect.generate(statements.get(0));
System.out.println("Generated: " + generated);
```

## Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| Identifiers not quoted | Override `formatIdentifier()` |
| Keywords not recognized | Add to `buildKeywordTrie()` |
| Function syntax wrong | Override `visitFunction()` |
| Type syntax wrong | Override `visitDataType()` |
| CAST syntax wrong | Override `visitCast()` |
| String escaping wrong | Check `getStringQuotes()` |
| Identifier escaping wrong | Check `getIdentifierQuotes()` |
| ParseException on valid SQL | Check keyword recognition |

## Performance Checklist

- [ ] Tokenizer uses efficient trie structure
- [ ] Keywords are precompiled (not dynamic)
- [ ] Parser depth limited to prevent stack overflow
- [ ] Generator uses StringBuilder (not string concatenation)
- [ ] No unnecessary object creation in hot paths
- [ ] Caching used for frequently accessed data

## Documentation Checklist

- [ ] Main dialect class has clear javadoc
- [ ] Tokenizer explains quoting styles
- [ ] Parser explains custom syntax
- [ ] Generator explains formatting rules
- [ ] Code comments explain dialect-specific logic
- [ ] Example SQL queries included
- [ ] Known limitations documented

## Testing Checklist

- [ ] Basic SELECT statements
- [ ] INSERT, UPDATE, DELETE statements
- [ ] JOINs and subqueries
- [ ] GROUP BY, ORDER BY, HAVING
- [ ] Dialect-specific keywords
- [ ] Dialect-specific functions
- [ ] Identifier quoting styles
- [ ] String literal escaping
- [ ] Data types
- [ ] CAST expressions
- [ ] Complex nested queries
- [ ] Round-trip parsing/generation
