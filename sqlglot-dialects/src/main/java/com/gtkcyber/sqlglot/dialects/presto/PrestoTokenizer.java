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
package com.gtkcyber.sqlglot.dialects.presto;

import com.gtkcyber.sqlglot.tokens.Tokenizer;
import com.gtkcyber.sqlglot.tokens.TokenType;
import com.gtkcyber.sqlglot.tokens.Trie;

import java.util.HashMap;
import java.util.Map;

/**
 * Presto tokenizer.
 * Supports Presto-specific syntax:
 * - Double quote identifiers
 * - Single quote strings with doubled quote escaping
 * - Presto-specific keywords and functions
 * - Complex data types (ROW, ARRAY, MAP)
 */
public class PrestoTokenizer extends Tokenizer {
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

        // Presto-specific keywords
        keywords.put("ROW", TokenType.IDENTIFIER);
        keywords.put("ARRAY", TokenType.IDENTIFIER);
        keywords.put("MAP", TokenType.IDENTIFIER);
        keywords.put("INTERVAL", TokenType.IDENTIFIER);
        keywords.put("YEAR", TokenType.IDENTIFIER);
        keywords.put("MONTH", TokenType.IDENTIFIER);
        keywords.put("DAY", TokenType.IDENTIFIER);
        keywords.put("HOUR", TokenType.IDENTIFIER);
        keywords.put("MINUTE", TokenType.IDENTIFIER);
        keywords.put("SECOND", TokenType.IDENTIFIER);
        keywords.put("MILLISECOND", TokenType.IDENTIFIER);
        keywords.put("MICROSECOND", TokenType.IDENTIFIER);
        keywords.put("NANOSECOND", TokenType.IDENTIFIER);
        keywords.put("SECOND_OF_MINUTE", TokenType.IDENTIFIER);
        keywords.put("MINUTE_OF_HOUR", TokenType.IDENTIFIER);
        keywords.put("HOUR_OF_DAY", TokenType.IDENTIFIER);
        keywords.put("DAY_OF_WEEK", TokenType.IDENTIFIER);
        keywords.put("DAY_OF_MONTH", TokenType.IDENTIFIER);
        keywords.put("DAY_OF_YEAR", TokenType.IDENTIFIER);
        keywords.put("WEEK_OF_YEAR", TokenType.IDENTIFIER);
        keywords.put("MONTH_OF_YEAR", TokenType.IDENTIFIER);
        keywords.put("QUARTER", TokenType.IDENTIFIER);
        keywords.put("QUARTER_OF_YEAR", TokenType.IDENTIFIER);
        keywords.put("YEAR_OF_WEEK", TokenType.IDENTIFIER);
        keywords.put("CATALOG", TokenType.IDENTIFIER);
        keywords.put("SCHEMA", TokenType.IDENTIFIER);
        keywords.put("TABLE", TokenType.IDENTIFIER);
        keywords.put("COLUMN", TokenType.IDENTIFIER);
        keywords.put("CONNECTOR", TokenType.IDENTIFIER);
        keywords.put("DESCRIPTOR", TokenType.IDENTIFIER);
        keywords.put("COMMENT", TokenType.IDENTIFIER);
        keywords.put("PROPERTIES", TokenType.IDENTIFIER);
        keywords.put("LOCATION", TokenType.IDENTIFIER);
        keywords.put("WITH", TokenType.IDENTIFIER);
        keywords.put("FORMAT", TokenType.IDENTIFIER);
        keywords.put("EXTERNAL", TokenType.IDENTIFIER);
        keywords.put("BUCKETED", TokenType.IDENTIFIER);
        keywords.put("CLUSTERED", TokenType.IDENTIFIER);
        keywords.put("INTO", TokenType.IDENTIFIER);
        keywords.put("BUCKETS", TokenType.IDENTIFIER);
        keywords.put("SORTED", TokenType.IDENTIFIER);
        keywords.put("BY", TokenType.IDENTIFIER);

        // Presto data types
        keywords.put("BOOLEAN", TokenType.IDENTIFIER);
        keywords.put("TINYINT", TokenType.IDENTIFIER);
        keywords.put("SMALLINT", TokenType.IDENTIFIER);
        keywords.put("INTEGER", TokenType.IDENTIFIER);
        keywords.put("BIGINT", TokenType.IDENTIFIER);
        keywords.put("DOUBLE", TokenType.IDENTIFIER);
        keywords.put("FLOAT", TokenType.IDENTIFIER);
        keywords.put("DECIMAL", TokenType.IDENTIFIER);
        keywords.put("VARCHAR", TokenType.IDENTIFIER);
        keywords.put("CHAR", TokenType.IDENTIFIER);
        keywords.put("STRING", TokenType.IDENTIFIER);
        keywords.put("DATE", TokenType.IDENTIFIER);
        keywords.put("TIME", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMPTZ", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMP_WITH_TIME_ZONE", TokenType.IDENTIFIER);
        keywords.put("INTERVAL", TokenType.IDENTIFIER);
        keywords.put("JSON", TokenType.IDENTIFIER);
        keywords.put("UUID", TokenType.IDENTIFIER);
        keywords.put("IPADDRESS", TokenType.IDENTIFIER);
        keywords.put("IPPREFIX", TokenType.IDENTIFIER);
        keywords.put("HYPERLOGLOG", TokenType.IDENTIFIER);
        keywords.put("VARBINARY", TokenType.IDENTIFIER);

