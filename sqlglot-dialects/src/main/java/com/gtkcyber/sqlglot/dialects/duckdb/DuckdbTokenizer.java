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
package com.gtkcyber.sqlglot.dialects.duckdb;

import com.gtkcyber.sqlglot.tokens.Tokenizer;
import com.gtkcyber.sqlglot.tokens.TokenType;
import com.gtkcyber.sqlglot.tokens.Trie;

import java.util.HashMap;
import java.util.Map;

/**
 * DuckDB tokenizer.
 * Supports DuckDB-specific syntax:
 * - Double quote identifiers
 * - Single quote strings
 * - Dollar-quoted strings (for complex strings)
 * - DuckDB-specific keywords and functions
 * - Advanced data type keywords
 */
public class DuckdbTokenizer extends Tokenizer {
    @Override
    protected Map<String, Character> getIdentifierQuotes() {
        Map<String, Character> quotes = new HashMap<>();
        quotes.put("\"", '"');   // Double quotes
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

        // DuckDB-specific keywords
        keywords.put("LIST", TokenType.IDENTIFIER);
        keywords.put("STRUCT", TokenType.IDENTIFIER);
        keywords.put("MAP", TokenType.IDENTIFIER);
        keywords.put("UNION", TokenType.IDENTIFIER);
        keywords.put("READ_CSV", TokenType.IDENTIFIER);
        keywords.put("READ_JSON", TokenType.IDENTIFIER);
        keywords.put("READ_PARQUET", TokenType.IDENTIFIER);
        keywords.put("COPY", TokenType.IDENTIFIER);
        keywords.put("FROM", TokenType.IDENTIFIER);
        keywords.put("TO", TokenType.IDENTIFIER);
        keywords.put("FORMAT", TokenType.IDENTIFIER);
        keywords.put("DELIMITER", TokenType.IDENTIFIER);
        keywords.put("HEADER", TokenType.IDENTIFIER);
        keywords.put("NULL", TokenType.IDENTIFIER);
        keywords.put("ESCAPE", TokenType.IDENTIFIER);
        keywords.put("QUOTE", TokenType.IDENTIFIER);
        keywords.put("DATEFORMAT", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMPFORMAT", TokenType.IDENTIFIER);
        keywords.put("SAMPLE", TokenType.IDENTIFIER);
        keywords.put("PERCENT", TokenType.IDENTIFIER);
        keywords.put("USING", TokenType.IDENTIFIER);
        keywords.put("SAMPLE", TokenType.IDENTIFIER);
        keywords.put("SEED", TokenType.IDENTIFIER);
        keywords.put("RANGE", TokenType.IDENTIFIER);
        keywords.put("WINDOW", TokenType.IDENTIFIER);
        keywords.put("OVER", TokenType.IDENTIFIER);
        keywords.put("PARTITION", TokenType.IDENTIFIER);
        keywords.put("BY", TokenType.IDENTIFIER);
        keywords.put("ORDER", TokenType.IDENTIFIER);
        keywords.put("ROWS", TokenType.IDENTIFIER);
        keywords.put("RANGE", TokenType.IDENTIFIER);
        keywords.put("GROUPS", TokenType.IDENTIFIER);
        keywords.put("UNBOUNDED", TokenType.IDENTIFIER);
        keywords.put("PRECEDING", TokenType.IDENTIFIER);
        keywords.put("FOLLOWING", TokenType.IDENTIFIER);
        keywords.put("CURRENT", TokenType.IDENTIFIER);
        keywords.put("ROW", TokenType.IDENTIFIER);
        keywords.put("BETWEEN", TokenType.IDENTIFIER);
        keywords.put("AND", TokenType.IDENTIFIER);
        keywords.put("EXCLUDE", TokenType.IDENTIFIER);
        keywords.put("NO", TokenType.IDENTIFIER);
        keywords.put("PEERS", TokenType.IDENTIFIER);
        keywords.put("OTHERS", TokenType.IDENTIFIER);
        keywords.put("TIES", TokenType.IDENTIFIER);

        // DuckDB advanced data types
        keywords.put("HUGEINT", TokenType.IDENTIFIER);
        keywords.put("UHUGEINT", TokenType.IDENTIFIER);
        keywords.put("UTINYINT", TokenType.IDENTIFIER);
        keywords.put("USMALLINT", TokenType.IDENTIFIER);
        keywords.put("UINT", TokenType.IDENTIFIER);
        keywords.put("UBIGINT", TokenType.IDENTIFIER);
        keywords.put("FLOAT", TokenType.IDENTIFIER);
        keywords.put("DOUBLE", TokenType.IDENTIFIER);
        keywords.put("DECIMAL", TokenType.IDENTIFIER);
        keywords.put("NUMERIC", TokenType.IDENTIFIER);
        keywords.put("STRING", TokenType.IDENTIFIER);
        keywords.put("VARCHAR", TokenType.IDENTIFIER);
        keywords.put("TEXT", TokenType.IDENTIFIER);
        keywords.put("BLOB", TokenType.IDENTIFIER);
        keywords.put("BYTEA", TokenType.IDENTIFIER);
        keywords.put("DATE", TokenType.IDENTIFIER);
        keywords.put("TIME", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMPTZ", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMPNS", TokenType.IDENTIFIER);
        keywords.put("INTERVAL", TokenType.IDENTIFIER);
        keywords.put("BOOLEAN", TokenType.IDENTIFIER);
        keywords.put("UUID", TokenType.IDENTIFIER);

        // DuckDB functions
        keywords.put("TYPEOF", TokenType.IDENTIFIER);
        keywords.put("CAST", TokenType.IDENTIFIER);
        keywords.put("TRY_CAST", TokenType.IDENTIFIER);
        keywords.put("COALESCE", TokenType.IDENTIFIER);
        keywords.put("NULLIF", TokenType.IDENTIFIER);
        keywords.put("IFNULL", TokenType.IDENTIFIER);
        keywords.put("LENGTH", TokenType.IDENTIFIER);
        keywords.put("SUBSTR", TokenType.IDENTIFIER);
        keywords.put("SUBSTRING", TokenType.IDENTIFIER);
        keywords.put("INSTR", TokenType.IDENTIFIER);
        keywords.put("REPLACE", TokenType.IDENTIFIER);
        keywords.put("TRIM", TokenType.IDENTIFIER);
        keywords.put("LTRIM", TokenType.IDENTIFIER);
        keywords.put("RTRIM", TokenType.IDENTIFIER);
        keywords.put("UPPER", TokenType.IDENTIFIER);
        keywords.put("LOWER", TokenType.IDENTIFIER);
        keywords.put("CONCAT", TokenType.IDENTIFIER);
        keywords.put("CONCAT_WS", TokenType.IDENTIFIER);
        keywords.put("REVERSE", TokenType.IDENTIFIER);
        keywords.put("REPEAT", TokenType.IDENTIFIER);
        keywords.put("EXPLODE", TokenType.IDENTIFIER);
        keywords.put("UNNEST", TokenType.IDENTIFIER);
        keywords.put("LIST_AGGREGATE", TokenType.IDENTIFIER);
        keywords.put("STRUCT_EXTRACT", TokenType.IDENTIFIER);
        keywords.put("ARRAY_LENGTH", TokenType.IDENTIFIER);
        keywords.put("ARRAY_CONTAINS", TokenType.IDENTIFIER);
        keywords.put("ARRAY_DISTINCT", TokenType.IDENTIFIER);
        keywords.put("ARRAY_REVERSE", TokenType.IDENTIFIER);
        keywords.put("ARRAY_UNIQUE", TokenType.IDENTIFIER);
        keywords.put("ARRAY_SLICE", TokenType.IDENTIFIER);
        keywords.put("LIST_SLICE", TokenType.IDENTIFIER);
        keywords.put("LIST_CONTAINS", TokenType.IDENTIFIER);
        keywords.put("LIST_HAS_ANY", TokenType.IDENTIFIER);
        keywords.put("LIST_HAS_ALL", TokenType.IDENTIFIER);
        keywords.put("LIST_UNIQUE", TokenType.IDENTIFIER);
        keywords.put("LIST_DISTINCT", TokenType.IDENTIFIER);
        keywords.put("LIST_SORT", TokenType.IDENTIFIER);
        keywords.put("LIST_REVERSE", TokenType.IDENTIFIER);
        keywords.put("LIST_FLATTEN", TokenType.IDENTIFIER);
        keywords.put("LIST_POSITION", TokenType.IDENTIFIER);
        keywords.put("LIST_EXTRACT", TokenType.IDENTIFIER);
        keywords.put("JSON_EXTRACT", TokenType.IDENTIFIER);
        keywords.put("JSON_EXTRACTALL", TokenType.IDENTIFIER);
        keywords.put("JSON_TYPE", TokenType.IDENTIFIER);
        keywords.put("JSON_VALID", TokenType.IDENTIFIER);
        keywords.put("JSON_ARRAY", TokenType.IDENTIFIER);
        keywords.put("JSON_OBJECT", TokenType.IDENTIFIER);
        keywords.put("JSON_QUOTE", TokenType.IDENTIFIER);
        keywords.put("JSON_ARRAY_LENGTH", TokenType.IDENTIFIER);
        keywords.put("JSON_KEYS", TokenType.IDENTIFIER);
        keywords.put("JSON_EACH", TokenType.IDENTIFIER);
        keywords.put("LEVENSHTEIN", TokenType.IDENTIFIER);
        keywords.put("JACCARD", TokenType.IDENTIFIER);
        keywords.put("JARO_WINKLER", TokenType.IDENTIFIER);
        keywords.put("JARO", TokenType.IDENTIFIER);
        keywords.put("SUBSTR_count", TokenType.IDENTIFIER);
        keywords.put("HAMMING", TokenType.IDENTIFIER);

        // Math functions
        keywords.put("ABS", TokenType.IDENTIFIER);
        keywords.put("ROUND", TokenType.IDENTIFIER);
        keywords.put("CEIL", TokenType.IDENTIFIER);
        keywords.put("FLOOR", TokenType.IDENTIFIER);
        keywords.put("SQRT", TokenType.IDENTIFIER);
        keywords.put("POWER", TokenType.IDENTIFIER);
        keywords.put("EXP", TokenType.IDENTIFIER);
        keywords.put("LN", TokenType.IDENTIFIER);
        keywords.put("LOG", TokenType.IDENTIFIER);
        keywords.put("LOG10", TokenType.IDENTIFIER);
        keywords.put("PI", TokenType.IDENTIFIER);
        keywords.put("RANDOM", TokenType.IDENTIFIER);
        keywords.put("RAND", TokenType.IDENTIFIER);

        // Date/time functions
        keywords.put("CURRENT_DATE", TokenType.IDENTIFIER);
        keywords.put("CURRENT_TIME", TokenType.IDENTIFIER);
        keywords.put("CURRENT_TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("NOW", TokenType.IDENTIFIER);
        keywords.put("EXTRACT", TokenType.IDENTIFIER);
        keywords.put("DATE_TRUNC", TokenType.IDENTIFIER);
        keywords.put("DATE_PART", TokenType.IDENTIFIER);
        keywords.put("DATEADD", TokenType.IDENTIFIER);
        keywords.put("DATEDIFF", TokenType.IDENTIFIER);
        keywords.put("STRFTIME", TokenType.IDENTIFIER);
        keywords.put("STRPTIME", TokenType.IDENTIFIER);
        keywords.put("DATE_FORMAT", TokenType.IDENTIFIER);

        return Trie.build(keywords);
    }
}
