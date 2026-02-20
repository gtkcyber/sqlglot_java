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

import com.gtkcyber.sqlglot.expressions.Expression;
import com.gtkcyber.sqlglot.expressions.Nodes;
import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.parser.ParserConfig;

/**
 * {DIALECT_NAME}-specific parser.
 *
 * This parser extends the base Parser class to support dialect-specific syntax.
 *
 * TEMPLATE INSTRUCTIONS:
 * 1. Replace {DIALECT_NAME} with your dialect name
 * 2. Replace {PARSER_CLASS_NAME} with your parser class name
 * 3. Override methods in Parser to customize parsing behavior
 * 4. Common methods to override:
 *    - parseStatement(): For dialect-specific statement types
 *    - parseExpression(): For dialect-specific expression syntax
 *    - parseFunction(): For dialect-specific functions
 *    - parseDataType(): For dialect-specific data types
 *    - parsePrimary(): For dialect-specific primary expressions
 *
 * COMMON PARSING PATTERNS:
 * - Check for specific keywords: match(keyword) or peek(keyword)
 * - Consume tokens: advance() to move to next token
 * - Parse nested structures: Recursively call parseExpression(), parseIdentifier(), etc.
 * - Build AST nodes: Create Nodes.* objects and set properties
 * - Error handling: Throw ParseException with descriptive messages
 */
public class {PARSER_CLASS_NAME} extends Parser {
    /**
     * Creates a new {DIALECT_NAME} parser with default configuration.
     */
    public {PARSER_CLASS_NAME}() {
        super(ParserConfig.defaultConfig());
    }

    /**
     * Creates a new {DIALECT_NAME} parser with custom configuration.
     *
     * @param config Parser configuration options
     */
    public {PARSER_CLASS_NAME}(ParserConfig config) {
        super(config);
    }

    /**
     * Override to parse dialect-specific statement types.
     *
     * Example:
     * <pre>
     * {@code
     * Optional<Expression> stmt = super.parseStatement();
     * if (stmt.isPresent()) {
     *     return stmt;
     * }
     *
     * if (match("{DIALECT_SPECIFIC_KEYWORD}")) {
     *     return Optional.of(parseDialectSpecificStatement());
     * }
     *
     * return Optional.empty();
     * }
     * </pre>
     */
    // @Override
    // public Optional<Expression> parseStatement() {
    //     // Add dialect-specific statement parsing here
    //     return super.parseStatement();
    // }

    /**
     * Override to parse dialect-specific expressions.
     *
     * Example:
     * <pre>
     * {@code
     * if (match("{DIALECT_SPECIFIC_KEYWORD}")) {
     *     return parseDialectSpecificExpression();
     * }
     * return super.parseExpression();
     * }
     * </pre>
     */
    // @Override
    // public Expression parseExpression() {
    //     // Add dialect-specific expression parsing here
    //     return super.parseExpression();
    // }

    /**
     * Override to parse dialect-specific functions.
     *
     * Example:
     * <pre>
     * {@code
     * String functionName = currentToken().text();
     * if ("SPECIAL_FUNCTION".equalsIgnoreCase(functionName)) {
     *     advance(); // consume function name
     *     // Parse function-specific syntax
     *     Expression arg = parseExpression();
     *     return new Nodes.Function(functionName, Collections.singletonList(arg));
     * }
     * return super.parseFunction();
     * }
     * </pre>
     */
    // @Override
    // public Expression parseFunction() {
    //     // Add dialect-specific function parsing here
    //     return super.parseFunction();
    // }

    /**
     * Override to parse dialect-specific data types.
     *
     * Example:
     * <pre>
     * {@code
     * if (match("CUSTOM_TYPE")) {
     *     String typeName = previous().text();
     *     // Parse type parameters
     *     return new Nodes.DataType(typeName);
     * }
     * return super.parseDataType();
     * }
     * </pre>
     */
    // @Override
    // public Expression parseDataType() {
    //     // Add dialect-specific data type parsing here
    //     return super.parseDataType();
    // }

    /**
     * Override to parse dialect-specific primary expressions.
     *
     * Example:
     * <pre>
     * {@code
     * if (match("SPECIAL_LITERAL")) {
     *     // Parse special literal syntax
     *     return new Nodes.Literal(currentToken().text());
     * }
     * return super.parsePrimary();
     * }
     * </pre>
     */
    // @Override
    // public Expression parsePrimary() {
    //     // Add dialect-specific primary expression parsing here
    //     return super.parsePrimary();
    // }

    /**
     * Helper method to check if current token matches a keyword.
     * Use this pattern: if (match("keyword")) { ... }
     *
     * @param keyword The keyword to match
     * @return true if current token is the keyword, false otherwise
     */
    // protected boolean match(String keyword) {
    //     if (currentToken().type() == TokenType.IDENTIFIER &&
    //         keyword.equalsIgnoreCase(currentToken().text())) {
    //         advance();
    //         return true;
    //     }
    //     return false;
    // }

    /**
     * Helper method to check if current token is a specific keyword without consuming it.
     * Use this pattern: if (peek("keyword")) { ... advance(); ... }
     *
     * @param keyword The keyword to peek
     * @return true if current token is the keyword, false otherwise
     */
    // protected boolean peek(String keyword) {
    //     return currentToken().type() == TokenType.IDENTIFIER &&
    //            keyword.equalsIgnoreCase(currentToken().text());
    // }
}
