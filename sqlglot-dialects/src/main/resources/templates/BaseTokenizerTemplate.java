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

import com.gtkcyber.sqlglot.tokens.Tokenizer;
import com.gtkcyber.sqlglot.tokens.TokenType;
import com.gtkcyber.sqlglot.tokens.Trie;

import java.util.HashMap;
import java.util.Map;

/**
 * {DIALECT_NAME}-specific tokenizer.
 *
 * The tokenizer breaks SQL text into tokens (keywords, identifiers, literals, etc.).
 * It's responsible for:
 * - Recognizing identifier and string quote characters
 * - Building a trie of keywords for fast recognition
 * - Handling dialect-specific syntax rules
 *
 * TEMPLATE INSTRUCTIONS:
 * 1. Replace {DIALECT_NAME} with your dialect name
 * 2. Replace {TOKENIZER_CLASS_NAME} with your tokenizer class name
 * 3. Override methods to customize tokenization:
 *    - getIdentifierQuotes(): Define how identifiers are quoted
 *    - getStringQuotes(): Define how strings are quoted
 *    - buildKeywordTrie(): Define dialect-specific keywords
 *
 * COMMON IDENTIFIER QUOTING STYLES:
 * - Double quotes: " (SQL standard, PostgreSQL)
 * - Backticks: ` (MySQL, Drill)
 * - Square brackets: [ (SQL Server)
 * - No quoting: (simple dialects)
 *
 * COMMON STRING QUOTING STYLES:
 * - Single quotes: ' (SQL standard)
 * - Double quotes: " (can be both string and identifier in some dialects)
 * - Escape character: \ (backslash) or " (double the quote)
 */
public class {TOKENIZER_CLASS_NAME} extends Tokenizer {
    /**
     * Gets the identifier quote characters for {DIALECT_NAME}.
     *
     * This defines how identifiers (table names, column names, etc.) are quoted.
     *
     * Common patterns:
     * - Double quotes: "column_name"
     * - Backticks: `column_name`
     * - Square brackets: [column_name]
     *
     * @return Map of quote character to escape character
     *         Key: opening quote (e.g., "\"")
     *         Value: escape character (e.g., '\"' for doubled quotes)
     */
    @Override
    protected Map<String, Character> getIdentifierQuotes() {
        Map<String, Character> quotes = new HashMap<>();

        // TEMPLATE: Define identifier quoting for your dialect
        // Example: Double quotes with doubled quote escaping (SQL standard)
        quotes.put("\"", '"');  // "column" with "" for escaped quote

        // Alternative: Backticks with doubled backtick escaping (MySQL style)
        // quotes.put("`", '`');  // `column` with `` for escaped backtick

        // Alternative: Square brackets (SQL Server style)
        // quotes.put("[", ']');  // [column] with ]] for escaped bracket

        return quotes;
    }

    /**
     * Gets the string quote characters for {DIALECT_NAME}.
     *
     * This defines how string literals are quoted.
     *
     * Common patterns:
     * - Single quotes: 'string value'
     * - Double quotes: "string value" (when not used for identifiers)
     * - Escape character: \ (backslash) or " (doubled quote)
     *
     * @return Map of quote character to escape character
     *         Key: opening quote (e.g., "'")
     *         Value: escape character (e.g., '\'', '\\', or '"')
     */
    @Override
    protected Map<String, Character> getStringQuotes() {
        Map<String, Character> quotes = new HashMap<>();

        // TEMPLATE: Define string quoting for your dialect
        // Example: Single quotes with doubled quote escaping (SQL standard)
        quotes.put("'", '\'');  // 'string' with '' for escaped quote

        // Alternative: With backslash escaping
        // quotes.put("'", '\\');  // 'string' with \' for escaped quote

        // Note: Double quotes may also be used for strings in some dialects
        // quotes.put("\"", '\\');

        return quotes;
    }

