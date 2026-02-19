package io.sqlglot.dialects.drill;

import io.sqlglot.dialect.*;
import io.sqlglot.generator.Generator;
import io.sqlglot.generator.GeneratorConfig;
import io.sqlglot.parser.Parser;
import io.sqlglot.tokens.Tokenizer;

/**
 * Apache Drill SQL dialect.
 *
 * Drill is a distributed SQL query engine for large-scale data exploration and analytics.
 * Key features:
 * - Schema-less data model (JSON, Parquet, CSV, etc.)
 * - Workspace and schema syntax: workspace.schema.table
 * - Backtick identifiers for reserved words and paths
 * - Extended CAST syntax for complex types (ARRAY, MAP, STRUCT)
 * - Dynamic UDF support
 * - File-based sources and plugin architecture
 */
public class DrillDialect extends Dialect {
    public DrillDialect() {
        super("DRILL", NormalizationStrategy.UPPERCASE);
    }

    @Override
    public Tokenizer createTokenizer() {
        return new DrillTokenizer();
    }

    @Override
    public Parser createParser() {
        return new DrillParser();
    }

    @Override
    public Generator createGenerator(GeneratorConfig config) {
        return new DrillGenerator(config);
    }
}
