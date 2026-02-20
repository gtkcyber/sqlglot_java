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
package com.gtkcyber.sqlglot.dialects.spark;

import com.gtkcyber.sqlglot.tokens.Tokenizer;
import com.gtkcyber.sqlglot.tokens.TokenType;
import com.gtkcyber.sqlglot.tokens.Trie;

import java.util.HashMap;
import java.util.Map;

/**
 * Spark SQL tokenizer.
 * Supports Spark-specific syntax:
 * - Backtick identifiers
 * - Double quote identifiers
 * - Single quote strings
 * - Spark-specific keywords and functions
 * - Delta Lake syntax
 */
public class SparkTokenizer extends Tokenizer {
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

        // Spark-specific keywords
        keywords.put("DELTA", TokenType.IDENTIFIER);
        keywords.put("FORMAT", TokenType.IDENTIFIER);
        keywords.put("LOCATION", TokenType.IDENTIFIER);
        keywords.put("PATH", TokenType.IDENTIFIER);
        keywords.put("EXTERNAL", TokenType.IDENTIFIER);
        keywords.put("MANAGED", TokenType.IDENTIFIER);
        keywords.put("PARTITION", TokenType.IDENTIFIER);
        keywords.put("BY", TokenType.IDENTIFIER);
        keywords.put("CLUSTERED", TokenType.IDENTIFIER);
        keywords.put("INTO", TokenType.IDENTIFIER);
        keywords.put("BUCKETS", TokenType.IDENTIFIER);
        keywords.put("SKEWED", TokenType.IDENTIFIER);
        keywords.put("STORED", TokenType.IDENTIFIER);
        keywords.put("ROW", TokenType.IDENTIFIER);
        keywords.put("FORMAT", TokenType.IDENTIFIER);
        keywords.put("FIELDS", TokenType.IDENTIFIER);
        keywords.put("TERMINATED", TokenType.IDENTIFIER);
        keywords.put("BY", TokenType.IDENTIFIER);
        keywords.put("ESCAPED", TokenType.IDENTIFIER);
        keywords.put("LINES", TokenType.IDENTIFIER);
        keywords.put("NULL", TokenType.IDENTIFIER);
        keywords.put("DEFINED", TokenType.IDENTIFIER);
        keywords.put("AS", TokenType.IDENTIFIER);
        keywords.put("COLLECTION", TokenType.IDENTIFIER);
        keywords.put("ITEMS", TokenType.IDENTIFIER);
        keywords.put("KEYS", TokenType.IDENTIFIER);
        keywords.put("PROPERTIES", TokenType.IDENTIFIER);
        keywords.put("TBLPROPERTIES", TokenType.IDENTIFIER);
        keywords.put("DBPROPERTIES", TokenType.IDENTIFIER);
        keywords.put("SERDEPROPERTIES", TokenType.IDENTIFIER);
        keywords.put("SERDE", TokenType.IDENTIFIER);
        keywords.put("WITH", TokenType.IDENTIFIER);
        keywords.put("COMMENT", TokenType.IDENTIFIER);
        keywords.put("TEMPORARY", TokenType.IDENTIFIER);
        keywords.put("TRANSIENT", TokenType.IDENTIFIER);
        keywords.put("SNAPSHOT", TokenType.IDENTIFIER);
        keywords.put("VERSION", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("FOR", TokenType.IDENTIFIER);
        keywords.put("TIME", TokenType.IDENTIFIER);
        keywords.put("TRAVEL", TokenType.IDENTIFIER);
        keywords.put("RESTORE", TokenType.IDENTIFIER);
        keywords.put("TABLE", TokenType.IDENTIFIER);
        keywords.put("TO", TokenType.IDENTIFIER);
        keywords.put("VERSION", TokenType.IDENTIFIER);

        // Spark data types
        keywords.put("ARRAY", TokenType.IDENTIFIER);
        keywords.put("MAP", TokenType.IDENTIFIER);
        keywords.put("STRUCT", TokenType.IDENTIFIER);
        keywords.put("DECIMAL", TokenType.IDENTIFIER);
        keywords.put("DATE", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("BINARY", TokenType.IDENTIFIER);
        keywords.put("BOOLEAN", TokenType.IDENTIFIER);
        keywords.put("BYTE", TokenType.IDENTIFIER);
        keywords.put("SHORT", TokenType.IDENTIFIER);
        keywords.put("INT", TokenType.IDENTIFIER);
        keywords.put("LONG", TokenType.IDENTIFIER);
        keywords.put("FLOAT", TokenType.IDENTIFIER);
        keywords.put("DOUBLE", TokenType.IDENTIFIER);
        keywords.put("STRING", TokenType.IDENTIFIER);

        // Spark functions
        keywords.put("EXPLODE", TokenType.IDENTIFIER);
        keywords.put("EXPLODE_OUTER", TokenType.IDENTIFIER);
        keywords.put("POSEXPLODE", TokenType.IDENTIFIER);
        keywords.put("POSEXPLODE_OUTER", TokenType.IDENTIFIER);
        keywords.put("INLINE", TokenType.IDENTIFIER);
        keywords.put("INLINE_OUTER", TokenType.IDENTIFIER);
        keywords.put("LATERAL", TokenType.IDENTIFIER);
        keywords.put("VIEW", TokenType.IDENTIFIER);
        keywords.put("LATERAL_VIEW", TokenType.IDENTIFIER);
        keywords.put("COLLATE", TokenType.IDENTIFIER);
        keywords.put("REPARTITION", TokenType.IDENTIFIER);
        keywords.put("DISTRIBUTE", TokenType.IDENTIFIER);
        keywords.put("SORT", TokenType.IDENTIFIER);
        keywords.put("BROADCAST", TokenType.IDENTIFIER);
        keywords.put("COALESCE", TokenType.IDENTIFIER);
        keywords.put("COMPACT", TokenType.IDENTIFIER);
        keywords.put("OPTIMIZE", TokenType.IDENTIFIER);
        keywords.put("ZORDER", TokenType.IDENTIFIER);
        keywords.put("BY", TokenType.IDENTIFIER);
        keywords.put("VACUUM", TokenType.IDENTIFIER);
        keywords.put("RETAIN", TokenType.IDENTIFIER);
        keywords.put("HOURS", TokenType.IDENTIFIER);
        keywords.put("ANALYZE", TokenType.IDENTIFIER);
        keywords.put("TABLE", TokenType.IDENTIFIER);
        keywords.put("COMPUTE", TokenType.IDENTIFIER);
        keywords.put("STATISTICS", TokenType.IDENTIFIER);
        keywords.put("FOR", TokenType.IDENTIFIER);
        keywords.put("COLUMNS", TokenType.IDENTIFIER);
        keywords.put("EXPLAIN", TokenType.IDENTIFIER);
        keywords.put("EXTENDED", TokenType.IDENTIFIER);
        keywords.put("FORMATTED", TokenType.IDENTIFIER);
        keywords.put("COST", TokenType.IDENTIFIER);

        // Spark SQL functions
        keywords.put("CAST", TokenType.IDENTIFIER);
        keywords.put("TRY_CAST", TokenType.IDENTIFIER);
        keywords.put("COALESCE", TokenType.IDENTIFIER);
        keywords.put("NULLIF", TokenType.IDENTIFIER);
        keywords.put("IFNULL", TokenType.IDENTIFIER);
        keywords.put("NVL", TokenType.IDENTIFIER);
        keywords.put("NVL2", TokenType.IDENTIFIER);
        keywords.put("ISNULL", TokenType.IDENTIFIER);
        keywords.put("IF", TokenType.IDENTIFIER);
        keywords.put("CASE", TokenType.IDENTIFIER);
        keywords.put("WHEN", TokenType.IDENTIFIER);
        keywords.put("THEN", TokenType.IDENTIFIER);
        keywords.put("ELSE", TokenType.IDENTIFIER);
        keywords.put("END", TokenType.IDENTIFIER);
        keywords.put("FROM_JSON", TokenType.IDENTIFIER);
        keywords.put("TO_JSON", TokenType.IDENTIFIER);
        keywords.put("JSON_TUPLE", TokenType.IDENTIFIER);
        keywords.put("GET_JSON_OBJECT", TokenType.IDENTIFIER);
        keywords.put("REGEXP", TokenType.IDENTIFIER);
        keywords.put("LIKE", TokenType.IDENTIFIER);
        keywords.put("RLIKE", TokenType.IDENTIFIER);
        keywords.put("GLOB", TokenType.IDENTIFIER);
        keywords.put("SUBSTR", TokenType.IDENTIFIER);
        keywords.put("SUBSTRING", TokenType.IDENTIFIER);
        keywords.put("LENGTH", TokenType.IDENTIFIER);
        keywords.put("TRIM", TokenType.IDENTIFIER);
        keywords.put("LTRIM", TokenType.IDENTIFIER);
        keywords.put("RTRIM", TokenType.IDENTIFIER);
        keywords.put("UPPER", TokenType.IDENTIFIER);
        keywords.put("LOWER", TokenType.IDENTIFIER);
        keywords.put("INITCAP", TokenType.IDENTIFIER);
        keywords.put("CONCAT", TokenType.IDENTIFIER);
        keywords.put("CONCAT_WS", TokenType.IDENTIFIER);
        keywords.put("REPEAT", TokenType.IDENTIFIER);
        keywords.put("REPLACE", TokenType.IDENTIFIER);
        keywords.put("INSTR", TokenType.IDENTIFIER);
        keywords.put("LOCATE", TokenType.IDENTIFIER);
        keywords.put("FIND_IN_SET", TokenType.IDENTIFIER);
        keywords.put("LPAD", TokenType.IDENTIFIER);
        keywords.put("RPAD", TokenType.IDENTIFIER);
        keywords.put("LEVENSHTEIN", TokenType.IDENTIFIER);
        keywords.put("SOUNDEX", TokenType.IDENTIFIER);
        keywords.put("REVERSE", TokenType.IDENTIFIER);
        keywords.put("FORMAT_STRING", TokenType.IDENTIFIER);
        keywords.put("PRINTF", TokenType.IDENTIFIER);

        // Date/time functions
        keywords.put("CURRENT_DATE", TokenType.IDENTIFIER);
        keywords.put("CURRENT_TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("CURRENT_TIMEZONE", TokenType.IDENTIFIER);
        keywords.put("DATE_FORMAT", TokenType.IDENTIFIER);
        keywords.put("DATE_PARSE", TokenType.IDENTIFIER);
        keywords.put("FROM_UNIXTIME", TokenType.IDENTIFIER);
        keywords.put("UNIX_TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("TO_DATE", TokenType.IDENTIFIER);
        keywords.put("TO_TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("DATEDIFF", TokenType.IDENTIFIER);
        keywords.put("DATEADD", TokenType.IDENTIFIER);
        keywords.put("DATE_ADD", TokenType.IDENTIFIER);
        keywords.put("DATE_SUB", TokenType.IDENTIFIER);
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
        keywords.put("WEEKOFYEAR", TokenType.IDENTIFIER);
        keywords.put("LAST_DAY", TokenType.IDENTIFIER);
        keywords.put("NEXT_DAY", TokenType.IDENTIFIER);
        keywords.put("TRUNC", TokenType.IDENTIFIER);

        // Math functions
        keywords.put("ABS", TokenType.IDENTIFIER);
        keywords.put("CEIL", TokenType.IDENTIFIER);
        keywords.put("FLOOR", TokenType.IDENTIFIER);
        keywords.put("ROUND", TokenType.IDENTIFIER);
        keywords.put("SQRT", TokenType.IDENTIFIER);
        keywords.put("POWER", TokenType.IDENTIFIER);
        keywords.put("SIGN", TokenType.IDENTIFIER);

        return Trie.build(keywords);
    }
}
