# SQL Dialect Template System

This directory contains template files and tools for quickly generating new SQL dialect implementations in sqlglot-java.

## Overview

The template system allows you to implement a new SQL dialect in **15-20 minutes** by:
1. Using the `DialectScaffold` tool to generate template files
2. Replacing placeholder values with dialect-specific names
3. Implementing dialect-specific tokenization, parsing, and code generation
4. Adding test cases

## Files in This Directory

### Core Template Files

- **`BaseDialectTemplate.java`** - Template for the main Dialect class
  - Extends `Dialect` base class
  - Creates Tokenizer, Parser, and Generator instances
  - Specifies normalization strategy

- **`BaseTokenizerTemplate.java`** - Template for the Tokenizer class
  - Recognizes identifier and string quote characters
  - Builds keyword trie for fast keyword recognition
  - Handles dialect-specific tokens

- **`BaseParserTemplate.java`** - Template for the Parser class
  - Extends base Parser with dialect-specific syntax
  - Handles custom statement types and expressions
  - Overridable methods for parsing extensions

- **`BaseGeneratorTemplate.java`** - Template for the Generator class
  - Formats AST back to SQL text
  - Implements dialect-specific identifier quoting
  - Handles custom transformations for dialect features

### Scaffolding Tools

- **`DialectScaffold.java`** - Main utility for generating dialect implementations
  - Reads template files
  - Replaces placeholder variables
  - Creates new dialect files with proper structure
  - Can be run from command line

### Documentation

- **`DIALECT_IMPLEMENTATION_GUIDE.md`** (in parent directory) - Comprehensive guide
  - Quick start instructions
  - Detailed explanation of each component
  - Example: implementing SQLite dialect
  - Testing patterns and best practices
  - Troubleshooting guide

- **`TEMPLATE_USAGE_EXAMPLE.md`** - Step-by-step SQLite example
  - Real-world example of dialect implementation
  - Complete code examples for each component
  - Test cases with explanations
  - Time estimates for each step

- **`QUICK_REFERENCE.md`** - Fast lookup guide
  - Identifier quoting patterns
  - String quoting patterns
  - Generator transforms
  - Parser override patterns
  - Testing patterns
  - Common issues and solutions

## Quick Start

### Generate Template Files

```bash
cd /path/to/sqlglot_java

# Generate for SQLite dialect
java com.gtkcyber.sqlglot.dialects.template.DialectScaffold \
  SQLite sqlite \
  sqlglot-dialects/src/main/java/com/gtkcyber/sqlglot/dialects/
```

This creates four files:
```
sqlglot-dialects/src/main/java/com/gtkcyber/sqlglot/dialects/sqlite/
├── SqliteDialect.java
├── SqliteParser.java
├── SqliteGenerator.java
└── SqliteTokenizer.java
```

### Customize Generated Files

1. **SqliteTokenizer.java** - Define identifier/string quoting and keywords
2. **SqliteGenerator.java** - Implement identifier formatting and transforms
3. **SqliteParser.java** - Add dialect-specific parsing (optional for most dialects)
4. **SqliteDialect.java** - Usually requires no changes

### Create Tests

```
sqlglot-dialects/src/test/java/com/gtkcyber/sqlglot/dialects/
└── SqliteDialectTest.java
```

### Verify

```bash
mvn test -Dtest=SqliteDialectTest
```

## Placeholder Variables

When generating templates, these placeholders are replaced:

| Placeholder | Example | Used In |
|------------|---------|---------|
| `{DIALECT_NAME}` | `SQLite` | Comments, documentation |
| `{DIALECT_NAME_UPPER}` | `SQLITE` | Dialect registration |
| `{DIALECT_NAME_LOWER}` | `sqlite` | Package paths |
| `{PACKAGE_PATH}` | `sqlite` | Java package path |
| `{DIALECT_CLASS_NAME}` | `SqliteDialect` | Class names |
| `{PARSER_CLASS_NAME}` | `SqliteParser` | Class names |
| `{GENERATOR_CLASS_NAME}` | `SqliteGenerator` | Class names |
| `{TOKENIZER_CLASS_NAME}` | `SqliteTokenizer` | Class names |

## Using DialectScaffold Programmatically

```java
import com.gtkcyber.sqlglot.dialects.template.DialectScaffold;
import java.nio.file.Paths;

public class GenerateDialect {
    public static void main(String[] args) throws IOException {
        // Create configuration
        DialectScaffold.DialectConfig config =
            new DialectScaffold.DialectConfig("SQLite", "sqlite");

        // Generate files
        DialectScaffold scaffold = new DialectScaffold();
        scaffold.generateDialect(
            config,
            Paths.get("sqlglot-dialects/src/main/java/com/gtkcyber/sqlglot/dialects/sqlite")
        );

        System.out.println("Dialect implementation generated!");
    }
}
```

