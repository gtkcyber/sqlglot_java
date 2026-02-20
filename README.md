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
- **Optimization** - Apply query optimization rules (Phase 5A Complete)

### Supported SQL Features (Phase 1-2)

**Phase 1 (Complete):**
- SELECT statements with basic clauses
- FROM, WHERE, GROUP BY, HAVING, ORDER BY, LIMIT, OFFSET
- All operators (arithmetic, comparison, logical)
- CAST expressions
- Basic functions
- ANSI SQL compliance

**Phase 2-4B (Complete) - 128 Passing Tests:**
- CTEs (Common Table Expressions / WITH clause)
- Set operations (UNION, INTERSECT, EXCEPT)
- Aggregate functions (COUNT, SUM, AVG, MIN, MAX, with DISTINCT)
- String functions (UPPER, LOWER, LENGTH, SUBSTR, TRIM, CONCAT)
- Numeric functions (ABS, ROUND, CEIL, FLOOR, POWER, SQRT)
- Function calls with multiple arguments
- HAVING with aggregate conditions
- DML (INSERT, DELETE)
- DDL (CREATE, DROP, ALTER TABLE)
- DISTINCT keyword
- Iterative expression parsing (no recursion overflow)
- Window functions (ROW_NUMBER, RANK, DENSE_RANK, etc.)
- Column and table aliases (explicit and implicit)
- Subqueries in all contexts (IN clause, scalar subqueries, derived tables)
- Complex JOINs with ON conditions
- NOT IN operator and set operations

### Supported Dialects

Currently Implemented (6):
- **ANSI** - Standard SQL
- **DRILL** - Apache Drill (Primary focus)
- **PostgreSQL** - Full support
- **MySQL** - Full support
- **BigQuery** - Full support
- **Snowflake** - Full support

The Python sqlglot library supports 31 dialects total. An additional 25 dialects are planned for future implementation.

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

### Phase 2 - Advanced SQL Examples

```java
import io.sqlglot.*;

// Aggregate functions
String sql = "SELECT department, COUNT(*), SUM(salary), AVG(salary) " +
             "FROM employees GROUP BY department " +
             "HAVING COUNT(*) > 10";
Optional<Expression> expr = SqlGlot.parseOne(sql);
String generated = SqlGlot.generate(expr.get());

// String functions
String sql = "SELECT UPPER(name), LENGTH(email), SUBSTR(phone, 1, 3) FROM users";
Optional<Expression> expr = SqlGlot.parseOne(sql);

// Numeric functions
String sql = "SELECT ABS(amount), ROUND(price, 2), POWER(base, exponent) FROM data";
Optional<Expression> expr = SqlGlot.parseOne(sql);

// Set operations
String sql = "SELECT id FROM table1 " +
             "UNION " +
             "SELECT id FROM table2 " +
             "EXCEPT " +
             "SELECT id FROM table3";
Optional<Expression> expr = SqlGlot.parseOne(sql);

// CTEs (Common Table Expressions)
String sql = "WITH cte AS (SELECT a, b FROM t WHERE x > 10) " +
             "SELECT * FROM cte WHERE a > 5";
Optional<Expression> expr = SqlGlot.parseOne(sql);

// Complex queries with DISTINCT, LIMIT, OFFSET
String sql = "SELECT DISTINCT category, COUNT(*) cnt FROM products " +
             "WHERE price > 100 GROUP BY category ORDER BY cnt DESC " +
             "LIMIT 10 OFFSET 5";
Optional<Expression> expr = SqlGlot.parseOne(sql);
```

## Query Optimization (Phase 5A)

### Overview

SQLGlot Java includes a comprehensive query optimization framework with multiple optimization rules applied sequentially to transform SQL queries into more efficient forms.

### Usage Examples

```java
import io.sqlglot.*;
import io.sqlglot.optimizer.OptimizerConfig;

// Basic optimization with default configuration (Phase 5A rules)
Optional<Expression> expr = SqlGlot.parseOne("SELECT * FROM t WHERE TRUE AND x = 5");
Expression optimized = SqlGlot.optimize(expr.get());
String result = SqlGlot.generate(optimized);
// Result: "SELECT * FROM t WHERE x = 5"

// Specify dialect and configuration
Expression optimized = SqlGlot.optimize(expr.get(), "ANSI", OptimizerConfig.PHASE_5A);

// Use preset configurations
Expression minimalOpt = SqlGlot.optimize(expr.get(), "ANSI", OptimizerConfig.MINIMAL);
Expression aggressiveOpt = SqlGlot.optimize(expr.get(), "ANSI", OptimizerConfig.AGGRESSIVE);

// Custom configuration - enable specific rules only
OptimizerConfig custom = new OptimizerConfig(
    true,   // simplify
    true,   // canonicalize
    false,  // quoteIdentifiers
    false,  // eliminateCtes
    true,   // normalizePredicates
    false,  // pushdownPredicates
    false,  // mergeSubqueries
    false,  // joinReordering
    false,  // projectionPushdown
    false,  // annotateTypes
    false   // qualifyColumns
);
Expression optimized = SqlGlot.optimize(expr.get(), "ANSI", custom);
```

