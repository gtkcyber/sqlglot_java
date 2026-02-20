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
package com.gtkcyber.sqlglot.dialects.starrocks;

import com.gtkcyber.sqlglot.tokens.Tokenizer;
import com.gtkcyber.sqlglot.tokens.TokenType;
import com.gtkcyber.sqlglot.tokens.Trie;

import java.util.HashMap;
import java.util.Map;

/**
 * StarRocks tokenizer.
 * Supports StarRocks-specific syntax:
 * - Backtick identifiers
 * - Double quote identifiers (alternative)
 * - Single quote strings with doubled quote escaping
 * - Bitmap index and HyperLogLog keywords
 * - Table model keywords (DUPLICATE, PRIMARY, UNIQUE, AGGREGATE)
 * - Partition and bucket specifications
 * - Complex data types (ARRAY, STRUCT, MAP)
 * - Colocate and replication keywords
 */
public class StarrocksTokenizer extends Tokenizer {
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

        // Bitmap and HyperLogLog
        keywords.put("BITMAP", TokenType.IDENTIFIER);
        keywords.put("BITMAP_UNION", TokenType.IDENTIFIER);
        keywords.put("BITMAP_AND", TokenType.IDENTIFIER);
        keywords.put("BITMAP_OR", TokenType.IDENTIFIER);
        keywords.put("BITMAP_XOR", TokenType.IDENTIFIER);
        keywords.put("BITMAP_COUNT", TokenType.IDENTIFIER);
        keywords.put("BITMAP_HASH", TokenType.IDENTIFIER);
        keywords.put("BITMAP_FROM_STRING", TokenType.IDENTIFIER);
        keywords.put("BITMAP_TO_STRING", TokenType.IDENTIFIER);
        keywords.put("HYPERLOGLOG", TokenType.IDENTIFIER);
        keywords.put("HLL", TokenType.IDENTIFIER);
        keywords.put("HLL_UNION", TokenType.IDENTIFIER);
        keywords.put("HLL_RAW_AGG", TokenType.IDENTIFIER);
        keywords.put("HLL_CARDINALITY", TokenType.IDENTIFIER);
        keywords.put("HLL_FROM_BASE64", TokenType.IDENTIFIER);
        keywords.put("HLL_TO_BASE64", TokenType.IDENTIFIER);

        // Table models
        keywords.put("DUPLICATE", TokenType.IDENTIFIER);
        keywords.put("PRIMARY", TokenType.IDENTIFIER);
        keywords.put("UNIQUE", TokenType.IDENTIFIER);
        keywords.put("KEY", TokenType.IDENTIFIER);
        keywords.put("AGGREGATE", TokenType.IDENTIFIER);
        keywords.put("AGGREGATED", TokenType.IDENTIFIER);

        // Partitioning and distribution
        keywords.put("DISTRIBUTED", TokenType.IDENTIFIER);
        keywords.put("PARTITION", TokenType.IDENTIFIER);
        keywords.put("PARTITIONED", TokenType.IDENTIFIER);
        keywords.put("BY", TokenType.IDENTIFIER);
        keywords.put("BUCKET", TokenType.IDENTIFIER);
        keywords.put("BUCKETED", TokenType.IDENTIFIER);
        keywords.put("BUCKETS", TokenType.IDENTIFIER);
        keywords.put("HASH", TokenType.IDENTIFIER);
        keywords.put("RANGE", TokenType.IDENTIFIER);
        keywords.put("LIST", TokenType.IDENTIFIER);
        keywords.put("AUTO", TokenType.IDENTIFIER);
        keywords.put("STEP", TokenType.IDENTIFIER);

        // Aggregate specifications
        keywords.put("SUM", TokenType.IDENTIFIER);
        keywords.put("COUNT", TokenType.IDENTIFIER);
        keywords.put("AVG", TokenType.IDENTIFIER);
        keywords.put("MIN", TokenType.IDENTIFIER);
        keywords.put("MAX", TokenType.IDENTIFIER);
        keywords.put("REPLACE", TokenType.IDENTIFIER);
        keywords.put("PERCENTILE_APPROX", TokenType.IDENTIFIER);

        // ROLLUP and materialized views
        keywords.put("ROLLUP", TokenType.IDENTIFIER);
        keywords.put("MATERIALIZED", TokenType.IDENTIFIER);
        keywords.put("VIEW", TokenType.IDENTIFIER);

        // Colocate and replication
        keywords.put("COLOCATE", TokenType.IDENTIFIER);
        keywords.put("WITH", TokenType.IDENTIFIER);
        keywords.put("REPLICATION_NUM", TokenType.IDENTIFIER);
        keywords.put("REPLICATION", TokenType.IDENTIFIER);
        keywords.put("REPLICA", TokenType.IDENTIFIER);
        keywords.put("DYNAMIC", TokenType.IDENTIFIER);

