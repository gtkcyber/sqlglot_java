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
package com.gtkcyber.sqlglot.dialects.databricks;

import com.gtkcyber.sqlglot.tokens.Tokenizer;
import com.gtkcyber.sqlglot.tokens.TokenType;
import com.gtkcyber.sqlglot.tokens.Trie;

import java.util.HashMap;
import java.util.Map;

/**
 * Databricks tokenizer.
 * Supports Databricks-specific syntax:
 * - Backtick identifiers for reserved words and special characters
 * - Single quote strings with doubled quote escaping
 * - Databricks-specific keywords (DELTA, MERGE, etc.)
 * - Python and Scala UDF declarations
 * - Complex data types (ARRAY, STRUCT, MAP)
 * - Delta Lake operations (OPTIMIZE, VACUUM, etc.)
 * - Unity Catalog naming
 */
public class DatabricksTokenizer extends Tokenizer {
    @Override
    protected Map<String, Character> getIdentifierQuotes() {
        Map<String, Character> quotes = new HashMap<>();
        quotes.put("`", '`');    // Backticks (primary)
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

        // Delta Lake keywords
        keywords.put("DELTA", TokenType.IDENTIFIER);
        keywords.put("MERGE", TokenType.IDENTIFIER);
        keywords.put("MATCHED", TokenType.IDENTIFIER);
        keywords.put("NOT", TokenType.IDENTIFIER);
        keywords.put("WHEN", TokenType.IDENTIFIER);
        keywords.put("THEN", TokenType.IDENTIFIER);
        keywords.put("UPDATE", TokenType.IDENTIFIER);
        keywords.put("DELETE", TokenType.IDENTIFIER);
        keywords.put("INSERT", TokenType.IDENTIFIER);
        keywords.put("USING", TokenType.IDENTIFIER);
        keywords.put("ON", TokenType.IDENTIFIER);
        keywords.put("OPTIMIZE", TokenType.IDENTIFIER);
        keywords.put("VACUUM", TokenType.IDENTIFIER);
        keywords.put("RETENTION", TokenType.IDENTIFIER);
        keywords.put("HOURS", TokenType.IDENTIFIER);
        keywords.put("DAYS", TokenType.IDENTIFIER);
        keywords.put("RESTORE", TokenType.IDENTIFIER);
        keywords.put("DESCRIBE", TokenType.IDENTIFIER);
        keywords.put("HISTORY", TokenType.IDENTIFIER);
        keywords.put("DETAIL", TokenType.IDENTIFIER);

        // UDF keywords
        keywords.put("UDFS", TokenType.IDENTIFIER);
        keywords.put("UDF", TokenType.IDENTIFIER);
        keywords.put("PYTHON", TokenType.IDENTIFIER);
        keywords.put("SCALA", TokenType.IDENTIFIER);
        keywords.put("SQL", TokenType.IDENTIFIER);
        keywords.put("JAVA", TokenType.IDENTIFIER);
        keywords.put("DEF", TokenType.IDENTIFIER);
        keywords.put("RETURN", TokenType.IDENTIFIER);
        keywords.put("VAL", TokenType.IDENTIFIER);

        // Data ingestion
        keywords.put("COPY", TokenType.IDENTIFIER);
        keywords.put("INTO", TokenType.IDENTIFIER);
        keywords.put("FROM", TokenType.IDENTIFIER);
        keywords.put("LOCATION", TokenType.IDENTIFIER);
        keywords.put("CREDENTIAL", TokenType.IDENTIFIER);
        keywords.put("CREDENTIALS", TokenType.IDENTIFIER);
        keywords.put("FORMAT", TokenType.IDENTIFIER);
        keywords.put("CSV", TokenType.IDENTIFIER);
        keywords.put("JSON", TokenType.IDENTIFIER);
        keywords.put("PARQUET", TokenType.IDENTIFIER);
        keywords.put("DELTA", TokenType.IDENTIFIER);
        keywords.put("AVRO", TokenType.IDENTIFIER);
        keywords.put("ORC", TokenType.IDENTIFIER);
        keywords.put("TEXT", TokenType.IDENTIFIER);
        keywords.put("FILEFORMAT", TokenType.IDENTIFIER);
        keywords.put("VALIDATION_MODE", TokenType.IDENTIFIER);
        keywords.put("FAIL", TokenType.IDENTIFIER);
        keywords.put("SKIP", TokenType.IDENTIFIER);
        keywords.put("ABORT", TokenType.IDENTIFIER);

        // Unity Catalog
        keywords.put("CATALOG", TokenType.IDENTIFIER);
        keywords.put("SCHEMA", TokenType.IDENTIFIER);
        keywords.put("DATABASE", TokenType.IDENTIFIER);
        keywords.put("VOLUME", TokenType.IDENTIFIER);
        keywords.put("METASTORE", TokenType.IDENTIFIER);
        keywords.put("WORKSPACE", TokenType.IDENTIFIER);
        keywords.put("EXTERNAL", TokenType.IDENTIFIER);
        keywords.put("MANAGED", TokenType.IDENTIFIER);

        // Partitioning and clustering
        keywords.put("PARTITION", TokenType.IDENTIFIER);
        keywords.put("PARTITIONED", TokenType.IDENTIFIER);
        keywords.put("BY", TokenType.IDENTIFIER);
        keywords.put("CLUSTER", TokenType.IDENTIFIER);
        keywords.put("BUCKET", TokenType.IDENTIFIER);
        keywords.put("BUCKETED", TokenType.IDENTIFIER);
        keywords.put("CLUSTERED", TokenType.IDENTIFIER);
        keywords.put("INTO", TokenType.IDENTIFIER);
        keywords.put("BUCKETS", TokenType.IDENTIFIER);
        keywords.put("SORTED", TokenType.IDENTIFIER);

        // Table properties
        keywords.put("TBLPROPERTIES", TokenType.IDENTIFIER);
        keywords.put("PROPERTIES", TokenType.IDENTIFIER);
        keywords.put("COMMENT", TokenType.IDENTIFIER);
        keywords.put("CONSTRAINT", TokenType.IDENTIFIER);
        keywords.put("PRIMARY", TokenType.IDENTIFIER);
        keywords.put("KEY", TokenType.IDENTIFIER);
        keywords.put("UNIQUE", TokenType.IDENTIFIER);
        keywords.put("NOT", TokenType.IDENTIFIER);
        keywords.put("NULL", TokenType.IDENTIFIER);
        keywords.put("DEFAULT", TokenType.IDENTIFIER);
        keywords.put("CHECK", TokenType.IDENTIFIER);
        keywords.put("COLLATE", TokenType.IDENTIFIER);
        keywords.put("GENERATED", TokenType.IDENTIFIER);
        keywords.put("ALWAYS", TokenType.IDENTIFIER);
        keywords.put("AS", TokenType.IDENTIFIER);

        // Data types
        keywords.put("BOOLEAN", TokenType.IDENTIFIER);
        keywords.put("BYTE", TokenType.IDENTIFIER);
        keywords.put("SHORT", TokenType.IDENTIFIER);
        keywords.put("INT", TokenType.IDENTIFIER);
        keywords.put("INTEGER", TokenType.IDENTIFIER);
        keywords.put("LONG", TokenType.IDENTIFIER);
        keywords.put("BIGINT", TokenType.IDENTIFIER);
        keywords.put("FLOAT", TokenType.IDENTIFIER);
        keywords.put("DOUBLE", TokenType.IDENTIFIER);
        keywords.put("DECIMAL", TokenType.IDENTIFIER);
        keywords.put("NUMERIC", TokenType.IDENTIFIER);
        keywords.put("CHAR", TokenType.IDENTIFIER);
        keywords.put("VARCHAR", TokenType.IDENTIFIER);
        keywords.put("STRING", TokenType.IDENTIFIER);
        keywords.put("BINARY", TokenType.IDENTIFIER);
        keywords.put("BYTE", TokenType.IDENTIFIER);
        keywords.put("DATE", TokenType.IDENTIFIER);
        keywords.put("TIME", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMPNTZ", TokenType.IDENTIFIER);
        keywords.put("INTERVAL", TokenType.IDENTIFIER);
        keywords.put("ARRAY", TokenType.IDENTIFIER);
        keywords.put("STRUCT", TokenType.IDENTIFIER);
        keywords.put("MAP", TokenType.IDENTIFIER);

        // Standard functions
        keywords.put("COUNT", TokenType.IDENTIFIER);
        keywords.put("SUM", TokenType.IDENTIFIER);
        keywords.put("AVG", TokenType.IDENTIFIER);
        keywords.put("MIN", TokenType.IDENTIFIER);
        keywords.put("MAX", TokenType.IDENTIFIER);
        keywords.put("STDDEV", TokenType.IDENTIFIER);
        keywords.put("VARIANCE", TokenType.IDENTIFIER);
        keywords.put("COLLECT_LIST", TokenType.IDENTIFIER);
        keywords.put("COLLECT_SET", TokenType.IDENTIFIER);
        keywords.put("APPROX_COUNT_DISTINCT", TokenType.IDENTIFIER);
        keywords.put("EXPLODE", TokenType.IDENTIFIER);
        keywords.put("LATERAL", TokenType.IDENTIFIER);
        keywords.put("COALESCE", TokenType.IDENTIFIER);
        keywords.put("IFNULL", TokenType.IDENTIFIER);
        keywords.put("NULLIF", TokenType.IDENTIFIER);
        keywords.put("CAST", TokenType.IDENTIFIER);
        keywords.put("TRY_CAST", TokenType.IDENTIFIER);

        // String functions
        keywords.put("UPPER", TokenType.IDENTIFIER);
        keywords.put("LOWER", TokenType.IDENTIFIER);
        keywords.put("TRIM", TokenType.IDENTIFIER);
        keywords.put("LTRIM", TokenType.IDENTIFIER);
        keywords.put("RTRIM", TokenType.IDENTIFIER);
        keywords.put("LENGTH", TokenType.IDENTIFIER);
        keywords.put("SUBSTR", TokenType.IDENTIFIER);
        keywords.put("SUBSTRING", TokenType.IDENTIFIER);
        keywords.put("CONCAT", TokenType.IDENTIFIER);
        keywords.put("CONCAT_WS", TokenType.IDENTIFIER);
        keywords.put("INITCAP", TokenType.IDENTIFIER);
        keywords.put("LPAD", TokenType.IDENTIFIER);
        keywords.put("RPAD", TokenType.IDENTIFIER);
        keywords.put("SPLIT", TokenType.IDENTIFIER);
        keywords.put("REPLACE", TokenType.IDENTIFIER);
        keywords.put("REGEXP_REPLACE", TokenType.IDENTIFIER);
        keywords.put("REGEXP_EXTRACT", TokenType.IDENTIFIER);
        keywords.put("RLIKE", TokenType.IDENTIFIER);

        // Date/Time functions
        keywords.put("NOW", TokenType.IDENTIFIER);
        keywords.put("CURRENT_DATE", TokenType.IDENTIFIER);
        keywords.put("CURRENT_TIME", TokenType.IDENTIFIER);
        keywords.put("CURRENT_TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("DATE", TokenType.IDENTIFIER);
        keywords.put("DATE_FORMAT", TokenType.IDENTIFIER);
        keywords.put("DATE_PARSE", TokenType.IDENTIFIER);
        keywords.put("DATE_TRUNC", TokenType.IDENTIFIER);
        keywords.put("DATE_ADD", TokenType.IDENTIFIER);
        keywords.put("DATE_SUB", TokenType.IDENTIFIER);
        keywords.put("DATEDIFF", TokenType.IDENTIFIER);
        keywords.put("DATEADD", TokenType.IDENTIFIER);
        keywords.put("EXTRACT", TokenType.IDENTIFIER);
        keywords.put("YEAR", TokenType.IDENTIFIER);
        keywords.put("MONTH", TokenType.IDENTIFIER);
        keywords.put("DAY", TokenType.IDENTIFIER);
        keywords.put("HOUR", TokenType.IDENTIFIER);
        keywords.put("MINUTE", TokenType.IDENTIFIER);
        keywords.put("SECOND", TokenType.IDENTIFIER);
        keywords.put("QUARTER", TokenType.IDENTIFIER);
        keywords.put("WEEK", TokenType.IDENTIFIER);
        keywords.put("DAYOFWEEK", TokenType.IDENTIFIER);
        keywords.put("DAYOFMONTH", TokenType.IDENTIFIER);
        keywords.put("DAYOFYEAR", TokenType.IDENTIFIER);

        // Array functions
        keywords.put("ARRAY", TokenType.IDENTIFIER);
        keywords.put("ARRAY_CONTAINS", TokenType.IDENTIFIER);
        keywords.put("ARRAY_LENGTH", TokenType.IDENTIFIER);
        keywords.put("ARRAY_POSITION", TokenType.IDENTIFIER);
        keywords.put("FLATTEN", TokenType.IDENTIFIER);
        keywords.put("TRANSFORM", TokenType.IDENTIFIER);
        keywords.put("FILTER", TokenType.IDENTIFIER);
        keywords.put("ZIP_WITH", TokenType.IDENTIFIER);
        keywords.put("SORT_ARRAY", TokenType.IDENTIFIER);
        keywords.put("REVERSE", TokenType.IDENTIFIER);
        keywords.put("CONCAT_WS", TokenType.IDENTIFIER);
        keywords.put("SIZE", TokenType.IDENTIFIER);

        // Map functions
        keywords.put("MAP", TokenType.IDENTIFIER);
        keywords.put("MAP_KEYS", TokenType.IDENTIFIER);
        keywords.put("MAP_VALUES", TokenType.IDENTIFIER);
        keywords.put("MAP_CONCAT", TokenType.IDENTIFIER);
        keywords.put("STR_TO_MAP", TokenType.IDENTIFIER);
        keywords.put("MAP_FROM_ENTRIES", TokenType.IDENTIFIER);

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
        keywords.put("RAND", TokenType.IDENTIFIER);

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

        // GPU and Performance
        keywords.put("GPU", TokenType.IDENTIFIER);
        keywords.put("PHOTON", TokenType.IDENTIFIER);
        keywords.put("ACCELERATED", TokenType.IDENTIFIER);
        keywords.put("HINT", TokenType.IDENTIFIER);
        keywords.put("HINTS", TokenType.IDENTIFIER);

        return Trie.build(keywords);
    }
}
