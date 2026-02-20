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
package com.gtkcyber.sqlglot.dialects.mariadb;

import com.gtkcyber.sqlglot.tokens.Tokenizer;
import com.gtkcyber.sqlglot.tokens.TokenType;
import com.gtkcyber.sqlglot.tokens.Trie;

import java.util.HashMap;
import java.util.Map;

/**
 * MariaDB tokenizer.
 * Supports MariaDB-specific syntax:
 * - Backtick identifiers
 * - Double quote identifiers
 * - Single quote strings
 * - MariaDB-specific keywords and functions
 * - Extended data type keywords
 */
public class MariadbTokenizer extends Tokenizer {
    @Override
    protected Map<String, Character> getIdentifierQuotes() {
        Map<String, Character> quotes = new HashMap<>();
        quotes.put("`", '`');    // Backticks (primary, same as MySQL)
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

        // MariaDB-specific keywords
        keywords.put("SEQUENCE", TokenType.IDENTIFIER);
        keywords.put("INCREMENT", TokenType.IDENTIFIER);
        keywords.put("BY", TokenType.IDENTIFIER);
        keywords.put("START", TokenType.IDENTIFIER);
        keywords.put("WITH", TokenType.IDENTIFIER);
        keywords.put("MINVALUE", TokenType.IDENTIFIER);
        keywords.put("MAXVALUE", TokenType.IDENTIFIER);
        keywords.put("CYCLE", TokenType.IDENTIFIER);
        keywords.put("NOCYCLE", TokenType.IDENTIFIER);
        keywords.put("CACHE", TokenType.IDENTIFIER);
        keywords.put("NOCACHE", TokenType.IDENTIFIER);
        keywords.put("GENERATED", TokenType.IDENTIFIER);
        keywords.put("ALWAYS", TokenType.IDENTIFIER);
        keywords.put("STORED", TokenType.IDENTIFIER);
        keywords.put("VIRTUAL", TokenType.IDENTIFIER);
        keywords.put("PERSISTENT", TokenType.IDENTIFIER);
        keywords.put("AS", TokenType.IDENTIFIER);
        keywords.put("DYNAMIC", TokenType.IDENTIFIER);
        keywords.put("COLUMN_GET", TokenType.IDENTIFIER);
        keywords.put("COLUMN_SET", TokenType.IDENTIFIER);
        keywords.put("COLUMN_ADD", TokenType.IDENTIFIER);
        keywords.put("COLUMN_DELETE", TokenType.IDENTIFIER);
        keywords.put("COLUMN_EXISTS", TokenType.IDENTIFIER);
        keywords.put("COLUMN_JSON", TokenType.IDENTIFIER);
        keywords.put("REPLACE", TokenType.IDENTIFIER);
        keywords.put("ON", TokenType.IDENTIFIER);
        keywords.put("DUPLICATE", TokenType.IDENTIFIER);
        keywords.put("KEY", TokenType.IDENTIFIER);
        keywords.put("UPDATE", TokenType.IDENTIFIER);
        keywords.put("FORCE", TokenType.IDENTIFIER);
        keywords.put("USE", TokenType.IDENTIFIER);
        keywords.put("INDEX", TokenType.IDENTIFIER);
        keywords.put("IGNORE", TokenType.IDENTIFIER);
        keywords.put("BULK_INSERT_BUFFER_SIZE", TokenType.IDENTIFIER);
        keywords.put("WITH", TokenType.IDENTIFIER);
        keywords.put("CHECK", TokenType.IDENTIFIER);
        keywords.put("PARSE_VCOL_EXPR", TokenType.IDENTIFIER);
        keywords.put("USING", TokenType.IDENTIFIER);
        keywords.put("BTREE", TokenType.IDENTIFIER);
        keywords.put("HASH", TokenType.IDENTIFIER);
        keywords.put("RTREE", TokenType.IDENTIFIER);
        keywords.put("FULLTEXT", TokenType.IDENTIFIER);
        keywords.put("SPATIAL", TokenType.IDENTIFIER);
        keywords.put("ENGINE", TokenType.IDENTIFIER);
        keywords.put("INNODB", TokenType.IDENTIFIER);
        keywords.put("MYISAM", TokenType.IDENTIFIER);
        keywords.put("MEMORY", TokenType.IDENTIFIER);
        keywords.put("ARIA", TokenType.IDENTIFIER);
        keywords.put("S3", TokenType.IDENTIFIER);
        keywords.put("SPIDER", TokenType.IDENTIFIER);
        keywords.put("PARTITION", TokenType.IDENTIFIER);
        keywords.put("BY", TokenType.IDENTIFIER);
        keywords.put("RANGE", TokenType.IDENTIFIER);
        keywords.put("LIST", TokenType.IDENTIFIER);
        keywords.put("HASH", TokenType.IDENTIFIER);
        keywords.put("LINEAR", TokenType.IDENTIFIER);
        keywords.put("KEY", TokenType.IDENTIFIER);
        keywords.put("COLUMNS", TokenType.IDENTIFIER);
        keywords.put("SUBPARTITION", TokenType.IDENTIFIER);
        keywords.put("EXPLAIN", TokenType.IDENTIFIER);
        keywords.put("FORMAT", TokenType.IDENTIFIER);
        keywords.put("JSON", TokenType.IDENTIFIER);
        keywords.put("TRADITIONAL", TokenType.IDENTIFIER);
        keywords.put("ANALYZE", TokenType.IDENTIFIER);
        keywords.put("FOR", TokenType.IDENTIFIER);
        keywords.put("CONNECTION", TokenType.IDENTIFIER);
        keywords.put("ALL", TokenType.IDENTIFIER);
        keywords.put("START", TokenType.IDENTIFIER);
        keywords.put("SLAVE", TokenType.IDENTIFIER);
        keywords.put("REPLICATION", TokenType.IDENTIFIER);
        keywords.put("PARALLEL", TokenType.IDENTIFIER);
        keywords.put("THREAD", TokenType.IDENTIFIER);

        // MariaDB functions
        keywords.put("JSON_ARRAY", TokenType.IDENTIFIER);
        keywords.put("JSON_OBJECT", TokenType.IDENTIFIER);
        keywords.put("JSON_EXTRACT", TokenType.IDENTIFIER);
        keywords.put("JSON_UNQUOTE", TokenType.IDENTIFIER);
        keywords.put("JSON_QUERY", TokenType.IDENTIFIER);
        keywords.put("JSON_VALUE", TokenType.IDENTIFIER);
        keywords.put("JSON_SET", TokenType.IDENTIFIER);
        keywords.put("JSON_INSERT", TokenType.IDENTIFIER);
        keywords.put("JSON_REPLACE", TokenType.IDENTIFIER);
        keywords.put("JSON_REMOVE", TokenType.IDENTIFIER);
        keywords.put("JSON_ARRAY_LENGTH", TokenType.IDENTIFIER);
        keywords.put("JSON_LENGTH", TokenType.IDENTIFIER);
        keywords.put("JSON_VALID", TokenType.IDENTIFIER);
        keywords.put("JSON_CONTAINS", TokenType.IDENTIFIER);
        keywords.put("JSON_CONTAINS_PATH", TokenType.IDENTIFIER);
        keywords.put("JSON_SEARCH", TokenType.IDENTIFIER);
        keywords.put("JSON_KEYS", TokenType.IDENTIFIER);
        keywords.put("JSON_DEPTH", TokenType.IDENTIFIER);
        keywords.put("JSON_TYPE", TokenType.IDENTIFIER);
        keywords.put("JSON_PRETTY", TokenType.IDENTIFIER);
        keywords.put("JSON_QUOTE", TokenType.IDENTIFIER);
        keywords.put("JSON_APPEND", TokenType.IDENTIFIER);
        keywords.put("JSON_ARRAY_APPEND", TokenType.IDENTIFIER);
        keywords.put("JSON_COMPACT", TokenType.IDENTIFIER);
        keywords.put("JSON_DETAILED", TokenType.IDENTIFIER);
        keywords.put("COALESCE", TokenType.IDENTIFIER);
        keywords.put("NULLIF", TokenType.IDENTIFIER);
        keywords.put("IFNULL", TokenType.IDENTIFIER);
        keywords.put("IF", TokenType.IDENTIFIER);
        keywords.put("CASE", TokenType.IDENTIFIER);
        keywords.put("WHEN", TokenType.IDENTIFIER);
        keywords.put("THEN", TokenType.IDENTIFIER);
        keywords.put("ELSE", TokenType.IDENTIFIER);
        keywords.put("END", TokenType.IDENTIFIER);
        keywords.put("CAST", TokenType.IDENTIFIER);
        keywords.put("CONVERT", TokenType.IDENTIFIER);
        keywords.put("FULLTEXT", TokenType.IDENTIFIER);
        keywords.put("MATCH", TokenType.IDENTIFIER);
        keywords.put("AGAINST", TokenType.IDENTIFIER);
        keywords.put("IN", TokenType.IDENTIFIER);
        keywords.put("BOOLEAN", TokenType.IDENTIFIER);
        keywords.put("MODE", TokenType.IDENTIFIER);
        keywords.put("WITH", TokenType.IDENTIFIER);
        keywords.put("QUERY", TokenType.IDENTIFIER);
        keywords.put("EXPANSION", TokenType.IDENTIFIER);
        keywords.put("COLUMN_GET", TokenType.IDENTIFIER);
        keywords.put("COLUMN_SET", TokenType.IDENTIFIER);
        keywords.put("SUBSTR", TokenType.IDENTIFIER);
        keywords.put("SUBSTRING", TokenType.IDENTIFIER);
        keywords.put("LENGTH", TokenType.IDENTIFIER);
        keywords.put("TRIM", TokenType.IDENTIFIER);
        keywords.put("LTRIM", TokenType.IDENTIFIER);
        keywords.put("RTRIM", TokenType.IDENTIFIER);
        keywords.put("UPPER", TokenType.IDENTIFIER);
        keywords.put("LOWER", TokenType.IDENTIFIER);
        keywords.put("CONCAT", TokenType.IDENTIFIER);
        keywords.put("CONCAT_WS", TokenType.IDENTIFIER);
        keywords.put("REPLACE", TokenType.IDENTIFIER);
        keywords.put("INSTR", TokenType.IDENTIFIER);
        keywords.put("LOCATE", TokenType.IDENTIFIER);
        keywords.put("FIND_IN_SET", TokenType.IDENTIFIER);
        keywords.put("LPAD", TokenType.IDENTIFIER);
        keywords.put("RPAD", TokenType.IDENTIFIER);
        keywords.put("REPEAT", TokenType.IDENTIFIER);
        keywords.put("REVERSE", TokenType.IDENTIFIER);
        keywords.put("INSERT", TokenType.IDENTIFIER);
        keywords.put("CHR", TokenType.IDENTIFIER);
        keywords.put("ASCII", TokenType.IDENTIFIER);
        keywords.put("HEX", TokenType.IDENTIFIER);
        keywords.put("UNHEX", TokenType.IDENTIFIER);
        keywords.put("FORMAT", TokenType.IDENTIFIER);
        keywords.put("SOUNDEX", TokenType.IDENTIFIER);
        keywords.put("LEVENSHTEIN", TokenType.IDENTIFIER);
        keywords.put("LEVENSHTEIN_RATIO", TokenType.IDENTIFIER);

        // Date/time functions
        keywords.put("CURDATE", TokenType.IDENTIFIER);
        keywords.put("CURRENT_DATE", TokenType.IDENTIFIER);
        keywords.put("CURTIME", TokenType.IDENTIFIER);
        keywords.put("CURRENT_TIME", TokenType.IDENTIFIER);
        keywords.put("NOW", TokenType.IDENTIFIER);
        keywords.put("SYSDATE", TokenType.IDENTIFIER);
        keywords.put("UTC_DATE", TokenType.IDENTIFIER);
        keywords.put("UTC_TIME", TokenType.IDENTIFIER);
        keywords.put("UTC_TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("UNIX_TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("FROM_UNIXTIME", TokenType.IDENTIFIER);
        keywords.put("DATE_FORMAT", TokenType.IDENTIFIER);
        keywords.put("TIME_FORMAT", TokenType.IDENTIFIER);
        keywords.put("DATE_ADD", TokenType.IDENTIFIER);
        keywords.put("DATE_SUB", TokenType.IDENTIFIER);
        keywords.put("ADDDATE", TokenType.IDENTIFIER);
        keywords.put("SUBDATE", TokenType.IDENTIFIER);
        keywords.put("DATE_DIFF", TokenType.IDENTIFIER);
        keywords.put("DATEDIFF", TokenType.IDENTIFIER);
        keywords.put("TIMEDIFF", TokenType.IDENTIFIER);
        keywords.put("TO_DAYS", TokenType.IDENTIFIER);
        keywords.put("FROM_DAYS", TokenType.IDENTIFIER);
        keywords.put("DAYNAME", TokenType.IDENTIFIER);
        keywords.put("MONTHNAME", TokenType.IDENTIFIER);
        keywords.put("DAYOFWEEK", TokenType.IDENTIFIER);
        keywords.put("WEEKDAY", TokenType.IDENTIFIER);
        keywords.put("DAYOFMONTH", TokenType.IDENTIFIER);
        keywords.put("DAYOFYEAR", TokenType.IDENTIFIER);
        keywords.put("WEEK", TokenType.IDENTIFIER);
        keywords.put("WEEKOFYEAR", TokenType.IDENTIFIER);
        keywords.put("YEARWEEK", TokenType.IDENTIFIER);
        keywords.put("YEAR", TokenType.IDENTIFIER);
        keywords.put("MONTH", TokenType.IDENTIFIER);
        keywords.put("MONTHOFYEAR", TokenType.IDENTIFIER);
        keywords.put("QUARTER", TokenType.IDENTIFIER);
        keywords.put("DAY", TokenType.IDENTIFIER);
        keywords.put("HOUR", TokenType.IDENTIFIER);
        keywords.put("MINUTE", TokenType.IDENTIFIER);
        keywords.put("SECOND", TokenType.IDENTIFIER);
        keywords.put("MICROSECOND", TokenType.IDENTIFIER);
        keywords.put("EXTRACT", TokenType.IDENTIFIER);
        keywords.put("MAKEDATE", TokenType.IDENTIFIER);
        keywords.put("MAKETIME", TokenType.IDENTIFIER);
        keywords.put("PERIOD_ADD", TokenType.IDENTIFIER);
        keywords.put("PERIOD_DIFF", TokenType.IDENTIFIER);
        keywords.put("LAST_DAY", TokenType.IDENTIFIER);
        keywords.put("NEXT_DAY", TokenType.IDENTIFIER);
        keywords.put("STR_TO_DATE", TokenType.IDENTIFIER);

        // Math functions
        keywords.put("ABS", TokenType.IDENTIFIER);
        keywords.put("ROUND", TokenType.IDENTIFIER);
        keywords.put("CEIL", TokenType.IDENTIFIER);
        keywords.put("FLOOR", TokenType.IDENTIFIER);
        keywords.put("TRUNCATE", TokenType.IDENTIFIER);
        keywords.put("SQRT", TokenType.IDENTIFIER);
        keywords.put("POWER", TokenType.IDENTIFIER);
        keywords.put("EXP", TokenType.IDENTIFIER);
        keywords.put("LN", TokenType.IDENTIFIER);
        keywords.put("LOG", TokenType.IDENTIFIER);
        keywords.put("LOG2", TokenType.IDENTIFIER);
        keywords.put("LOG10", TokenType.IDENTIFIER);
        keywords.put("PI", TokenType.IDENTIFIER);
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
        keywords.put("SIGN", TokenType.IDENTIFIER);
        keywords.put("MOD", TokenType.IDENTIFIER);
        keywords.put("RAND", TokenType.IDENTIFIER);
        keywords.put("LEAST", TokenType.IDENTIFIER);
        keywords.put("GREATEST", TokenType.IDENTIFIER);

        return Trie.build(keywords);
    }
}
