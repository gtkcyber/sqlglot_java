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
package io.sqlglot.dialect;

import io.sqlglot.expressions.Expression;
import io.sqlglot.generator.Generator;
import io.sqlglot.generator.GeneratorConfig;
import io.sqlglot.parser.Parser;
import io.sqlglot.tokens.Tokenizer;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Abstract base class for SQL dialects.
 * Each dialect provides its own Tokenizer, Parser, and Generator implementations.
 */
public abstract class Dialect {
    private final String name;
    private final NormalizationStrategy normalizationStrategy;

    protected Dialect(String name, NormalizationStrategy normalizationStrategy) {
        this.name = Objects.requireNonNull(name);
        this.normalizationStrategy = Objects.requireNonNull(normalizationStrategy);
    }

    public String getName() {
        return name;
    }

    public NormalizationStrategy getNormalizationStrategy() {
        return normalizationStrategy;
    }

    /**
     * Creates a tokenizer for this dialect.
     */
    public abstract Tokenizer createTokenizer();

    /**
     * Creates a parser for this dialect.
     */
    public abstract Parser createParser();

    /**
     * Creates a generator for this dialect.
     */
    public abstract Generator createGenerator(GeneratorConfig config);

    /**
     * Parses SQL with this dialect.
     */
    public List<Expression> parse(String sql) {
        return createParser().parse(sql);
    }

    /**
     * Parses a single SQL statement with this dialect.
     */
    public Optional<Expression> parseOne(String sql) {
        List<Expression> expressions = parse(sql);
        return expressions.isEmpty() ? Optional.empty() : Optional.of(expressions.get(0));
    }

    /**
     * Generates SQL for an expression using this dialect.
     */
    public String generate(Expression expr) {
        return generate(expr, GeneratorConfig.defaultConfig());
    }

    /**
     * Generates SQL for an expression with custom config.
     */
    public String generate(Expression expr, GeneratorConfig config) {
        return createGenerator(config).sql(expr);
    }

    /**
     * Transpiles SQL from another dialect to this dialect.
     */
    public String transpile(String sql, String fromDialectName) {
        Dialect fromDialect = DialectRegistry.getInstance().get(fromDialectName);
        Optional<Expression> expr = fromDialect.parseOne(sql);
        if (expr.isEmpty()) {
            return "";
        }
        return generate(expr.get());
    }

    /**
     * Formats SQL with pretty printing.
     */
    public String format(String sql) {
        Optional<Expression> expr = parseOne(sql);
        if (expr.isEmpty()) {
            return "";
        }
        GeneratorConfig config = new GeneratorConfig(true, false, true, 0);
        return generate(expr.get(), config);
    }

    /**
     * Gets a dialect by name.
     */
    public static Dialect of(String name) {
        return DialectRegistry.getInstance().get(name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dialect)) return false;
        Dialect dialect = (Dialect) o;
        return name.equalsIgnoreCase(dialect.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }
}
