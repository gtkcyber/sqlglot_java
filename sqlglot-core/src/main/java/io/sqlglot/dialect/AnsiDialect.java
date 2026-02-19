package io.sqlglot.dialect;

import io.sqlglot.generator.Generator;
import io.sqlglot.generator.GeneratorConfig;
import io.sqlglot.parser.Parser;
import io.sqlglot.tokens.Tokenizer;

/**
 * ANSI SQL dialect (standard SQL).
 * This is the base dialect from which other dialects may inherit.
 */
public class AnsiDialect extends Dialect {
    public AnsiDialect() {
        super("ANSI", NormalizationStrategy.UPPERCASE);
    }

    @Override
    public Tokenizer createTokenizer() {
        return new Tokenizer();
    }

    @Override
    public Parser createParser() {
        return new Parser();
    }

    @Override
    public Generator createGenerator(GeneratorConfig config) {
        return new Generator(config);
    }
}
