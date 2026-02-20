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
package com.gtkcyber.sqlglot.dialects.athena;

import com.gtkcyber.sqlglot.tokens.Tokenizer;
import com.gtkcyber.sqlglot.tokens.TokenType;
import com.gtkcyber.sqlglot.tokens.Trie;

import java.util.HashMap;
import java.util.Map;

/**
 * AWS Athena SQL tokenizer.
 * Supports Athena-specific syntax:
 * - Double quote identifiers (Presto-based)
 * - Backtick identifiers as alternative (Hive compatibility)
 * - Single quote string literals with doubled quote escaping
 * - S3 and Glue Catalog keywords
 * - File format keywords (PARQUET, ORC, CSV, JSON)
 * - Athena-specific keywords and functions
 * - Complex data type keywords
 * - Window function keywords
 * - Lambda function syntax
 */
public class AthenaTokenizer extends Tokenizer {
    @Override
    protected Map<String, Character> getIdentifierQuotes() {
        Map<String, Character> quotes = new HashMap<>();
        quotes.put("\"", '"');   // Double quotes (primary - Presto-compatible)
        quotes.put("`", '`');    // Backticks (secondary - Hive compatibility)
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

        // Athena-specific S3 and table keywords
        keywords.put("LOCATION", TokenType.IDENTIFIER);
        keywords.put("EXTERNAL", TokenType.IDENTIFIER);
        keywords.put("STORED", TokenType.IDENTIFIER);
        keywords.put("AS", TokenType.IDENTIFIER);
        keywords.put("ROW", TokenType.IDENTIFIER);
        keywords.put("FORMAT", TokenType.IDENTIFIER);
        keywords.put("SERDEPROPERTIES", TokenType.IDENTIFIER);
        keywords.put("TBLPROPERTIES", TokenType.IDENTIFIER);
        keywords.put("PROPERTIES", TokenType.IDENTIFIER);
        keywords.put("WITH", TokenType.IDENTIFIER);
        keywords.put("PARTITION", TokenType.IDENTIFIER);
        keywords.put("PARTITIONED", TokenType.IDENTIFIER);
        keywords.put("CLUSTERED", TokenType.IDENTIFIER);
        keywords.put("INTO", TokenType.IDENTIFIER);
        keywords.put("BUCKETS", TokenType.IDENTIFIER);
        keywords.put("SORTED", TokenType.IDENTIFIER);
        keywords.put("BY", TokenType.IDENTIFIER);
        keywords.put("BUCKET", TokenType.IDENTIFIER);
        keywords.put("SKEWED", TokenType.IDENTIFIER);
        keywords.put("ON", TokenType.IDENTIFIER);

        // File format keywords
        keywords.put("PARQUET", TokenType.IDENTIFIER);
        keywords.put("ORC", TokenType.IDENTIFIER);
        keywords.put("CSV", TokenType.IDENTIFIER);
        keywords.put("JSON", TokenType.IDENTIFIER);
        keywords.put("JSONFILE", TokenType.IDENTIFIER);
        keywords.put("TEXTFILE", TokenType.IDENTIFIER);
        keywords.put("AVRO", TokenType.IDENTIFIER);
        keywords.put("SEQUENCEFILE", TokenType.IDENTIFIER);
        keywords.put("RCFILE", TokenType.IDENTIFIER);
        keywords.put("ICEBERG", TokenType.IDENTIFIER);

        // Row format and SerDe keywords
        keywords.put("DELIMITED", TokenType.IDENTIFIER);
        keywords.put("FIELDS", TokenType.IDENTIFIER);
        keywords.put("TERMINATED", TokenType.IDENTIFIER);
        keywords.put("ESCAPED", TokenType.IDENTIFIER);
        keywords.put("COLLECTION", TokenType.IDENTIFIER);
        keywords.put("ITEMS", TokenType.IDENTIFIER);
        keywords.put("KEYS", TokenType.IDENTIFIER);
        keywords.put("LINES", TokenType.IDENTIFIER);
        keywords.put("INPUTFORMAT", TokenType.IDENTIFIER);
        keywords.put("OUTPUTFORMAT", TokenType.IDENTIFIER);
        keywords.put("SERDE", TokenType.IDENTIFIER);

        // Complex data types
        keywords.put("STRUCT", TokenType.IDENTIFIER);
        keywords.put("ARRAY", TokenType.IDENTIFIER);
        keywords.put("MAP", TokenType.IDENTIFIER);
        keywords.put("UNIONTYPE", TokenType.IDENTIFIER);

        // Athena data types
        keywords.put("BYTE", TokenType.IDENTIFIER);
        keywords.put("TINYINT", TokenType.IDENTIFIER);
        keywords.put("SMALLINT", TokenType.IDENTIFIER);
        keywords.put("INT", TokenType.IDENTIFIER);
        keywords.put("INTEGER", TokenType.IDENTIFIER);
        keywords.put("LONG", TokenType.IDENTIFIER);
        keywords.put("BIGINT", TokenType.IDENTIFIER);
        keywords.put("FLOAT", TokenType.IDENTIFIER);
        keywords.put("DOUBLE", TokenType.IDENTIFIER);
        keywords.put("DECIMAL", TokenType.IDENTIFIER);
        keywords.put("NUMERIC", TokenType.IDENTIFIER);
        keywords.put("VARCHAR", TokenType.IDENTIFIER);
        keywords.put("CHAR", TokenType.IDENTIFIER);
        keywords.put("STRING", TokenType.IDENTIFIER);
        keywords.put("TEXT", TokenType.IDENTIFIER);
        keywords.put("BOOLEAN", TokenType.IDENTIFIER);
        keywords.put("BOOL", TokenType.IDENTIFIER);
        keywords.put("BINARY", TokenType.IDENTIFIER);
        keywords.put("VARBINARY", TokenType.IDENTIFIER);
        keywords.put("BLOB", TokenType.IDENTIFIER);
        keywords.put("DATE", TokenType.IDENTIFIER);
        keywords.put("TIME", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMPTZ", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMP_WITH_TIME_ZONE", TokenType.IDENTIFIER);
        keywords.put("INTERVAL", TokenType.IDENTIFIER);
        keywords.put("YEAR", TokenType.IDENTIFIER);
        keywords.put("MONTH", TokenType.IDENTIFIER);
        keywords.put("DAY", TokenType.IDENTIFIER);
        keywords.put("HOUR", TokenType.IDENTIFIER);
        keywords.put("MINUTE", TokenType.IDENTIFIER);
        keywords.put("SECOND", TokenType.IDENTIFIER);
        keywords.put("MILLISECOND", TokenType.IDENTIFIER);
        keywords.put("MICROSECOND", TokenType.IDENTIFIER);

        // Window function and analytic keywords
        keywords.put("WINDOW", TokenType.IDENTIFIER);
        keywords.put("OVER", TokenType.IDENTIFIER);
        keywords.put("ROWS", TokenType.IDENTIFIER);
        keywords.put("RANGE", TokenType.IDENTIFIER);
        keywords.put("GROUPS", TokenType.IDENTIFIER);
        keywords.put("UNBOUNDED", TokenType.IDENTIFIER);
        keywords.put("PRECEDING", TokenType.IDENTIFIER);
        keywords.put("FOLLOWING", TokenType.IDENTIFIER);
        keywords.put("EXCLUDE", TokenType.IDENTIFIER);
        keywords.put("NO", TokenType.IDENTIFIER);
        keywords.put("TIES", TokenType.IDENTIFIER);
        keywords.put("PEERS", TokenType.IDENTIFIER);

        // Lambda function keywords
        keywords.put("LAMBDA", TokenType.IDENTIFIER);

        // Array and map operation keywords
        keywords.put("ELEMENT_AT", TokenType.IDENTIFIER);
        keywords.put("ARRAY_AGG", TokenType.IDENTIFIER);
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
        keywords.put("MAP_CONCAT", TokenType.IDENTIFIER);
        keywords.put("MAP_FILTER", TokenType.IDENTIFIER);
        keywords.put("MAP_KEYS", TokenType.IDENTIFIER);
        keywords.put("MAP_VALUES", TokenType.IDENTIFIER);
        keywords.put("TRANSFORM_KEYS", TokenType.IDENTIFIER);
        keywords.put("TRANSFORM_VALUES", TokenType.IDENTIFIER);

        // JSON operation keywords
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

        // String operation keywords
        keywords.put("SUBSTR", TokenType.IDENTIFIER);
        keywords.put("SUBSTRING", TokenType.IDENTIFIER);
        keywords.put("LTRIM", TokenType.IDENTIFIER);
        keywords.put("RTRIM", TokenType.IDENTIFIER);
        keywords.put("INITCAP", TokenType.IDENTIFIER);
        keywords.put("LPAD", TokenType.IDENTIFIER);
        keywords.put("RPAD", TokenType.IDENTIFIER);
        keywords.put("REVERSE", TokenType.IDENTIFIER);
        keywords.put("REPEAT", TokenType.IDENTIFIER);
        keywords.put("STRPOS", TokenType.IDENTIFIER);
        keywords.put("INSTR", TokenType.IDENTIFIER);
        keywords.put("REGEXP_EXTRACT", TokenType.IDENTIFIER);
        keywords.put("REGEXP_EXTRACT_ALL", TokenType.IDENTIFIER);
        keywords.put("REGEXP_LIKE", TokenType.IDENTIFIER);
        keywords.put("REGEXP_REPLACE", TokenType.IDENTIFIER);
        keywords.put("REGEXP_SPLIT", TokenType.IDENTIFIER);

        // Date/time function keywords
        keywords.put("FROM_UNIXTIME", TokenType.IDENTIFIER);
        keywords.put("TO_UNIXTIME", TokenType.IDENTIFIER);
        keywords.put("FROM_ISO8601_TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("TO_ISO8601", TokenType.IDENTIFIER);
        keywords.put("DATE_FORMAT", TokenType.IDENTIFIER);
        keywords.put("DATE_PARSE", TokenType.IDENTIFIER);
        keywords.put("DATE_DIFF", TokenType.IDENTIFIER);
        keywords.put("DATE_ADD", TokenType.IDENTIFIER);
        keywords.put("DATE_SUBTRACT", TokenType.IDENTIFIER);
        keywords.put("DATE_TRUNC", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMP_DIFF", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMP_ADD", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMP_SUBTRACT", TokenType.IDENTIFIER);
        keywords.put("AT_TIMEZONE", TokenType.IDENTIFIER);
        keywords.put("TIMEZONE_HOUR", TokenType.IDENTIFIER);
        keywords.put("TIMEZONE_MINUTE", TokenType.IDENTIFIER);

        // Casting and type functions
        keywords.put("TRY_CAST", TokenType.IDENTIFIER);
        keywords.put("TYPEOF", TokenType.IDENTIFIER);

        // Conditional and null handling functions
        keywords.put("NULLIF", TokenType.IDENTIFIER);
        keywords.put("IFNULL", TokenType.IDENTIFIER);

        // Aggregate function keywords
        keywords.put("APPROX_DISTINCT", TokenType.IDENTIFIER);
        keywords.put("APPROX_PERCENTILE", TokenType.IDENTIFIER);
        keywords.put("APPROX_SET", TokenType.IDENTIFIER);
        keywords.put("STDDEV", TokenType.IDENTIFIER);
        keywords.put("STDDEV_POP", TokenType.IDENTIFIER);
        keywords.put("STDDEV_SAMP", TokenType.IDENTIFIER);
        keywords.put("VARIANCE", TokenType.IDENTIFIER);
        keywords.put("VAR_POP", TokenType.IDENTIFIER);
        keywords.put("VAR_SAMP", TokenType.IDENTIFIER);

        // Glue Catalog metadata keywords
        keywords.put("CATALOG", TokenType.IDENTIFIER);

        // Math function keywords
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

        return Trie.build(keywords);
    }
}
