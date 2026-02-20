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
package com.gtkcyber.sqlglot.dialects.trino;

import com.gtkcyber.sqlglot.tokens.Tokenizer;
import com.gtkcyber.sqlglot.tokens.TokenType;
import com.gtkcyber.sqlglot.tokens.Trie;

import java.util.HashMap;
import java.util.Map;

/**
 * Trino tokenizer.
 * Supports Trino-specific syntax:
 * - Double quote identifiers (primary)
 * - Backtick identifiers (alternative)
 * - Single quote strings with doubled quote escaping
 * - Complex data types (ROW, ARRAY, MAP)
 * - Interval arithmetic keywords
 * - UUID and IPADDRESS types
 * - JSON operations
 * - APPROX functions
 * - Lambda expressions
 * - Grouping functions
 */
public class TrinoTokenizer extends Tokenizer {
    @Override
    protected Map<String, Character> getIdentifierQuotes() {
        Map<String, Character> quotes = new HashMap<>();
        quotes.put("\"", '"');   // Double quotes (primary)
        quotes.put("`", '`');    // Backticks (alternative)
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

        // Interval keywords
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
        keywords.put("YEAR_TO_MONTH", TokenType.IDENTIFIER);
        keywords.put("DAY_TO_HOUR", TokenType.IDENTIFIER);
        keywords.put("DAY_TO_MINUTE", TokenType.IDENTIFIER);
        keywords.put("DAY_TO_SECOND", TokenType.IDENTIFIER);
        keywords.put("HOUR_TO_MINUTE", TokenType.IDENTIFIER);
        keywords.put("HOUR_TO_SECOND", TokenType.IDENTIFIER);
        keywords.put("MINUTE_TO_SECOND", TokenType.IDENTIFIER);

        // Data types
        keywords.put("ROW", TokenType.IDENTIFIER);
        keywords.put("STRUCT", TokenType.IDENTIFIER);
        keywords.put("ARRAY", TokenType.IDENTIFIER);
        keywords.put("MAP", TokenType.IDENTIFIER);
        keywords.put("UUID", TokenType.IDENTIFIER);
        keywords.put("IPADDRESS", TokenType.IDENTIFIER);
        keywords.put("IPPREFIX", TokenType.IDENTIFIER);
        keywords.put("JSON", TokenType.IDENTIFIER);
        keywords.put("BOOLEAN", TokenType.IDENTIFIER);
        keywords.put("TINYINT", TokenType.IDENTIFIER);
        keywords.put("SMALLINT", TokenType.IDENTIFIER);
        keywords.put("INTEGER", TokenType.IDENTIFIER);
        keywords.put("BIGINT", TokenType.IDENTIFIER);
        keywords.put("FLOAT", TokenType.IDENTIFIER);
        keywords.put("DOUBLE", TokenType.IDENTIFIER);
        keywords.put("DECIMAL", TokenType.IDENTIFIER);
        keywords.put("VARCHAR", TokenType.IDENTIFIER);
        keywords.put("CHAR", TokenType.IDENTIFIER);
        keywords.put("STRING", TokenType.IDENTIFIER);
        keywords.put("VARBINARY", TokenType.IDENTIFIER);
        keywords.put("DATE", TokenType.IDENTIFIER);
        keywords.put("TIME", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMPTZ", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMP_WITH_TIME_ZONE", TokenType.IDENTIFIER);

        // Approximate functions
        keywords.put("APPROX_DISTINCT", TokenType.IDENTIFIER);
        keywords.put("APPROX_PERCENTILE", TokenType.IDENTIFIER);
        keywords.put("APPROX_SET", TokenType.IDENTIFIER);

        // Array functions
        keywords.put("ARRAY_AGG", TokenType.IDENTIFIER);
        keywords.put("ARRAY_DISTINCT", TokenType.IDENTIFIER);
        keywords.put("ARRAY_FILTER", TokenType.IDENTIFIER);
        keywords.put("ARRAY_FLATTEN", TokenType.IDENTIFIER);
        keywords.put("ARRAY_JOIN", TokenType.IDENTIFIER);
        keywords.put("ARRAY_LENGTH", TokenType.IDENTIFIER);
        keywords.put("ARRAY_MAX", TokenType.IDENTIFIER);
        keywords.put("ARRAY_MIN", TokenType.IDENTIFIER);
        keywords.put("ARRAY_POSITION", TokenType.IDENTIFIER);
        keywords.put("ARRAY_REMOVE", TokenType.IDENTIFIER);
        keywords.put("ARRAY_REVERSE", TokenType.IDENTIFIER);
        keywords.put("ARRAY_SORT", TokenType.IDENTIFIER);
        keywords.put("ARRAY_SLICE", TokenType.IDENTIFIER);
        keywords.put("ARRAY_UNION", TokenType.IDENTIFIER);
        keywords.put("ARRAYS_OVERLAP", TokenType.IDENTIFIER);
        keywords.put("CARDINALITY", TokenType.IDENTIFIER);
        keywords.put("ELEMENT_AT", TokenType.IDENTIFIER);
        keywords.put("FLATTEN", TokenType.IDENTIFIER);
        keywords.put("SLICE", TokenType.IDENTIFIER);

        // Map functions
        keywords.put("MAP_CONCAT", TokenType.IDENTIFIER);
        keywords.put("MAP_FILTER", TokenType.IDENTIFIER);
        keywords.put("MAP_KEYS", TokenType.IDENTIFIER);
        keywords.put("MAP_VALUES", TokenType.IDENTIFIER);
        keywords.put("MAP_ENTRIES", TokenType.IDENTIFIER);
        keywords.put("MULTIMAP_FROM_ENTRIES", TokenType.IDENTIFIER);
        keywords.put("TRANSFORM_KEYS", TokenType.IDENTIFIER);
        keywords.put("TRANSFORM_VALUES", TokenType.IDENTIFIER);

        // Row/Struct functions
        keywords.put("ROW_CONSTRUCTOR", TokenType.IDENTIFIER);
        keywords.put("FIELD", TokenType.IDENTIFIER);
        keywords.put("NAMED_STRUCT", TokenType.IDENTIFIER);

        // JSON functions
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

        // UUID functions
        keywords.put("UUID_NIL", TokenType.IDENTIFIER);
        keywords.put("UUID_V4", TokenType.IDENTIFIER);

        // IP address functions
        keywords.put("IP_FROM_STRING", TokenType.IDENTIFIER);
        keywords.put("IP_FROM_BYTES", TokenType.IDENTIFIER);
        keywords.put("IP_TO_STRING", TokenType.IDENTIFIER);
        keywords.put("IP_TO_BYTES", TokenType.IDENTIFIER);
        keywords.put("IS_IPADDRESS", TokenType.IDENTIFIER);
        keywords.put("IS_IPPREFIX", TokenType.IDENTIFIER);
        keywords.put("IPPREFIX_MATCH", TokenType.IDENTIFIER);
        keywords.put("IPADDRESS_NETMASK", TokenType.IDENTIFIER);

        // String functions
        keywords.put("LENGTH", TokenType.IDENTIFIER);
        keywords.put("SUBSTR", TokenType.IDENTIFIER);
        keywords.put("SUBSTRING", TokenType.IDENTIFIER);
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
        keywords.put("LTRIM", TokenType.IDENTIFIER);
        keywords.put("RTRIM", TokenType.IDENTIFIER);
        keywords.put("TRIM", TokenType.IDENTIFIER);
        keywords.put("REGEXP_EXTRACT_ALL", TokenType.IDENTIFIER);
        keywords.put("REGEXP_LIKE", TokenType.IDENTIFIER);
        keywords.put("REGEXP_REPLACE", TokenType.IDENTIFIER);
        keywords.put("REGEXP_SPLIT", TokenType.IDENTIFIER);

        // Date/Time functions
        keywords.put("FROM_UNIXTIME", TokenType.IDENTIFIER);
        keywords.put("TO_UNIXTIME", TokenType.IDENTIFIER);
        keywords.put("FROM_ISO8601_TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("TO_ISO8601", TokenType.IDENTIFIER);
        keywords.put("FROM_ISO8601_DATE", TokenType.IDENTIFIER);
        keywords.put("FROM_ISO8601_TIME", TokenType.IDENTIFIER);
        keywords.put("FROM_RFC2822_TIMESTAMP", TokenType.IDENTIFIER);
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

        // Aggregation functions
        keywords.put("COUNT", TokenType.IDENTIFIER);
        keywords.put("SUM", TokenType.IDENTIFIER);
        keywords.put("AVG", TokenType.IDENTIFIER);
        keywords.put("MIN", TokenType.IDENTIFIER);
        keywords.put("MAX", TokenType.IDENTIFIER);
        keywords.put("STDDEV", TokenType.IDENTIFIER);
        keywords.put("STDDEV_POP", TokenType.IDENTIFIER);
        keywords.put("STDDEV_SAMP", TokenType.IDENTIFIER);
        keywords.put("VARIANCE", TokenType.IDENTIFIER);
        keywords.put("VAR_POP", TokenType.IDENTIFIER);
        keywords.put("VAR_SAMP", TokenType.IDENTIFIER);
        keywords.put("COVARIANCE_POP", TokenType.IDENTIFIER);
        keywords.put("COVARIANCE_SAMP", TokenType.IDENTIFIER);
        keywords.put("CORR", TokenType.IDENTIFIER);
        keywords.put("REGR_SLOPE", TokenType.IDENTIFIER);
        keywords.put("REGR_INTERCEPT", TokenType.IDENTIFIER);

        // Grouping functions
        keywords.put("GROUPING", TokenType.IDENTIFIER);
        keywords.put("GROUPING_SETS", TokenType.IDENTIFIER);
        keywords.put("CUBE", TokenType.IDENTIFIER);
        keywords.put("ROLLUP", TokenType.IDENTIFIER);
        keywords.put("GROUP", TokenType.IDENTIFIER);
        keywords.put("BY", TokenType.IDENTIFIER);
        keywords.put("HAVING", TokenType.IDENTIFIER);

        // Window functions
        keywords.put("ROW_NUMBER", TokenType.IDENTIFIER);
        keywords.put("RANK", TokenType.IDENTIFIER);
        keywords.put("DENSE_RANK", TokenType.IDENTIFIER);
        keywords.put("PERCENT_RANK", TokenType.IDENTIFIER);
        keywords.put("CUME_DIST", TokenType.IDENTIFIER);
        keywords.put("NTILE", TokenType.IDENTIFIER);
        keywords.put("LAG", TokenType.IDENTIFIER);
        keywords.put("LEAD", TokenType.IDENTIFIER);
        keywords.put("FIRST_VALUE", TokenType.IDENTIFIER);
        keywords.put("LAST_VALUE", TokenType.IDENTIFIER);
        keywords.put("NTH_VALUE", TokenType.IDENTIFIER);
        keywords.put("OVER", TokenType.IDENTIFIER);
        keywords.put("PARTITION", TokenType.IDENTIFIER);
        keywords.put("ORDER", TokenType.IDENTIFIER);
        keywords.put("ROWS", TokenType.IDENTIFIER);
        keywords.put("RANGE", TokenType.IDENTIFIER);
        keywords.put("UNBOUNDED", TokenType.IDENTIFIER);
        keywords.put("PRECEDING", TokenType.IDENTIFIER);
        keywords.put("FOLLOWING", TokenType.IDENTIFIER);
        keywords.put("CURRENT", TokenType.IDENTIFIER);
        keywords.put("ROW", TokenType.IDENTIFIER);

        // Math functions
        keywords.put("ABS", TokenType.IDENTIFIER);
        keywords.put("CEIL", TokenType.IDENTIFIER);
        keywords.put("CEILING", TokenType.IDENTIFIER);
        keywords.put("FLOOR", TokenType.IDENTIFIER);
        keywords.put("ROUND", TokenType.IDENTIFIER);
        keywords.put("SQRT", TokenType.IDENTIFIER);
        keywords.put("POWER", TokenType.IDENTIFIER);
        keywords.put("EXP", TokenType.IDENTIFIER);
        keywords.put("LN", TokenType.IDENTIFIER);
        keywords.put("LOG", TokenType.IDENTIFIER);
        keywords.put("LOG2", TokenType.IDENTIFIER);
        keywords.put("LOG10", TokenType.IDENTIFIER);
        keywords.put("PI", TokenType.IDENTIFIER);
        keywords.put("E", TokenType.IDENTIFIER);
        keywords.put("SIN", TokenType.IDENTIFIER);
        keywords.put("COS", TokenType.IDENTIFIER);
        keywords.put("TAN", TokenType.IDENTIFIER);
        keywords.put("ASIN", TokenType.IDENTIFIER);
        keywords.put("ACOS", TokenType.IDENTIFIER);
        keywords.put("ATAN", TokenType.IDENTIFIER);
        keywords.put("ATAN2", TokenType.IDENTIFIER);
        keywords.put("SINH", TokenType.IDENTIFIER);
        keywords.put("COSH", TokenType.IDENTIFIER);
        keywords.put("TANH", TokenType.IDENTIFIER);
        keywords.put("DEGREES", TokenType.IDENTIFIER);
        keywords.put("RADIANS", TokenType.IDENTIFIER);
        keywords.put("GREATEST", TokenType.IDENTIFIER);
        keywords.put("LEAST", TokenType.IDENTIFIER);
        keywords.put("SIGN", TokenType.IDENTIFIER);
        keywords.put("TRUNCATE", TokenType.IDENTIFIER);
        keywords.put("RANDOM", TokenType.IDENTIFIER);

        // Higher-order functions / Lambda
        keywords.put("TRANSFORM", TokenType.IDENTIFIER);
        keywords.put("FILTER", TokenType.IDENTIFIER);
        keywords.put("REDUCE", TokenType.IDENTIFIER);
        keywords.put("FOLD", TokenType.IDENTIFIER);
        keywords.put("ZIP_WITH", TokenType.IDENTIFIER);
        keywords.put("MAP_FROM_ENTRIES", TokenType.IDENTIFIER);

        // Type casting
        keywords.put("CAST", TokenType.IDENTIFIER);
        keywords.put("TRY_CAST", TokenType.IDENTIFIER);

        // Other functions
        keywords.put("COALESCE", TokenType.IDENTIFIER);
        keywords.put("NULLIF", TokenType.IDENTIFIER);
        keywords.put("IFNULL", TokenType.IDENTIFIER);
        keywords.put("IF", TokenType.IDENTIFIER);
        keywords.put("CASE", TokenType.IDENTIFIER);
        keywords.put("WHEN", TokenType.IDENTIFIER);
        keywords.put("THEN", TokenType.IDENTIFIER);
        keywords.put("ELSE", TokenType.IDENTIFIER);
        keywords.put("END", TokenType.IDENTIFIER);

        // Catalog/Schema keywords
        keywords.put("CATALOG", TokenType.IDENTIFIER);
        keywords.put("SCHEMA", TokenType.IDENTIFIER);
        keywords.put("TABLE", TokenType.IDENTIFIER);
        keywords.put("VIEW", TokenType.IDENTIFIER);
        keywords.put("MATERIALIZED", TokenType.IDENTIFIER);
        keywords.put("DATABASE", TokenType.IDENTIFIER);

        // CTE keywords
        keywords.put("WITH", TokenType.IDENTIFIER);
        keywords.put("RECURSIVE", TokenType.IDENTIFIER);

        return Trie.build(keywords);
    }
}