        // Presto functions
        keywords.put("CAST", TokenType.IDENTIFIER);
        keywords.put("TRY_CAST", TokenType.IDENTIFIER);
        keywords.put("COALESCE", TokenType.IDENTIFIER);
        keywords.put("NULLIF", TokenType.IDENTIFIER);
        keywords.put("IFNULL", TokenType.IDENTIFIER);
        keywords.put("IF", TokenType.IDENTIFIER);
        keywords.put("CASE", TokenType.IDENTIFIER);
        keywords.put("WHEN", TokenType.IDENTIFIER);
        keywords.put("THEN", TokenType.IDENTIFIER);
        keywords.put("ELSE", TokenType.IDENTIFIER);
        keywords.put("END", TokenType.IDENTIFIER);
        keywords.put("APPROX_DISTINCT", TokenType.IDENTIFIER);
        keywords.put("APPROX_PERCENTILE", TokenType.IDENTIFIER);
        keywords.put("APPROX_SET", TokenType.IDENTIFIER);
        keywords.put("ARRAY_AGG", TokenType.IDENTIFIER);
        keywords.put("ARRAY", TokenType.IDENTIFIER);
        keywords.put("ARRAY_DISTINCT", TokenType.IDENTIFIER);
        keywords.put("ARRAY_FILTER", TokenType.IDENTIFIER);
        keywords.put("ARRAY_FLATTEN", TokenType.IDENTIFIER);
        keywords.put("ARRAY_JOIN", TokenType.IDENTIFIER);
        keywords.put("ARRAY_LENGTH", TokenType.IDENTIFIER);
        keywords.put("ARRAY_POSITION", TokenType.IDENTIFIER);
        keywords.put("ARRAY_REMOVE", TokenType.IDENTIFIER);
        keywords.put("ARRAY_REVERSE", TokenType.IDENTIFIER);
        keywords.put("ARRAY_SORT", TokenType.IDENTIFIER);
        keywords.put("ARRAY_SLICE", TokenType.IDENTIFIER);
        keywords.put("ARRAY_UNION", TokenType.IDENTIFIER);
        keywords.put("CONCAT", TokenType.IDENTIFIER);
        keywords.put("CONCAT_WS", TokenType.IDENTIFIER);
        keywords.put("ELEMENT_AT", TokenType.IDENTIFIER);
        keywords.put("FLATTEN", TokenType.IDENTIFIER);
        keywords.put("MAP", TokenType.IDENTIFIER);
        keywords.put("MAP_CONCAT", TokenType.IDENTIFIER);
        keywords.put("MAP_FILTER", TokenType.IDENTIFIER);
        keywords.put("MAP_KEYS", TokenType.IDENTIFIER);
        keywords.put("MAP_VALUES", TokenType.IDENTIFIER);
        keywords.put("MAP_ENTRIES", TokenType.IDENTIFIER);
        keywords.put("MULTIMAP_FROM_ENTRIES", TokenType.IDENTIFIER);
        keywords.put("TRANSFORM_KEYS", TokenType.IDENTIFIER);
        keywords.put("TRANSFORM_VALUES", TokenType.IDENTIFIER);
        keywords.put("ROW", TokenType.IDENTIFIER);
        keywords.put("ROW_CONSTRUCTOR", TokenType.IDENTIFIER);
        keywords.put("FIELD", TokenType.IDENTIFIER);
        keywords.put("CONTAINS", TokenType.IDENTIFIER);
        keywords.put("CONTAINSKEY", TokenType.IDENTIFIER);
        keywords.put("SUBSTR", TokenType.IDENTIFIER);
        keywords.put("SUBSTRING", TokenType.IDENTIFIER);
        keywords.put("LENGTH", TokenType.IDENTIFIER);
        keywords.put("LTRIM", TokenType.IDENTIFIER);
        keywords.put("RTRIM", TokenType.IDENTIFIER);
        keywords.put("TRIM", TokenType.IDENTIFIER);
        keywords.put("UPPER", TokenType.IDENTIFIER);
        keywords.put("LOWER", TokenType.IDENTIFIER);
        keywords.put("INITCAP", TokenType.IDENTIFIER);
        keywords.put("LPAD", TokenType.IDENTIFIER);
        keywords.put("RPAD", TokenType.IDENTIFIER);
        keywords.put("REPLACE", TokenType.IDENTIFIER);
        keywords.put("REVERSE", TokenType.IDENTIFIER);
        keywords.put("REPEAT", TokenType.IDENTIFIER);
        keywords.put("CHR", TokenType.IDENTIFIER);
        keywords.put("CODEPOINT", TokenType.IDENTIFIER);
        keywords.put("STRPOS", TokenType.IDENTIFIER);
        keywords.put("INSTR", TokenType.IDENTIFIER);
        keywords.put("SPLIT", TokenType.IDENTIFIER);
        keywords.put("REGEXP_EXTRACT_ALL", TokenType.IDENTIFIER);
        keywords.put("REGEXP_LIKE", TokenType.IDENTIFIER);
        keywords.put("REGEXP_REPLACE", TokenType.IDENTIFIER);
        keywords.put("REGEXP_SPLIT", TokenType.IDENTIFIER);
        keywords.put("FROM_UNIXTIME", TokenType.IDENTIFIER);
        keywords.put("TO_UNIXTIME", TokenType.IDENTIFIER);
        keywords.put("FROM_ISO8601_TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("TO_ISO8601", TokenType.IDENTIFIER);
        keywords.put("CURRENT_DATE", TokenType.IDENTIFIER);
        keywords.put("CURRENT_TIME", TokenType.IDENTIFIER);
        keywords.put("CURRENT_TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("NOW", TokenType.IDENTIFIER);
        keywords.put("DATE_FORMAT", TokenType.IDENTIFIER);
        keywords.put("DATE_PARSE", TokenType.IDENTIFIER);
        keywords.put("DATE_DIFF", TokenType.IDENTIFIER);
        keywords.put("DATE_ADD", TokenType.IDENTIFIER);
        keywords.put("DATE_SUBTRACT", TokenType.IDENTIFIER);
        keywords.put("DATE_TRUNC", TokenType.IDENTIFIER);
        keywords.put("EXTRACT", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMP_DIFF", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMP_ADD", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMP_SUBTRACT", TokenType.IDENTIFIER);
        keywords.put("AT_TIMEZONE", TokenType.IDENTIFIER);
        keywords.put("TIMEZONE_HOUR", TokenType.IDENTIFIER);
        keywords.put("TIMEZONE_MINUTE", TokenType.IDENTIFIER);
        keywords.put("JSON_EXTRACT", TokenType.IDENTIFIER);
        keywords.put("JSON_EXTRACT_SCALAR", TokenType.IDENTIFIER);
        keywords.put("JSON_ARRAY_LENGTH", TokenType.IDENTIFIER);
        keywords.put("JSON_ARRAY_CONTAINS", TokenType.IDENTIFIER);
        keywords.put("JSON_ARRAY_GET", TokenType.IDENTIFIER);
        keywords.put("JSON_SIZE", TokenType.IDENTIFIER);
        keywords.put("JSON_PARSE", TokenType.IDENTIFIER);
        keywords.put("TO_JSON", TokenType.IDENTIFIER);
        keywords.put("JSON_FORMAT", TokenType.IDENTIFIER);
        keywords.put("IS_JSON", TokenType.IDENTIFIER);
        keywords.put("JSON_TREE", TokenType.IDENTIFIER);
        keywords.put("JSON_EXTRACT_PATH", TokenType.IDENTIFIER);
        keywords.put("JSON_EXTRACT_PATH_TEXT", TokenType.IDENTIFIER);
        keywords.put("UUID", TokenType.IDENTIFIER);
        keywords.put("IPADDRESS", TokenType.IDENTIFIER);
        keywords.put("IPPREFIX", TokenType.IDENTIFIER);
        keywords.put("IP_FROM_STRING", TokenType.IDENTIFIER);
        keywords.put("IP_FROM_BYTES", TokenType.IDENTIFIER);
        keywords.put("IP_TO_STRING", TokenType.IDENTIFIER);
        keywords.put("IP_TO_BYTES", TokenType.IDENTIFIER);
        keywords.put("IS_IPADDRESS", TokenType.IDENTIFIER);
        keywords.put("IS_IPPREFIX", TokenType.IDENTIFIER);
        keywords.put("IPPREFIX_MATCH", TokenType.IDENTIFIER);
        keywords.put("IPADDRESS_NETMASK", TokenType.IDENTIFIER);

        return Trie.build(keywords);
    }
}
