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
package com.gtkcyber.sqlglot.dialects.{PACKAGE_PATH};

import com.gtkcyber.sqlglot.dialect.*;
import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;
import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.tokens.Tokenizer;

/**
 * {DIALECT_NAME} SQL dialect.
 *
 * This is a template for implementing a new SQL dialect. Key characteristics:
 * - Identifier quoting style (backticks, double quotes, etc.)
 * - Supported keywords and functions
 * - Normalization strategy (UPPERCASE, LOWERCASE, or PRESERVE)
 * - Dialect-specific syntax features
 *
 * TEMPLATE INSTRUCTIONS:
 * 1. Replace {DIALECT_NAME} with your dialect name (e.g., "SQLite")
 * 2. Replace {DIALECT_NAME_UPPER} with uppercase version (e.g., "SQLITE")
 * 3. Replace {DIALECT_CLASS_NAME} with the class name (e.g., "SqliteDialect")
 * 4. Update the documentation with dialect-specific features
 * 5. Choose appropriate NormalizationStrategy
 * 6. Implement createTokenizer(), createParser(), createGenerator()
 */
public class {DIALECT_CLASS_NAME} extends Dialect {
    /**
     * Creates a new {DIALECT_NAME} dialect instance.
     *
     * The NormalizationStrategy determines how identifiers are normalized:
     * - UPPERCASE: Converts to uppercase (most common for traditional SQL)
     * - LOWERCASE: Converts to lowercase
     * - PRESERVE: Keeps original case
     */
    public {DIALECT_CLASS_NAME}() {
        super("{DIALECT_NAME_UPPER}", NormalizationStrategy.UPPERCASE);
    }

    /**
     * Creates the tokenizer for {DIALECT_NAME}.
     *
     * The tokenizer is responsible for:
     * - Recognizing identifier quote characters (e.g., backticks, double quotes)
     * - Recognizing string quote characters
     * - Building the keyword trie for fast keyword recognition
     * - Handling dialect-specific tokens
     *
     * @return A new {TOKENIZER_CLASS_NAME} instance
     */
    @Override
    public Tokenizer createTokenizer() {
        return new {TOKENIZER_CLASS_NAME}();
    }

    /**
     * Creates the parser for {DIALECT_NAME}.
     *
     * The parser is responsible for:
     * - Parsing SQL statements into an AST (Abstract Syntax Tree)
     * - Handling dialect-specific syntax
     * - Supporting dialect-specific functions and clauses
     *
     * @return A new {PARSER_CLASS_NAME} instance
     */
    @Override
    public Parser createParser() {
        return new {PARSER_CLASS_NAME}();
    }

    /**
     * Creates the generator for {DIALECT_NAME}.
     *
     * The generator is responsible for:
     * - Converting AST back to SQL text
     * - Using dialect-specific formatting for identifiers
     * - Supporting dialect-specific functions
     * - Handling dialect-specific type syntax
     *
     * @param config Generator configuration options
     * @return A new {GENERATOR_CLASS_NAME} instance
     */
    @Override
    public Generator createGenerator(GeneratorConfig config) {
        return new {GENERATOR_CLASS_NAME}(config);
    }
}
