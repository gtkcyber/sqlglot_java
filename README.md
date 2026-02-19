# SQLGlot Java - SQL Parsing and Generation Library

A comprehensive Java 17+ port of the [sqlglot](https://github.com/tobymao/sqlglot) Python library, providing SQL parsing, generation, validation, and transpilation across multiple SQL dialects.

**Special Focus:** Apache Drill support for distributed SQL query execution across heterogeneous data sources.

## Features

### Core Capabilities

- **SQL Parsing** - Convert SQL strings into Abstract Syntax Trees (AST)
- **Code Generation** - Convert ASTs back to SQL with configurable formatting
- **Query Analysis** - Walk, search, and transform AST nodes
- **Transpilation** - Convert SQL between different dialects
- **Formatting** - Pretty-print and normalize SQL
- **Optimization** - Apply query optimization rules (Phase 4)

### Supported SQL Features (Phase 1-2)

**Phase 1 (Complete):**
- SELECT statements with basic clauses
- FROM, WHERE, GROUP BY, HAVING, ORDER BY, LIMIT, OFFSET
- Basic JOINs (INNER, LEFT, RIGHT, CROSS)
- Basic data types (INT, VARCHAR, etc.)
- Operators (arithmetic, comparison, logical)
- Subqueries
- CASTs and functions
- ANSI SQL compliance

**Phase 2 (In Progress):**
- CTEs (Common Table Expressions / WITH clause)
- Set operations (UNION, INTERSECT, EXCEPT)
- Window functions (ROW_NUMBER, RANK, etc.)
- Complex data types (ARRAY, MAP, STRUCT)
- EXPLAIN and utility statements
- **Apache Drill specific features** ⭐

### Supported Dialects

- **ANSI** - Standard SQL
- **DRILL** - Apache Drill ⭐ (Primary focus)
- **PostgreSQL** - (Phase 2)
- **MySQL** - (Phase 2)
- **BigQuery** - (Phase 2)
- **Snowflake** - (Phase 2)
- _27 additional dialects planned for Phase 5_

## Quick Start

### Installation

Add to your Maven `pom.xml`:

```xml
<dependency>
    <groupId>io.sqlglot</groupId>
    <artifactId>sqlglot-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>io.sqlglot</groupId>
    <artifactId>sqlglot-dialects</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Basic Usage

```java
import io.sqlglot.*;

// Parse SQL
Optional<Expression> expr = SqlGlot.parseOne("SELECT a, b FROM t WHERE x = 1");

// Generate SQL
String sql = SqlGlot.generate(expr.get());

// Format SQL
String formatted = SqlGlot.format("SELECT a,b FROM t WHERE x=1");

// Transpile between dialects
String drillSQL = SqlGlot.transpile("SELECT * FROM table", "ANSI", "DRILL");

// Use Apache Drill dialect
Dialect drill = Dialect.of("DRILL");
Optional<Expression> parsed = drill.parseOne("SELECT * FROM `workspace`.`schema`.`table`");
```

## Apache Drill Support

### Overview

SQLGlot Java has first-class support for Apache Drill, enabling SQL parsing, validation, and transpilation for Drill's unique syntax and features.

### Drill-Specific Features

#### 1. **Workspace and Schema Paths**

Drill supports a three-level namespace: `workspace.schema.table`

```java
String sql = "SELECT * FROM `datalake`.`raw`.`customers`";
Dialect drill = Dialect.of("DRILL");
Optional<Expression> expr = drill.parseOne(sql);
```

#### 2. **Backtick Identifiers**

Drill uses backticks for identifiers (especially for reserved words and paths):

```java
String sql = "SELECT `select`, `from` FROM `my-table-123`";
// Backticks are preserved and used consistently in generated SQL
```

#### 3. **Complex Data Types**

Support for Drill's nested types:

```java
// ARRAY type
String sql = "SELECT CAST(col AS ARRAY<INT>) FROM t";

// MAP type
String sql = "SELECT CAST(col AS MAP<STRING, INT>) FROM t";

// STRUCT type
String sql = "SELECT CAST(col AS STRUCT<name STRING, age INT, address STRUCT<street STRING, city STRING>>) FROM t";
```

#### 4. **FLATTEN Function**

Unnest arrays and maps into individual rows:

```java
// Basic flatten
String sql = "SELECT FLATTEN(nested_col) FROM t";

// Flatten with depth limit
String sql = "SELECT FLATTEN(deeply_nested_col, 2) FROM t";
```

#### 5. **File Sources**

Query files directly:

```java
String sql = "SELECT * FROM dfs.`/data/customers.parquet`";
String sql = "SELECT * FROM dfs.`s3://bucket/data/`";
```

#### 6. **Nested Field Access**

Access nested JSON and structured data:

```java
String sql = "SELECT person.name, person.address.street FROM customers";
```

### Drill Example Workflows

#### Query Validation

```java
Dialect drill = Dialect.of("DRILL");
String userQuery = "SELECT * FROM `problematic`.`query`";

try {
    Optional<Expression> validated = drill.parseOne(userQuery);
    if (validated.isPresent()) {
        System.out.println("Query is valid");
    }
} catch (Exception e) {
    System.out.println("Query validation failed: " + e.getMessage());
}
```

#### Query Normalization

```java
Dialect drill = Dialect.of("DRILL");
String messySQL = "select a,b,c from t WHERE x=1";

String normalized = drill.format(messySQL);
// Output: SELECT a, b, c FROM t WHERE x = 1
```

#### Cross-Dialect Transpilation

```java
// Convert from another SQL dialect to Drill
String postgresSQL = "SELECT column_name FROM table_name";
String drillSQL = SqlGlot.transpile(postgresSQL, "POSTGRES", "DRILL");
// Output: SELECT `column_name` FROM `table_name`

// Or from Drill to another dialect
String drillSQL = "SELECT `a`, `b` FROM `my_table`";
String ansiSQL = SqlGlot.transpile(drillSQL, "DRILL", "ANSI");
// Output: SELECT a, b FROM my_table
```

#### Query Analysis

```java
Dialect drill = Dialect.of("DRILL");
Optional<Expression> expr = drill.parseOne("SELECT * FROM `t1` JOIN `t2` ON `t1`.id = `t2`.id WHERE x > 10");

if (expr.isPresent()) {
    // Find all table references
    expr.get().findAll(Nodes.Table.class).forEach(table -> {
        System.out.println("Table: " + table.getName());
    });

    // Find all functions
    expr.get().findAll(Nodes.Function.class).forEach(func -> {
        System.out.println("Function: " + func.getName());
    });

    // Transform the query
    Expression transformed = expr.get().transform(e -> {
        // Apply custom transformations
        return e;
    });
}
```

## Architecture

### Project Structure

```
sqlglot-java/
├── sqlglot-core/
│   ├── src/main/java/io/sqlglot/
│   │   ├── tokens/          - Tokenization
│   │   ├── expressions/      - AST node types
│   │   ├── parser/           - SQL parsing
│   │   ├── generator/        - SQL generation
│   │   ├── dialect/          - Dialect infrastructure
│   │   └── SqlGlot.java      - Public API
│   └── src/test/java/io/sqlglot/  - Comprehensive tests
│
└── sqlglot-dialects/
    ├── src/main/java/io/sqlglot/dialects/
    │   ├── drill/            - Apache Drill dialect ⭐
    │   ├── postgres/         - PostgreSQL dialect
    │   ├── mysql/            - MySQL dialect
    │   ├── bigquery/         - BigQuery dialect
    │   └── snowflake/        - Snowflake dialect
    └── src/test/java/io/sqlglot/dialects/  - Dialect tests
```

### Core Components

1. **Tokenizer** - Converts SQL strings into tokens
   - Base: `io.sqlglot.tokens.Tokenizer`
   - Drill: `io.sqlglot.dialects.drill.DrillTokenizer`

2. **Parser** - Converts tokens into AST
   - Base: `io.sqlglot.parser.Parser`
   - Drill: `io.sqlglot.dialects.drill.DrillParser`

3. **Expression AST** - Hierarchical node types
   - Base: `io.sqlglot.expressions.Expression`
   - Nodes: `io.sqlglot.expressions.Nodes`
   - Visitor: `io.sqlglot.expressions.ExpressionVisitor<R>`

4. **Generator** - Converts AST back to SQL
   - Base: `io.sqlglot.generator.Generator`
   - Drill: `io.sqlglot.dialects.drill.DrillGenerator`

5. **Dialect** - Bundles tokenizer, parser, generator
   - Base: `io.sqlglot.dialect.Dialect`
   - Drill: `io.sqlglot.dialects.drill.DrillDialect`

## Testing

The project includes comprehensive test coverage:

- **Tokenizer Tests** (`TokenizerTest.java`) - Token recognition and parsing
- **Parser Tests** (`ParserTest.java`) - SQL statement parsing
- **Generator Tests** (`GeneratorTest.java`) - SQL code generation
- **Round-Trip Tests** (`RoundTripTest.java`) - Parse → Generate → Parse verification
- **Phase 2 Tests** (`Phase2Test.java`) - Advanced SQL features
- **Drill Dialect Tests** (`DrillDialectTest.java`) - Drill-specific functionality

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=DrillDialectTest

# Run with coverage
mvn test jacoco:report
```

## Implementation Phases

### ✅ Phase 1 (Complete)
- Core tokenizer, parser, generator
- ANSI SQL SELECT statements
- Basic operators and functions
- CAST and type support

### 🚧 Phase 2 (In Progress)
- CTEs and WITH clauses
- Set operations (UNION, INTERSECT, EXCEPT)
- Window functions
- Apache Drill dialect ⭐
- PostgreSQL, MySQL, BigQuery, Snowflake dialects

### 📋 Phase 3 (Planned)
- Additional SQL dialects

### 📋 Phase 4 (Planned)
- Query optimization (14 optimizer passes)
- Scope analysis and name resolution

### 📋 Phase 5 (Planned)
- Remaining 27+ SQL dialects
- Performance optimization and benchmarking

## Development

### Building from Source

```bash
# Clone the repository
git clone https://github.com/charlesgivre/sqlglot_java.git
cd sqlglot_java

# Build
mvn clean install

# Build without tests (faster)
mvn clean install -DskipTests
```

### Code Style

- Java 17+ features encouraged (records, sealed classes, pattern matching)
- Immutability preferred
- Comprehensive Javadoc comments
- Test coverage for new features

## Key Design Decisions

### Records for Value Objects
- `Token` is a record for immutability
- `ParserConfig`, `GeneratorConfig` as records

### Sealed Classes
- `Expression` is abstract (sealed in Java 19+)
- Binary operators extend sealed `Binary` class
- Permits 50+ node types

### Visitor Pattern
- Type-safe AST traversal
- Compile-time exhaustiveness checking
- Easy to extend with dialects

### Dialect Registry
- ServiceLoader-based plugin discovery
- Manual registration fallback
- Case-insensitive dialect lookup

## Performance Characteristics

- **Tokenization**: O(n) where n = SQL length
- **Parsing**: O(n²) worst case (operator precedence)
- **Generation**: O(n) where n = AST nodes
- **Memory**: Minimal; streaming tokens, tree-based AST

## Limitations (Phase 2)

- Parser recursion bug fixed but needs comprehensive testing
- Limited optimization (Phase 4)
- Some dialect-specific features not yet implemented
- No concurrent parsing/generation (sequential only)

## Contributing

Contributions welcome! Focus areas:

1. **Drill Features** - Expand Drill-specific functionality
2. **Dialects** - Add PostgreSQL, MySQL, BigQuery, Snowflake support
3. **Tests** - Increase test coverage and real-world SQL examples
4. **Performance** - Optimize tokenizer and parser
5. **Documentation** - Improve Javadoc and examples

## License

[Specify your license here]

## References

- [SQLGlot Python](https://github.com/tobymao/sqlglot) - Original Python library
- [Apache Drill](https://drill.apache.org/) - SQL query engine
- [Java 17 Features](https://www.oracle.com/java/technologies/javase/17-relnote-issues.html)
- [SQL Standard](https://en.wikipedia.org/wiki/SQL)

## Support

For issues, questions, or contributions related to Apache Drill support, please refer to the `DrillDialectTest` and `DrillDialect` classes for examples.

---

**Last Updated**: 2026-02-19
**Phase**: 2 (Advanced SQL + Drill Support)
