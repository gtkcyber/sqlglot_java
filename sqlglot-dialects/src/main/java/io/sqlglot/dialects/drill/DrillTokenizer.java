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
package io.sqlglot.dialects.drill;

import io.sqlglot.tokens.Tokenizer;
import io.sqlglot.tokens.TokenType;

import java.util.HashMap;
import java.util.Map;

/**
 * Apache Drill-specific tokenizer.
 * - Backtick identifiers (same as MySQL)
 * - Workspace path syntax
 * - Extended data type keywords
 */
public class DrillTokenizer extends Tokenizer {
    @Override
    protected Map<String, Character> getIdentifierQuotes() {
        Map<String, Character> quotes = new HashMap<>();
        quotes.put("`", '`');   // Backtick for identifiers and paths
        quotes.put("\"", '\\'); // Double quotes also supported
        return quotes;
    }

    @Override
    protected Map<String, Character> getStringQuotes() {
        Map<String, Character> quotes = new HashMap<>();
        quotes.put("'", '\'');  // Single quote
        quotes.put("\"", '\\'); // Double quote (can be both string and identifier)
        return quotes;
    }

    @Override
    protected io.sqlglot.tokens.Trie buildKeywordTrie() {
        // Start with standard SQL keywords
        Map<String, TokenType> keywords = new HashMap<>();

        // Include all standard keywords
        for (TokenType type : TokenType.values()) {
            if (type.isKeyword() && type.getText() != null) {
                keywords.put(type.getText(), type);
            }
        }

        // Add Drill-specific keywords
        keywords.put("WORKSPACE", TokenType.IDENTIFIER);
        keywords.put("PLUGIN", TokenType.IDENTIFIER);
        keywords.put("FLATTEN", TokenType.IDENTIFIER);
        keywords.put("REPEATED", TokenType.IDENTIFIER);
        keywords.put("OPTIONAL", TokenType.IDENTIFIER);
        keywords.put("REQUIRED", TokenType.IDENTIFIER);
        keywords.put("MATERIALIZATION", TokenType.IDENTIFIER);
        keywords.put("REFRESH", TokenType.IDENTIFIER);
        keywords.put("SUMMARIZE", TokenType.IDENTIFIER);

        return io.sqlglot.tokens.Trie.build(keywords);
    }
}
