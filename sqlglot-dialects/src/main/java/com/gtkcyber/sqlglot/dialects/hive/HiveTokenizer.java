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
package com.gtkcyber.sqlglot.dialects.hive;

import com.gtkcyber.sqlglot.tokens.Tokenizer;
import com.gtkcyber.sqlglot.tokens.TokenType;
import com.gtkcyber.sqlglot.tokens.Trie;

import java.util.HashMap;
import java.util.Map;

/**
 * Apache Hive tokenizer.
 * Supports Hive-specific syntax:
 * - Backtick identifiers
 * - Double quote identifiers
 * - Single quote strings
 * - Hive-specific keywords and functions
 * - Complex data type keywords
 */
public class HiveTokenizer extends Tokenizer {
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

        // Hive-specific keywords
        keywords.put("LATERAL", TokenType.IDENTIFIER);
        keywords.put("VIEW", TokenType.IDENTIFIER);
        keywords.put("LATERAL_VIEW", TokenType.IDENTIFIER);
        keywords.put("EXPLODE", TokenType.IDENTIFIER);
        keywords.put("EXPLODE_OUTER", TokenType.IDENTIFIER);
        keywords.put("POSEXPLODE", TokenType.IDENTIFIER);
        keywords.put("POSEXPLODE_OUTER", TokenType.IDENTIFIER);
        keywords.put("INLINE", TokenType.IDENTIFIER);
        keywords.put("INLINE_OUTER", TokenType.IDENTIFIER);
        keywords.put("STACK", TokenType.IDENTIFIER);
        keywords.put("SPLIT", TokenType.IDENTIFIER);
        keywords.put("MAP_KEYS", TokenType.IDENTIFIER);
        keywords.put("MAP_VALUES", TokenType.IDENTIFIER);
        keywords.put("STRUCT", TokenType.IDENTIFIER);
        keywords.put("ARRAY", TokenType.IDENTIFIER);
        keywords.put("MAP", TokenType.IDENTIFIER);
        keywords.put("UNIONTYPE", TokenType.IDENTIFIER);
        keywords.put("SKEWED", TokenType.IDENTIFIER);
        keywords.put("BY", TokenType.IDENTIFIER);
        keywords.put("ON", TokenType.IDENTIFIER);
        keywords.put("STORED", TokenType.IDENTIFIER);
        keywords.put("AS", TokenType.IDENTIFIER);
        keywords.put("DIRECTORIES", TokenType.IDENTIFIER);
        keywords.put("LOCATION", TokenType.IDENTIFIER);
        keywords.put("PARTITION", TokenType.IDENTIFIER);
        keywords.put("PARTITIONED", TokenType.IDENTIFIER);
        keywords.put("CLUSTERED", TokenType.IDENTIFIER);
        keywords.put("INTO", TokenType.IDENTIFIER);
        keywords.put("BUCKETS", TokenType.IDENTIFIER);
        keywords.put("SORTED", TokenType.IDENTIFIER);
        keywords.put("ORDERED", TokenType.IDENTIFIER);
        keywords.put("BUCKET", TokenType.IDENTIFIER);
        keywords.put("EXTERNAL", TokenType.IDENTIFIER);
        keywords.put("TEMPORARY", TokenType.IDENTIFIER);
        keywords.put("TRANSIENT", TokenType.IDENTIFIER);
        keywords.put("TABLE", TokenType.IDENTIFIER);
        keywords.put("PROPERTIES", TokenType.IDENTIFIER);
        keywords.put("TBLPROPERTIES", TokenType.IDENTIFIER);
        keywords.put("DBPROPERTIES", TokenType.IDENTIFIER);
        keywords.put("SERDEPROPERTIES", TokenType.IDENTIFIER);
        keywords.put("SERDE", TokenType.IDENTIFIER);
        keywords.put("WITH", TokenType.IDENTIFIER);
        keywords.put("DELIMITER", TokenType.IDENTIFIER);
        keywords.put("FIELDS", TokenType.IDENTIFIER);
        keywords.put("TERMINATED", TokenType.IDENTIFIER);
        keywords.put("ESCAPED", TokenType.IDENTIFIER);
        keywords.put("COLLECTION", TokenType.IDENTIFIER);
        keywords.put("ITEMS", TokenType.IDENTIFIER);
        keywords.put("KEYS", TokenType.IDENTIFIER);
        keywords.put("LINES", TokenType.IDENTIFIER);
        keywords.put("INPUTFORMAT", TokenType.IDENTIFIER);
        keywords.put("OUTPUTFORMAT", TokenType.IDENTIFIER);
        keywords.put("ROW", TokenType.IDENTIFIER);
        keywords.put("FORMAT", TokenType.IDENTIFIER);
        keywords.put("OFILE", TokenType.IDENTIFIER);
        keywords.put("ORC", TokenType.IDENTIFIER);
        keywords.put("PARQUET", TokenType.IDENTIFIER);
        keywords.put("STORED", TokenType.IDENTIFIER);
        keywords.put("AS", TokenType.IDENTIFIER);
        keywords.put("SEQUENCEFILE", TokenType.IDENTIFIER);
        keywords.put("RCFILE", TokenType.IDENTIFIER);
        keywords.put("TEXTFILE", TokenType.IDENTIFIER);
        keywords.put("AVRO", TokenType.IDENTIFIER);
        keywords.put("JSONFILE", TokenType.IDENTIFIER);
        keywords.put("SHOW", TokenType.IDENTIFIER);
        keywords.put("DESCRIBE", TokenType.IDENTIFIER);
        keywords.put("DESC", TokenType.IDENTIFIER);
        keywords.put("EXTENDED", TokenType.IDENTIFIER);
        keywords.put("FORMATTED", TokenType.IDENTIFIER);
        keywords.put("PARTITIONS", TokenType.IDENTIFIER);
        keywords.put("COLUMNS", TokenType.IDENTIFIER);
        keywords.put("STATS", TokenType.IDENTIFIER);
        keywords.put("TABLESAMPLE", TokenType.IDENTIFIER);
        keywords.put("BUCKET", TokenType.IDENTIFIER);
        keywords.put("OUT", TokenType.IDENTIFIER);
        keywords.put("OF", TokenType.IDENTIFIER);
        keywords.put("PERCENT", TokenType.IDENTIFIER);
        keywords.put("ROWS", TokenType.IDENTIFIER);
        keywords.put("MSORT_BATCH_SIZE", TokenType.IDENTIFIER);
        keywords.put("SORTBY", TokenType.IDENTIFIER);
        keywords.put("DISTRIBUTEBY", TokenType.IDENTIFIER);
        keywords.put("REPARTITION", TokenType.IDENTIFIER);
        keywords.put("REWRITE_ENABLED", TokenType.IDENTIFIER);
        keywords.put("STORED_AS_DIRECTORIES", TokenType.IDENTIFIER);
        keywords.put("UNION", TokenType.IDENTIFIER);
        keywords.put("UNIONALL", TokenType.IDENTIFIER);