        // Data types
        keywords.put("BOOLEAN", TokenType.IDENTIFIER);
        keywords.put("TINYINT", TokenType.IDENTIFIER);
        keywords.put("SMALLINT", TokenType.IDENTIFIER);
        keywords.put("INT", TokenType.IDENTIFIER);
        keywords.put("INTEGER", TokenType.IDENTIFIER);
        keywords.put("BIGINT", TokenType.IDENTIFIER);
        keywords.put("LARGEINT", TokenType.IDENTIFIER);
        keywords.put("FLOAT", TokenType.IDENTIFIER);
        keywords.put("DOUBLE", TokenType.IDENTIFIER);
        keywords.put("DECIMAL", TokenType.IDENTIFIER);
        keywords.put("NUMERIC", TokenType.IDENTIFIER);
        keywords.put("CHAR", TokenType.IDENTIFIER);
        keywords.put("VARCHAR", TokenType.IDENTIFIER);
        keywords.put("STRING", TokenType.IDENTIFIER);
        keywords.put("BINARY", TokenType.IDENTIFIER);
        keywords.put("VARBINARY", TokenType.IDENTIFIER);
        keywords.put("DATE", TokenType.IDENTIFIER);
        keywords.put("DATETIME", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("TIME", TokenType.IDENTIFIER);
        keywords.put("JSON", TokenType.IDENTIFIER);
        keywords.put("JSONB", TokenType.IDENTIFIER);
        keywords.put("ARRAY", TokenType.IDENTIFIER);
        keywords.put("STRUCT", TokenType.IDENTIFIER);
        keywords.put("MAP", TokenType.IDENTIFIER);

        // Engine and properties
        keywords.put("ENGINE", TokenType.IDENTIFIER);
        keywords.put("OLAP", TokenType.IDENTIFIER);
        keywords.put("ICEBERG", TokenType.IDENTIFIER);
        keywords.put("HIVE", TokenType.IDENTIFIER);
        keywords.put("JDBC", TokenType.IDENTIFIER);
        keywords.put("MYSQL", TokenType.IDENTIFIER);
        keywords.put("ELASTICSEARCH", TokenType.IDENTIFIER);
        keywords.put("PROPERTIES", TokenType.IDENTIFIER);
        keywords.put("COMMENT", TokenType.IDENTIFIER);
        keywords.put("DEFAULT", TokenType.IDENTIFIER);
        keywords.put("NOT", TokenType.IDENTIFIER);
        keywords.put("NULL", TokenType.IDENTIFIER);
        keywords.put("CONSTRAINT", TokenType.IDENTIFIER);

        // Compression and encoding
        keywords.put("COMPRESSION", TokenType.IDENTIFIER);
        keywords.put("CODEC", TokenType.IDENTIFIER);
        keywords.put("LZ4", TokenType.IDENTIFIER);
        keywords.put("LZ4HC", TokenType.IDENTIFIER);
        keywords.put("ZSTD", TokenType.IDENTIFIER);
        keywords.put("SNAPPY", TokenType.IDENTIFIER);
        keywords.put("DEFLATE", TokenType.IDENTIFIER);
        keywords.put("UNCOMPRESSED", TokenType.IDENTIFIER);

        // Standard string functions
        keywords.put("LENGTH", TokenType.IDENTIFIER);
        keywords.put("SUBSTR", TokenType.IDENTIFIER);
        keywords.put("SUBSTRING", TokenType.IDENTIFIER);
        keywords.put("UPPER", TokenType.IDENTIFIER);
        keywords.put("LOWER", TokenType.IDENTIFIER);
        keywords.put("TRIM", TokenType.IDENTIFIER);
        keywords.put("LTRIM", TokenType.IDENTIFIER);
        keywords.put("RTRIM", TokenType.IDENTIFIER);
        keywords.put("CONCAT", TokenType.IDENTIFIER);
        keywords.put("CONCAT_WS", TokenType.IDENTIFIER);
        keywords.put("SPLIT", TokenType.IDENTIFIER);

        // Date functions
        keywords.put("NOW", TokenType.IDENTIFIER);
        keywords.put("CURRENT_DATE", TokenType.IDENTIFIER);
        keywords.put("CURRENT_TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("DATE_FORMAT", TokenType.IDENTIFIER);
        keywords.put("DATE_PARSE", TokenType.IDENTIFIER);
        keywords.put("DATE_ADD", TokenType.IDENTIFIER);
        keywords.put("DATE_SUB", TokenType.IDENTIFIER);
        keywords.put("DATEDIFF", TokenType.IDENTIFIER);
        keywords.put("EXTRACT", TokenType.IDENTIFIER);
        keywords.put("YEAR", TokenType.IDENTIFIER);
        keywords.put("MONTH", TokenType.IDENTIFIER);
        keywords.put("DAY", TokenType.IDENTIFIER);
        keywords.put("HOUR", TokenType.IDENTIFIER);
        keywords.put("MINUTE", TokenType.IDENTIFIER);
        keywords.put("SECOND", TokenType.IDENTIFIER);

        // Math functions
        keywords.put("ABS", TokenType.IDENTIFIER);
        keywords.put("CEIL", TokenType.IDENTIFIER);
        keywords.put("FLOOR", TokenType.IDENTIFIER);
        keywords.put("ROUND", TokenType.IDENTIFIER);
        keywords.put("SQRT", TokenType.IDENTIFIER);
        keywords.put("POWER", TokenType.IDENTIFIER);
        keywords.put("EXP", TokenType.IDENTIFIER);
        keywords.put("LOG", TokenType.IDENTIFIER);
        keywords.put("LOG10", TokenType.IDENTIFIER);
        keywords.put("LN", TokenType.IDENTIFIER);

        return Trie.build(keywords);
    }
}
