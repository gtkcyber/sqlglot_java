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
package com.gtkcyber.sqlglot.dialects.clickhouse;

import com.gtkcyber.sqlglot.tokens.Tokenizer;
import com.gtkcyber.sqlglot.tokens.TokenType;
import com.gtkcyber.sqlglot.tokens.Trie;

import java.util.HashMap;
import java.util.Map;

/**
 * ClickHouse tokenizer.
 * Supports ClickHouse-specific syntax:
 * - Backtick identifiers
 * - Double quote identifiers
 * - Single quote strings
 * - ClickHouse-specific keywords and functions
 * - MergeTree engine keywords
 */
public class ClickhouseTokenizer extends Tokenizer {
    @Override
    protected Map<String, Character> getIdentifierQuotes() {
        Map<String, Character> quotes = new HashMap<>();
        quotes.put("`", '`');    // Backticks (primary)
        quotes.put("\"", '"');   // Double quotes (secondary)
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

        // ClickHouse-specific keywords
        keywords.put("ENGINE", TokenType.IDENTIFIER);
        keywords.put("MERGETREE", TokenType.IDENTIFIER);
        keywords.put("REPLACINGMERGETREE", TokenType.IDENTIFIER);
        keywords.put("SUMMINGMERGETREE", TokenType.IDENTIFIER);
        keywords.put("AGGREGATINGMERGETREE", TokenType.IDENTIFIER);
        keywords.put("COLLAPSINGMERGETREE", TokenType.IDENTIFIER);
        keywords.put("VERSIONEDCOLLAPSINGMERGETREE", TokenType.IDENTIFIER);
        keywords.put("GRAPHITEMERGETREE", TokenType.IDENTIFIER);
        keywords.put("LOG", TokenType.IDENTIFIER);
        keywords.put("TINYLOG", TokenType.IDENTIFIER);
        keywords.put("STRIPELOG", TokenType.IDENTIFIER);
        keywords.put("MEMORY", TokenType.IDENTIFIER);
        keywords.put("TEMPORARY", TokenType.IDENTIFIER);
        keywords.put("MATERIALIZED", TokenType.IDENTIFIER);
        keywords.put("VIEW", TokenType.IDENTIFIER);
        keywords.put("LIVE", TokenType.IDENTIFIER);
        keywords.put("DICTIONARY", TokenType.IDENTIFIER);
        keywords.put("DISTRIBUTED", TokenType.IDENTIFIER);
        keywords.put("KAFKA", TokenType.IDENTIFIER);
        keywords.put("MYSQL", TokenType.IDENTIFIER);
        keywords.put("JDBC", TokenType.IDENTIFIER);
        keywords.put("ODBC", TokenType.IDENTIFIER);
        keywords.put("HIVE", TokenType.IDENTIFIER);
        keywords.put("URL", TokenType.IDENTIFIER);
        keywords.put("S3", TokenType.IDENTIFIER);
        keywords.put("HDFS", TokenType.IDENTIFIER);
        keywords.put("CLUSTER", TokenType.IDENTIFIER);
        keywords.put("REPLICA", TokenType.IDENTIFIER);
        keywords.put("SHARD", TokenType.IDENTIFIER);
        keywords.put("WEIGHT", TokenType.IDENTIFIER);
        keywords.put("PRIORITY", TokenType.IDENTIFIER);
        keywords.put("PRIMARY", TokenType.IDENTIFIER);
        keywords.put("KEY", TokenType.IDENTIFIER);
        keywords.put("ORDER", TokenType.IDENTIFIER);
        keywords.put("BY", TokenType.IDENTIFIER);
        keywords.put("PARTITION", TokenType.IDENTIFIER);
        keywords.put("SAMPLE", TokenType.IDENTIFIER);
        keywords.put("SETTINGS", TokenType.IDENTIFIER);
        keywords.put("CODEC", TokenType.IDENTIFIER);
        keywords.put("LZ4", TokenType.IDENTIFIER);
        keywords.put("LZ4HC", TokenType.IDENTIFIER);
        keywords.put("ZSTD", TokenType.IDENTIFIER);
        keywords.put("DEFLATE", TokenType.IDENTIFIER);
        keywords.put("DELTA", TokenType.IDENTIFIER);
        keywords.put("ROWBINARY", TokenType.IDENTIFIER);
        keywords.put("EXPRESSION", TokenType.IDENTIFIER);
        keywords.put("DICTIONARY", TokenType.IDENTIFIER);
        keywords.put("RANGE", TokenType.IDENTIFIER);
        keywords.put("FLAT", TokenType.IDENTIFIER);
        keywords.put("HASHED", TokenType.IDENTIFIER);
        keywords.put("SPARSE_HASHED", TokenType.IDENTIFIER);
        keywords.put("CACHE", TokenType.IDENTIFIER);
        keywords.put("COMPLEX_KEY_HASHED", TokenType.IDENTIFIER);
        keywords.put("COMPLEX_KEY_SPARSE_HASHED", TokenType.IDENTIFIER);
        keywords.put("COMPLEX_KEY_CACHE", TokenType.IDENTIFIER);
        keywords.put("IPV4", TokenType.IDENTIFIER);
        keywords.put("IPV6", TokenType.IDENTIFIER);
        keywords.put("PREALLOCATE", TokenType.IDENTIFIER);
        keywords.put("CUSTOM_SEPARATOR", TokenType.IDENTIFIER);
        keywords.put("SKIP_UNKNOWN_KEYS", TokenType.IDENTIFIER);
        keywords.put("WITH", TokenType.IDENTIFIER);
        keywords.put("PARSER", TokenType.IDENTIFIER);

        // ClickHouse aggregate functions
        keywords.put("COUNT", TokenType.IDENTIFIER);
        keywords.put("SUM", TokenType.IDENTIFIER);
        keywords.put("AVG", TokenType.IDENTIFIER);
        keywords.put("MIN", TokenType.IDENTIFIER);
        keywords.put("MAX", TokenType.IDENTIFIER);
        keywords.put("SUMIF", TokenType.IDENTIFIER);
        keywords.put("COUNTIF", TokenType.IDENTIFIER);
        keywords.put("AVGIF", TokenType.IDENTIFIER);
        keywords.put("MINIF", TokenType.IDENTIFIER);
        keywords.put("MAXIF", TokenType.IDENTIFIER);
        keywords.put("SUMARRAY", TokenType.IDENTIFIER);
        keywords.put("COUNTARRAY", TokenType.IDENTIFIER);
        keywords.put("AVGARRAY", TokenType.IDENTIFIER);
        keywords.put("MINARRAY", TokenType.IDENTIFIER);
        keywords.put("MAXARRAY", TokenType.IDENTIFIER);
        keywords.put("STDDEV", TokenType.IDENTIFIER);
        keywords.put("VARIANCE", TokenType.IDENTIFIER);
        keywords.put("QUANTILE", TokenType.IDENTIFIER);
        keywords.put("QUANTILES", TokenType.IDENTIFIER);
        keywords.put("MEDIAN", TokenType.IDENTIFIER);
        keywords.put("PERCENTILE", TokenType.IDENTIFIER);
        keywords.put("PERCENTILES", TokenType.IDENTIFIER);
        keywords.put("GROUP_CONCAT", TokenType.IDENTIFIER);
        keywords.put("GROUPARRAY", TokenType.IDENTIFIER);
        keywords.put("GROUPARRAYINSERTAFTER", TokenType.IDENTIFIER);
        keywords.put("GROUPARRAYUNIQUEINSERTAFTER", TokenType.IDENTIFIER);
        keywords.put("GROUPUNIQARRAY", TokenType.IDENTIFIER);

        // ClickHouse string functions
        keywords.put("LENGTH", TokenType.IDENTIFIER);
        keywords.put("LENGTHUTF8", TokenType.IDENTIFIER);
        keywords.put("SUBSTR", TokenType.IDENTIFIER);
        keywords.put("SUBSTRING", TokenType.IDENTIFIER);
        keywords.put("SUBSTRINGUTF8", TokenType.IDENTIFIER);
        keywords.put("UPPER", TokenType.IDENTIFIER);
        keywords.put("LOWER", TokenType.IDENTIFIER);
        keywords.put("CONCAT", TokenType.IDENTIFIER);
        keywords.put("CONCAT_WS", TokenType.IDENTIFIER);
        keywords.put("REPLACE", TokenType.IDENTIFIER);
        keywords.put("REPLACEALL", TokenType.IDENTIFIER);
        keywords.put("POSITIONUTF8", TokenType.IDENTIFIER);
        keywords.put("POSITION", TokenType.IDENTIFIER);
        keywords.put("TRIM", TokenType.IDENTIFIER);
        keywords.put("LTRIM", TokenType.IDENTIFIER);
        keywords.put("RTRIM", TokenType.IDENTIFIER);
        keywords.put("SPLIT", TokenType.IDENTIFIER);
        keywords.put("SPLITBYSTRING", TokenType.IDENTIFIER);
        keywords.put("SPLITBYCHAR", TokenType.IDENTIFIER);
        keywords.put("ARRAYMAP", TokenType.IDENTIFIER);
        keywords.put("ARRAYFILTER", TokenType.IDENTIFIER);
        keywords.put("ARRAYCONCAT", TokenType.IDENTIFIER);
        keywords.put("ARRAYCOUNT", TokenType.IDENTIFIER);
        keywords.put("ARRAYSUM", TokenType.IDENTIFIER);
        keywords.put("ARRAYAVG", TokenType.IDENTIFIER);
        keywords.put("ARRAYMIN", TokenType.IDENTIFIER);
        keywords.put("ARRAYMAX", TokenType.IDENTIFIER);
        keywords.put("ARRAYPRODUCT", TokenType.IDENTIFIER);
        keywords.put("ARRAYREVERSE", TokenType.IDENTIFIER);
        keywords.put("ARRAYUNIQ", TokenType.IDENTIFIER);
        keywords.put("ARRAYUNIQSORTED", TokenType.IDENTIFIER);
        keywords.put("ARRAYLENGTH", TokenType.IDENTIFIER);
        keywords.put("ARRAYELEMENT", TokenType.IDENTIFIER);
        keywords.put("ARRAYINDEX", TokenType.IDENTIFIER);
        keywords.put("ARRAYSLICE", TokenType.IDENTIFIER);
        keywords.put("ARRAYCONTAINS", TokenType.IDENTIFIER);
        keywords.put("ARRAYPUSH", TokenType.IDENTIFIER);
        keywords.put("ARRAYPUSHFRONT", TokenType.IDENTIFIER);
        keywords.put("ARRAYINSERTAFTER", TokenType.IDENTIFIER);
        keywords.put("ARRAYUNIQUEINSERTAFTER", TokenType.IDENTIFIER);
        keywords.put("ARRAYPOPBACK", TokenType.IDENTIFIER);
        keywords.put("ARRAYPOPFRONT", TokenType.IDENTIFIER);
        keywords.put("ARRAYCOMPACT", TokenType.IDENTIFIER);
        keywords.put("ARRAYINTERSECT", TokenType.IDENTIFIER);
        keywords.put("ARRAYHASALL", TokenType.IDENTIFIER);
        keywords.put("ARRAYHASANY", TokenType.IDENTIFIER);
        keywords.put("ARRAYHASSUBSET", TokenType.IDENTIFIER);
        keywords.put("ARRAYUNIQSORTED", TokenType.IDENTIFIER);
        keywords.put("ARRAYUNIQ", TokenType.IDENTIFIER);
        keywords.put("ARRAYUNIQUNSORTED", TokenType.IDENTIFIER);

        // ClickHouse math functions
        keywords.put("ABS", TokenType.IDENTIFIER);
        keywords.put("ROUND", TokenType.IDENTIFIER);
        keywords.put("CEIL", TokenType.IDENTIFIER);
        keywords.put("FLOOR", TokenType.IDENTIFIER);
        keywords.put("SQRT", TokenType.IDENTIFIER);
        keywords.put("POWER", TokenType.IDENTIFIER);
        keywords.put("EXP", TokenType.IDENTIFIER);
        keywords.put("LN", TokenType.IDENTIFIER);
        keywords.put("LOG", TokenType.IDENTIFIER);
        keywords.put("LOG2", TokenType.IDENTIFIER);
        keywords.put("LOG10", TokenType.IDENTIFIER);
        keywords.put("PI", TokenType.IDENTIFIER);
        keywords.put("E", TokenType.IDENTIFIER);

        // ClickHouse date functions
        keywords.put("NOW", TokenType.IDENTIFIER);
        keywords.put("TODAY", TokenType.IDENTIFIER);
        keywords.put("YESTERDAY", TokenType.IDENTIFIER);
        keywords.put("TOMORROW", TokenType.IDENTIFIER);
        keywords.put("TODATE", TokenType.IDENTIFIER);
        keywords.put("TODATETIME", TokenType.IDENTIFIER);
        keywords.put("TOSTRING", TokenType.IDENTIFIER);
        keywords.put("FORMATDATETIME", TokenType.IDENTIFIER);
        keywords.put("PARSEDATETIME", TokenType.IDENTIFIER);

        return Trie.build(keywords);
    }
}
