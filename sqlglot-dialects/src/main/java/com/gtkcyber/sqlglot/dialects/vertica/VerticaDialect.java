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
package com.gtkcyber.sqlglot.dialects.vertica;

import com.gtkcyber.sqlglot.dialect.*;
import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;
import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.tokens.Tokenizer;

/**
 * Vertica SQL dialect.
 *
 * Vertica columnar analytics engine
 */
public class VerticaDialect extends Dialect {
    /**
     * Constructs a Vertica dialect instance.
     */
    public VerticaDialect() {
        super("Vertica", NormalizationStrategy.UPPERCASE);
    }

    /**
     * Creates a Vertica-specific tokenizer.
     *
     * @return a new VerticaTokenizer instance
     */
    @Override
    public Tokenizer createTokenizer() {
        return new VerticaTokenizer();
    }

    /**
     * Creates a Vertica-specific parser.
     *
     * @return a new VerticaParser instance
     */
    @Override
    public Parser createParser() {
        return new VerticaParser();
    }

    /**
     * Creates a Vertica-specific generator.
     *
     * @param config the generator configuration
     * @return a new VerticaGenerator instance
     */
    @Override
    public Generator createGenerator(GeneratorConfig config) {
        if (config == null) {
            config = GeneratorConfig.defaultConfig();
        }
        return new VerticaGenerator(config);
    }
}