        // Hive functions
        keywords.put("EXPLODE", TokenType.IDENTIFIER);
        keywords.put("EXPLODE_OUTER", TokenType.IDENTIFIER);
        keywords.put("POSEXPLODE", TokenType.IDENTIFIER);
        keywords.put("POSEXPLODE_OUTER", TokenType.IDENTIFIER);
        keywords.put("INLINE", TokenType.IDENTIFIER);
        keywords.put("INLINE_OUTER", TokenType.IDENTIFIER);
        keywords.put("STACK", TokenType.IDENTIFIER);
        keywords.put("MAP_KEYS", TokenType.IDENTIFIER);
        keywords.put("MAP_VALUES", TokenType.IDENTIFIER);
        keywords.put("STRUCT", TokenType.IDENTIFIER);
        keywords.put("NAMED_STRUCT", TokenType.IDENTIFIER);
        keywords.put("ARRAY", TokenType.IDENTIFIER);
        keywords.put("MAP", TokenType.IDENTIFIER);
        keywords.put("GET_JSON_OBJECT", TokenType.IDENTIFIER);
        keywords.put("FROM_JSON", TokenType.IDENTIFIER);
        keywords.put("TO_JSON", TokenType.IDENTIFIER);
        keywords.put("JSON_TUPLE", TokenType.IDENTIFIER);
        keywords.put("SIZE", TokenType.IDENTIFIER);
        keywords.put("LENGTH", TokenType.IDENTIFIER);
        keywords.put("SUBSTR", TokenType.IDENTIFIER);
        keywords.put("SUBSTRING", TokenType.IDENTIFIER);
        keywords.put("SUBSTRING_INDEX", TokenType.IDENTIFIER);
        keywords.put("SPLIT", TokenType.IDENTIFIER);
        keywords.put("SPLIT_PART", TokenType.IDENTIFIER);
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
        keywords.put("FIND_IN_SET", TokenType.IDENTIFIER);
        keywords.put("INSTR", TokenType.IDENTIFIER);
        keywords.put("LOCATE", TokenType.IDENTIFIER);
        keywords.put("LPAD", TokenType.IDENTIFIER);
        keywords.put("RPAD", TokenType.IDENTIFIER);
        keywords.put("REVERSE", TokenType.IDENTIFIER);
        keywords.put("ASCII", TokenType.IDENTIFIER);
        keywords.put("CHR", TokenType.IDENTIFIER);
        keywords.put("CAST", TokenType.IDENTIFIER);
        keywords.put("COALESCE", TokenType.IDENTIFIER);
        keywords.put("NULLIF", TokenType.IDENTIFIER);
        keywords.put("IFNULL", TokenType.IDENTIFIER);
        keywords.put("IF", TokenType.IDENTIFIER);
        keywords.put("CASE", TokenType.IDENTIFIER);
        keywords.put("WHEN", TokenType.IDENTIFIER);
        keywords.put("THEN", TokenType.IDENTIFIER);
        keywords.put("ELSE", TokenType.IDENTIFIER);
        keywords.put("END", TokenType.IDENTIFIER);
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
        keywords.put("SIN", TokenType.IDENTIFIER);
        keywords.put("COS", TokenType.IDENTIFIER);
        keywords.put("TAN", TokenType.IDENTIFIER);
        keywords.put("ASIN", TokenType.IDENTIFIER);
        keywords.put("ACOS", TokenType.IDENTIFIER);
        keywords.put("ATAN", TokenType.IDENTIFIER);
        keywords.put("ATAN2", TokenType.IDENTIFIER);
        keywords.put("DEGREES", TokenType.IDENTIFIER);
        keywords.put("RADIANS", TokenType.IDENTIFIER);
        keywords.put("PI", TokenType.IDENTIFIER);
        keywords.put("SIGN", TokenType.IDENTIFIER);
        keywords.put("MOD", TokenType.IDENTIFIER);
        keywords.put("PMOD", TokenType.IDENTIFIER);
        keywords.put("POSITIVE", TokenType.IDENTIFIER);
        keywords.put("NEGATIVE", TokenType.IDENTIFIER);
        keywords.put("RAND", TokenType.IDENTIFIER);
        keywords.put("SHUFFLE", TokenType.IDENTIFIER);
        keywords.put("LEAST", TokenType.IDENTIFIER);
        keywords.put("GREATEST", TokenType.IDENTIFIER);
        keywords.put("UNHEX", TokenType.IDENTIFIER);
        keywords.put("HEX", TokenType.IDENTIFIER);
        keywords.put("FROM_UNIXTIME", TokenType.IDENTIFIER);
        keywords.put("UNIX_TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("TO_DATE", TokenType.IDENTIFIER);
        keywords.put("FROM_UTC_TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("TO_UTC_TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("CURRENT_DATE", TokenType.IDENTIFIER);
        keywords.put("CURRENT_TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("NOW", TokenType.IDENTIFIER);
        keywords.put("DATEDIFF", TokenType.IDENTIFIER);
        keywords.put("DATEADD", TokenType.IDENTIFIER);
        keywords.put("DATE_ADD", TokenType.IDENTIFIER);
        keywords.put("DATE_SUB", TokenType.IDENTIFIER);
        keywords.put("DATE_FORMAT", TokenType.IDENTIFIER);
        keywords.put("TRUNC", TokenType.IDENTIFIER);
        keywords.put("YEAR", TokenType.IDENTIFIER);
        keywords.put("MONTH", TokenType.IDENTIFIER);
        keywords.put("DAY", TokenType.IDENTIFIER);
        keywords.put("HOUR", TokenType.IDENTIFIER);
        keywords.put("MINUTE", TokenType.IDENTIFIER);
        keywords.put("SECOND", TokenType.IDENTIFIER);
        keywords.put("WEEK", TokenType.IDENTIFIER);
        keywords.put("QUARTER", TokenType.IDENTIFIER);
        keywords.put("WEEKDAY", TokenType.IDENTIFIER);
        keywords.put("DAYOFWEEK", TokenType.IDENTIFIER);
        keywords.put("DAYOFMONTH", TokenType.IDENTIFIER);
        keywords.put("DAYOFYEAR", TokenType.IDENTIFIER);
        keywords.put("WEEKOFYEAR", TokenType.IDENTIFIER);
        keywords.put("MONTHOFYEAR", TokenType.IDENTIFIER);

        return Trie.build(keywords);
    }
}