### Optimization Rules

**Phase 5A (Core Rules):**
- **SimplifyRule** - Removes redundant conditions (`TRUE AND x` → `x`), folds constants (`1 + 2` → `3`)
- **CanonicalizeRule** - Normalizes expressions (`5 < x` → `x > 5`, `NOT(x = 5)` → `x <> 5`)
- **QuoteIdentifiersRule** - Applies dialect-specific identifier quoting
- **EliminateCTEsRule** - Removes unused Common Table Expressions

**Phase 5B (Advanced Rules - Planned):**
- PushdownPredicatesRule, NormalizePredicatesRule, and more

### Configuration Presets

```java
OptimizerConfig.DEFAULT        // Phase 5A rules (default)
OptimizerConfig.MINIMAL        // Simplify only
OptimizerConfig.PHASE_5A       // All Phase 5A rules
OptimizerConfig.PHASE_5B       // Phase 5A + Phase 5B rules (future)
OptimizerConfig.AGGRESSIVE     // All available rules
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
    │   ├── drill/            - Apache Drill dialect
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

**222+ Passing Tests** across 30+ test classes (Phase 2-5A Complete):

### Core Tests
- **TokenizerTest** (5 tests) - Token recognition and SQL tokenization
- **GeneratorTest** (12 tests) - SQL code generation from AST

### Operators & Expressions
- **ArithmeticComparisonTest** (15 tests) - All arithmetic and comparison operators
- **BasicOperatorsTest** (6 tests) - IN, LIKE, IS NULL, BETWEEN, CAST operators
- **ComplexExpressionTest** (1 test) - Complex CASE expressions

### Functions
- **FunctionCallTest** (9 tests) - Function parsing and generation
- **AggregateFunctionsTest** (4 tests) - COUNT, SUM, AVG aggregate functions
- **StringFunctionsTest** (7 tests) - UPPER, LOWER, LENGTH, SUBSTR, TRIM, CONCAT
- **NumericFunctionsTest** (7 tests) - ABS, ROUND, CEIL, FLOOR, POWER, SQRT

### SQL Statements
- **DMLTest** (5 tests) - INSERT, DELETE statements
- **DDLTest** (6 tests) - CREATE, DROP, ALTER TABLE
- **GroupByTest** (2 tests) - GROUP BY with proper clause termination
- **HavingClauseTest** (4 tests) - HAVING with aggregate functions
- **DistinctTest** (2 tests) - DISTINCT keyword
- **LimitOffsetTest** (4 tests) - LIMIT and OFFSET clauses

### Advanced Features (Phase 3-4B)
- **FeatureDebugTest** (5 tests) - UNION, INTERSECT, EXCEPT, CTEs
- **WindowFunctionTest** - Window functions (ROW_NUMBER, RANK, etc.)
- **AliasTest** - Column and table aliases (explicit/implicit)
- **SubqueryTest** - Subqueries in all contexts
- **JoinTest** - Complex JOINs with ON conditions
- **RoundTripTest** (4 tests, skipped) - Parse → Generate → Parse verification
- **Phase2Test** (17 tests, skipped) - Additional advanced features
- **DrillDialectTest** (18 tests) - Drill-specific functionality

### Query Optimization (Phase 5A)
- **OptimizerTest** (10 tests) - Optimizer framework
- **ScopeTest** (12 tests) - Scope and context analysis
- **SimplifyRuleTest** (23 tests) - Boolean/arithmetic simplification
- **CanonicalizeRuleTest** (12 tests) - Expression normalization
- **QuoteIdentifiersRuleTest** (8 tests) - Identifier quoting
- **PushdownPredicatesRuleTest** (16 tests) - WHERE clause pushdown
- **NormalizePredicatesRuleTest** (13 tests) - CNF normalization
- Total Phase 5A: 94 Tests

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

### Phase 1 (Complete)
- Core tokenizer, parser, generator with iterative expression parsing
- ANSI SQL SELECT statements
- All operators (arithmetic, comparison, logical)
- CAST expressions
- Basic functions

### Phase 2 (Complete) - 98 Passing Tests
Key Achievement: Iterative expression parsing that prevents stack overflow
- Aggregate functions (COUNT, SUM, AVG, MIN, MAX)
- String functions (6+ functions)
- Numeric functions (6+ functions)
- Function calls with multiple arguments
- CTEs (WITH clauses)
- Set operations (UNION, INTERSECT, EXCEPT)
- DML statements (INSERT, DELETE)
- DDL statements (CREATE, DROP, ALTER TABLE)
- HAVING with aggregate conditions
- DISTINCT, LIMIT, OFFSET

### Phase 3 (Complete)
- Resolved heap overflow issues for aliases and subqueries
- Window functions (ROW_NUMBER, RANK, DENSE_RANK, etc.)
- Apache Drill dialect enhancements
- Support for implicit and explicit table aliases
- Subqueries in all contexts (IN, scalar, derived tables)

### Phase 4A (Complete) - Query Parsing Fixes
- COUNT(DISTINCT col) parsing
- Scalar subqueries and IN(SELECT) parsing
- Implicit alias parsing without AS keyword

### Phase 4B (Complete) - Dialect Implementation
- PostgreSQL dialect
- MySQL dialect
- BigQuery dialect
- Snowflake dialect
- ServiceLoader-based dialect registry
- Total: 128 Passing Tests

### Phase 5A (Complete) - Query Optimization
- Optimizer framework and rule system
- SimplifyRule (boolean/arithmetic simplification)
- CanonicalizeRule (expression normalization including NOT handling)
- QuoteIdentifiersRule (dialect-specific identifier quoting)
- EliminateCTEsRule (unused CTE elimination)
- PushdownPredicatesRule (WHERE clause pushdown through subqueries)
- NormalizePredicatesRule (CNF normalization with De Morgan's laws)
- Scope system (query context and name resolution foundation)
- Total: 94 Passing Optimizer Tests (Phase 5A)
- Combined: 222+ Passing Tests

### Phase 5B (Planned)
- Advanced optimizer rules (join reordering, subquery merging, projection pushdown)
- Type inference and annotation
- Column qualification and full name resolution
- Remaining 25 SQL dialects (to reach 31 total parity with Python sqlglot)

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

## Known Limitations (Post Phase 5A)

### Completed in Phase 5A
- Query optimization framework (optimizer pass system)
- Scope analysis and basic name resolution
- 7 core optimization rules (SimplifyRule, CanonicalizeRule, QuoteIdentifiersRule, EliminateCTEsRule, PushdownPredicatesRule, NormalizePredicatesRule, and ScopeBuilder)

### Still Planned for Future Phases
- Advanced optimizer rules (Phase 5B: join reordering, subquery merging, projection pushdown, type inference, column qualification)
- Some dialect-specific features not yet fully implemented
- No concurrent parsing/generation (sequential only)
- 25 remaining SQL dialects not yet implemented (to reach parity with Python sqlglot's 31 dialects)

### Resolved Issues (Phase 3-5A)
- Aliases (column and table names)
- Subqueries in all contexts
- Window functions
- Dialect support (5 dialects implemented and registered)
- Query optimization framework
- Expression comparison for simplification
- Parenthesized expression handling in canonicalization

### Phase 5B Goals (Next)
- Implement advanced optimizer rules (join reordering, subquery merging, projection pushdown)
- Add type inference and annotation
- Implement full column qualification
- Implement remaining 27+ SQL dialects
- Performance optimization and benchmarking

## Contributing

Contributions welcome! Focus areas:

1. **Drill Features** - Expand Drill-specific functionality
2. **Dialects** - Implement remaining 25 dialects (current: 6 of 31)
3. **Optimizer** - Advanced Phase 5B optimizer rules
4. **Tests** - Increase test coverage and real-world SQL examples
5. **Performance** - Optimize tokenizer and parser
6. **Documentation** - Improve Javadoc and examples

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
**Current Phase**: 5A Complete (222+ tests passing)
**Next Phase**: 5B (Advanced Optimizer Rules + Remaining Dialects)

### Build Status
- All 222+ tests passing (Phase 2-5A complete)
- Phase 5A optimizer with 7 optimization rules fully implemented
- 6 SQL dialects fully implemented (ANSI, Drill, PostgreSQL, MySQL, BigQuery, Snowflake)
- Zero failures or errors
- Clean compilation
- ServiceLoader-based dialect registry operational
- Query optimization framework operational (94 optimizer tests passing)
- Ready for Phase 5B work (Advanced Optimizer Rules + Remaining Dialects)