    /**
     * Builds the keyword trie for {DIALECT_NAME}.
     *
     * The keyword trie is used for fast recognition of keywords vs identifiers.
     * All keywords should be mapped to their TokenType.
     *
     * Common keyword types:
     * - TokenType.SELECT, TokenType.FROM, TokenType.WHERE, etc. (standard keywords)
     * - TokenType.IDENTIFIER (for custom keywords or potential keywords)
     *
     * @return Trie data structure for keyword recognition
     */
    @Override
    protected Trie buildKeywordTrie() {
        Map<String, TokenType> keywords = new HashMap<>();

        // TEMPLATE: Add standard SQL keywords
        // These should typically include all standard SQL keywords
        // Start with standard keywords (you may inherit these from base Tokenizer)
        for (TokenType type : TokenType.values()) {
            if (type.isKeyword() && type.getText() != null) {
                keywords.put(type.getText(), type);
            }
        }

        // TEMPLATE: Add dialect-specific keywords
        // Example keywords for a hypothetical dialect:
        keywords.put("PRAGMA", TokenType.IDENTIFIER);           // SQLite: PRAGMA statements
        keywords.put("ATTACH", TokenType.IDENTIFIER);           // SQLite: ATTACH DATABASE
        keywords.put("AUTOINCREMENT", TokenType.IDENTIFIER);    // SQLite: AUTOINCREMENT
        keywords.put("COLLATE", TokenType.IDENTIFIER);          // Collation keyword
        keywords.put("CONFLICT", TokenType.IDENTIFIER);         // Conflict resolution
        keywords.put("GLOB", TokenType.IDENTIFIER);             // Glob pattern matching
        keywords.put("REGEXP", TokenType.IDENTIFIER);           // Regular expression matching
        keywords.put("VACUUM", TokenType.IDENTIFIER);           // SQLite: VACUUM command
        keywords.put("ANALYZE", TokenType.IDENTIFIER);          // Table analysis
        keywords.put("EXPLAIN", TokenType.IDENTIFIER);          // Query plan explanation
        keywords.put("QUERY", TokenType.IDENTIFIER);            // Query explanation
        keywords.put("PLAN", TokenType.IDENTIFIER);             // For EXPLAIN QUERY PLAN

        // TEMPLATE: Add dialect-specific functions (if they should be recognized as keywords)
        keywords.put("IFNULL", TokenType.IDENTIFIER);           // NULL coalescing function
        keywords.put("NULLIF", TokenType.IDENTIFIER);           // NULL comparison function
        keywords.put("TYPEOF", TokenType.IDENTIFIER);           // Type checking function
        keywords.put("RANDOM", TokenType.IDENTIFIER);           // Random number function

        // TEMPLATE: Add common date/time functions if specific to dialect
        keywords.put("DATE", TokenType.IDENTIFIER);             // Date function
        keywords.put("TIME", TokenType.IDENTIFIER);             // Time function
        keywords.put("DATETIME", TokenType.IDENTIFIER);         // DateTime function
        keywords.put("STRFTIME", TokenType.IDENTIFIER);         // String format time function

        return Trie.build(keywords);
    }

    /**
     * DIALECT-SPECIFIC CONFIGURATION GUIDE
     *
     * When implementing a new dialect, consider:
     *
     * 1. IDENTIFIER QUOTING
     *    - What characters are used to quote identifiers?
     *    - How are escaped quotes handled?
     *    - Example: SELECT "column_name" FROM "table"
     *
     * 2. STRING QUOTING
     *    - What characters are used for string literals?
     *    - How are escaped quotes handled?
     *    - Can strings use different quote styles?
     *    - Example: SELECT 'string value', "another string"
     *
     * 3. KEYWORDS
     *    - What keywords are specific to this dialect?
     *    - Are there dialect-specific functions?
     *    - Are there dialect-specific clauses?
     *    - Examples:
     *      - SQLite: PRAGMA, ATTACH, AUTOINCREMENT
     *      - PostgreSQL: ARRAY, JSONB, ENUM types
     *      - MySQL: LIMIT, OFFSET syntax variations
     *      - SQL Server: GO statement, brackets for identifiers
     *      - Snowflake: STAGE, CLUSTER, TIME_ZONE
     *
     * 4. SPECIAL TOKENS
     *    - Are there special characters or operators?
     *    - Do you need custom token handling?
     *    - Examples: :: (PostgreSQL casting), @ (Snowflake arrays)
     *
     * TESTING KEYWORDS
     * After implementing the tokenizer, test with:
     * <pre>
     * {@code
     * {TOKENIZER_CLASS_NAME} tokenizer = new {TOKENIZER_CLASS_NAME}();
     * List<Token> tokens = tokenizer.tokenize("SELECT * FROM table");
     * assert tokens.get(0).type() == TokenType.SELECT;
     * assert tokens.get(3).type() == TokenType.FROM;
     * }
     * </pre>
     */
}
