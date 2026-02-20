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
package com.gtkcyber.sqlglot.dialects.sqlite;

import com.gtkcyber.sqlglot.tokens.Tokenizer;
import com.gtkcyber.sqlglot.tokens.TokenType;
import com.gtkcyber.sqlglot.tokens.Trie;

import java.util.HashMap;
import java.util.Map;

/**
 * SQLite tokenizer.
 * Supports SQLite-specific syntax:
 * - Double quote and square bracket identifiers
 * - Single quote strings with standard SQL escaping
 * - SQLite-specific keywords and functions
 */
public class SqliteTokenizer extends Tokenizer {
    @Override
    protected Map<String, Character> getIdentifierQuotes() {
        Map<String, Character> quotes = new HashMap<>();
        quotes.put("\"", '"');   // Double quotes
        quotes.put("[", ']');    // Square brackets
        quotes.put("`", '`');    // Backticks (for MySQL compatibility)
        return quotes;
    }

    @Override
    protected Map<String, Character> getStringQuotes() {
        Map<String, Character> quotes = new HashMap<>();
        quotes.put("'", '\'');   // Single quote with doubled quote escaping
        return quotes;
    }

    @Override
    protected Trie buildKeywordTrie() {
        Map<String, TokenType> keywords = new HashMap<>();

        // Standard SQL keywords
        for (TokenType type : TokenType.values()) {
            if (type.isKeyword() && type.getText() != null) {
                keywords.put(type.getText(), type);
            }
        }

        // SQLite-specific keywords
        keywords.put("PRAGMA", TokenType.IDENTIFIER);
        keywords.put("ATTACH", TokenType.IDENTIFIER);
        keywords.put("DATABASE", TokenType.IDENTIFIER);
        keywords.put("DETACH", TokenType.IDENTIFIER);
        keywords.put("AUTOINCREMENT", TokenType.IDENTIFIER);
        keywords.put("COLLATE", TokenType.IDENTIFIER);
        keywords.put("CONFLICT", TokenType.IDENTIFIER);
        keywords.put("GLOB", TokenType.IDENTIFIER);
        keywords.put("REGEXP", TokenType.IDENTIFIER);
        keywords.put("VACUUM", TokenType.IDENTIFIER);
        keywords.put("ANALYZE", TokenType.IDENTIFIER);
        keywords.put("EXPLAIN", TokenType.IDENTIFIER);
        keywords.put("QUERY", TokenType.IDENTIFIER);
        keywords.put("PLAN", TokenType.IDENTIFIER);
        keywords.put("INDEXED", TokenType.IDENTIFIER);
        keywords.put("WITHOUT", TokenType.IDENTIFIER);
        keywords.put("ROWID", TokenType.IDENTIFIER);
        keywords.put("OID", TokenType.IDENTIFIER);
        keywords.put("RETURNING", TokenType.IDENTIFIER);
        keywords.put("GENERATED", TokenType.IDENTIFIER);
        keywords.put("ALWAYS", TokenType.IDENTIFIER);
        keywords.put("STORED", TokenType.IDENTIFIER);

        // SQLite-specific functions
        keywords.put("IFNULL", TokenType.IDENTIFIER);
        keywords.put("NULLIF", TokenType.IDENTIFIER);
        keywords.put("TYPEOF", TokenType.IDENTIFIER);
        keywords.put("RANDOM", TokenType.IDENTIFIER);
        keywords.put("ABS", TokenType.IDENTIFIER);
        keywords.put("ROUND", TokenType.IDENTIFIER);
        keywords.put("UPPER", TokenType.IDENTIFIER);
        keywords.put("LOWER", TokenType.IDENTIFIER);
        keywords.put("LENGTH", TokenType.IDENTIFIER);
        keywords.put("SUBSTR", TokenType.IDENTIFIER);
        keywords.put("TRIM", TokenType.IDENTIFIER);
        keywords.put("LTRIM", TokenType.IDENTIFIER);
        keywords.put("RTRIM", TokenType.IDENTIFIER);
        keywords.put("INSTR", TokenType.IDENTIFIER);
        keywords.put("REPLACE", TokenType.IDENTIFIER);
        keywords.put("COALESCE", TokenType.IDENTIFIER);
        keywords.put("CAST", TokenType.IDENTIFIER);
        keywords.put("LIKE", TokenType.IDENTIFIER);
        keywords.put("ESCAPE", TokenType.IDENTIFIER);

        // Date/time functions
        keywords.put("DATE", TokenType.IDENTIFIER);
        keywords.put("TIME", TokenType.IDENTIFIER);
        keywords.put("DATETIME", TokenType.IDENTIFIER);
        keywords.put("JULIANDAY", TokenType.IDENTIFIER);
        keywords.put("STRFTIME", TokenType.IDENTIFIER);

        // Aggregate functions
        keywords.put("COUNT", TokenType.IDENTIFIER);
        keywords.put("SUM", TokenType.IDENTIFIER);
        keywords.put("AVG", TokenType.IDENTIFIER);
        keywords.put("MIN", TokenType.IDENTIFIER);
        keywords.put("MAX", TokenType.IDENTIFIER);
        keywords.put("GROUP_CONCAT", TokenType.IDENTIFIER);
        keywords.put("TOTAL", TokenType.IDENTIFIER);

        return Trie.build(keywords);
    }
}