## Dialect Components Explained

### 1. Tokenizer

Breaks SQL text into tokens. Customize:
- **Identifier quoting**: How to quote table/column names
- **String quoting**: How to quote string literals
- **Keywords**: Dialect-specific keywords

### 2. Parser

Converts tokens to Abstract Syntax Tree (AST). Customize:
- **Custom statements**: New statement types
- **Custom expressions**: New expression syntax
- **Custom functions**: Special function syntax

### 3. Generator

Converts AST back to SQL text. Customize:
- **Identifier formatting**: How to quote identifiers
- **Transformations**: Custom node generation
- **Functions**: Special function syntax

### 4. Dialect

Main dialect class. Usually requires minimal customization:
- Set dialect name
- Set normalization strategy
- Create Tokenizer, Parser, Generator

## Implementation Time Estimate

| Step | Time |
|------|------|
| Generate templates | 1 min |
| Implement Tokenizer | 3-4 min |
| Implement Generator | 2-3 min |
| Implement Parser | 2-3 min |
| Create tests | 3-5 min |
| Testing & debugging | 3-5 min |
| **Total** | **14-21 min** |

## Example Dialects Implemented

Study these real implementations for reference:
- PostgreSQL: `/dialects/postgres/`
- MySQL: `/dialects/mysql/`
- BigQuery: `/dialects/bigquery/`
- Drill: `/dialects/drill/`
- Snowflake: `/dialects/snowflake/`

## Getting Help

### Documentation Files

1. **`DIALECT_IMPLEMENTATION_GUIDE.md`**
   - Most comprehensive resource
   - Complete walkthrough with examples
   - Best practices and patterns

2. **`TEMPLATE_USAGE_EXAMPLE.md`**
   - Real-world SQLite example
   - Copy-paste ready code
   - Detailed explanations

3. **`QUICK_REFERENCE.md`**
   - Quick lookup tables
   - Code snippets for common patterns
   - Troubleshooting guide

### Common Questions

**Q: Do I need to override the Parser class?**
A: Not always. Many dialects only need custom Tokenizer and Generator.

**Q: What's the difference between Tokenizer and Parser?**
A: Tokenizer converts text to tokens. Parser converts tokens to AST.

**Q: Where should I define dialect-specific keywords?**
A: In Tokenizer's `buildKeywordTrie()` method.

**Q: How do I handle special operators like `::`?**
A: Define them in the Tokenizer or Parser as needed.

**Q: Can I reuse code from other dialects?**
A: Yes! Study similar dialects and adapt their implementations.

## Best Practices

1. **Start Simple**: Use templates, add complexity only when needed
2. **Follow Patterns**: Study existing dialects for patterns
3. **Document Dialect**: Add clear comments explaining dialect features
4. **Test Thoroughly**: Create comprehensive test cases
5. **Use Standard Names**: Follow Java naming conventions
6. **Check Existing Code**: See how Drill/PostgreSQL/MySQL handle similar issues

## File Structure

After implementing a dialect:

```
sqlglot-dialects/
├── src/main/java/com/gtkcyber/sqlglot/dialects/
│   ├── template/           (this directory)
│   │   ├── BaseDialectTemplate.java
│   │   ├── BaseParserTemplate.java
│   │   ├── BaseGeneratorTemplate.java
│   │   ├── BaseTokenizerTemplate.java
│   │   ├── DialectScaffold.java
│   │   ├── README.md
│   │   ├── QUICK_REFERENCE.md
│   │   └── TEMPLATE_USAGE_EXAMPLE.md
│   ├── yourdialect/        (generated files)
│   │   ├── YourDialect.java
│   │   ├── YourDialectParser.java
│   │   ├── YourDialectGenerator.java
│   │   └── YourDialectTokenizer.java
│   └── ... (other dialects)
└── src/test/java/com/gtkcyber/sqlglot/dialects/
    ├── YourDialectTest.java
    └── ... (other test files)
```

## Next Steps

1. Read `DIALECT_IMPLEMENTATION_GUIDE.md` for comprehensive overview
2. Follow `TEMPLATE_USAGE_EXAMPLE.md` for step-by-step instructions
3. Use `QUICK_REFERENCE.md` while implementing
4. Run `DialectScaffold` to generate initial files
5. Implement dialect-specific components
6. Create and run tests

## Support

For issues or questions:
1. Check `QUICK_REFERENCE.md` for common patterns
2. Review example dialects in `/dialects/`
3. Check existing test cases for examples
4. Review implementation guide for detailed explanations

## Summary

The template system streamlines dialect implementation by:
- Providing ready-to-use templates with clear structure
- Automating placeholder replacement
- Offering comprehensive documentation and examples
- Showing best practices and common patterns
- Enabling new dialect implementation in 15-20 minutes

Start by reading the implementation guide, generate templates, and customize for your dialect!
