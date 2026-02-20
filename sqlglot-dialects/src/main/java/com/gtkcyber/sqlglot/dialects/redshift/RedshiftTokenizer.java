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
package com.gtkcyber.sqlglot.dialects.redshift;

import com.gtkcyber.sqlglot.tokens.Tokenizer;
import com.gtkcyber.sqlglot.tokens.TokenType;
import com.gtkcyber.sqlglot.tokens.Trie;

import java.util.HashMap;
import java.util.Map;

/**
 * Redshift tokenizer.
 * Supports Redshift-specific syntax:
 * - Double quote identifiers
 * - Single quote strings
 * - Redshift-specific keywords and functions
 * - Data distribution and sorting keywords
 */
public class RedshiftTokenizer extends Tokenizer {
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

        // Redshift-specific keywords
        keywords.put("DISTKEY", TokenType.IDENTIFIER);
        keywords.put("SORTKEY", TokenType.IDENTIFIER);
        keywords.put("ENCODE", TokenType.IDENTIFIER);
        keywords.put("COMPOUND", TokenType.IDENTIFIER);
        keywords.put("INTERLEAVED", TokenType.IDENTIFIER);
        keywords.put("DISTSTYLE", TokenType.IDENTIFIER);
        keywords.put("EVEN", TokenType.IDENTIFIER);
        keywords.put("KEY", TokenType.IDENTIFIER);
        keywords.put("ALL", TokenType.IDENTIFIER);
        keywords.put("BACKUP", TokenType.IDENTIFIER);
        keywords.put("NOBACKUP", TokenType.IDENTIFIER);
        keywords.put("ENCODE", TokenType.IDENTIFIER);
        keywords.put("ENCODE", TokenType.IDENTIFIER);
        keywords.put("RAW", TokenType.IDENTIFIER);
        keywords.put("AZ64", TokenType.IDENTIFIER);
        keywords.put("ZSTD", TokenType.IDENTIFIER);
        keywords.put("DEFLATE", TokenType.IDENTIFIER);
        keywords.put("DELTA", TokenType.IDENTIFIER);
        keywords.put("DELTA32K", TokenType.IDENTIFIER);
        keywords.put("RUNLENGTH", TokenType.IDENTIFIER);
        keywords.put("BYTEDICT", TokenType.IDENTIFIER);
        keywords.put("MOSTLY8", TokenType.IDENTIFIER);
        keywords.put("MOSTLY16", TokenType.IDENTIFIER);
        keywords.put("MOSTLY32", TokenType.IDENTIFIER);
        keywords.put("LZO", TokenType.IDENTIFIER);
        keywords.put("BZIP2", TokenType.IDENTIFIER);
        keywords.put("PGLZ", TokenType.IDENTIFIER);
        keywords.put("TEXT255", TokenType.IDENTIFIER);
        keywords.put("TEXT32K", TokenType.IDENTIFIER);
        keywords.put("BZIP2", TokenType.IDENTIFIER);
        keywords.put("PGLZ", TokenType.IDENTIFIER);
        keywords.put("VACUUM", TokenType.IDENTIFIER);
        keywords.put("ANALYZE", TokenType.IDENTIFIER);
        keywords.put("COPY", TokenType.IDENTIFIER);
        keywords.put("UNLOAD", TokenType.IDENTIFIER);
        keywords.put("FROM", TokenType.IDENTIFIER);
        keywords.put("TO", TokenType.IDENTIFIER);
        keywords.put("WITH", TokenType.IDENTIFIER);
        keywords.put("CREDENTIALS", TokenType.IDENTIFIER);
        keywords.put("IAM_ROLE", TokenType.IDENTIFIER);
        keywords.put("ACCESS_KEY_ID", TokenType.IDENTIFIER);
        keywords.put("SECRET_ACCESS_KEY", TokenType.IDENTIFIER);
        keywords.put("DELIMITER", TokenType.IDENTIFIER);
        keywords.put("HEADER", TokenType.IDENTIFIER);
        keywords.put("IGNOREHEADER", TokenType.IDENTIFIER);
        keywords.put("NULL", TokenType.IDENTIFIER);
        keywords.put("EMPTYASNULL", TokenType.IDENTIFIER);
        keywords.put("BLANKSASNULL", TokenType.IDENTIFIER);
        keywords.put("ESCAPE", TokenType.IDENTIFIER);
        keywords.put("QUOTE", TokenType.IDENTIFIER);
        keywords.put("ACCEPTANYDATE", TokenType.IDENTIFIER);
        keywords.put("DATEFORMAT", TokenType.IDENTIFIER);
        keywords.put("TIMEFORMAT", TokenType.IDENTIFIER);
        keywords.put("MANIFEST", TokenType.IDENTIFIER);
        keywords.put("STATUPDATE", TokenType.IDENTIFIER);
        keywords.put("COMPUPDATE", TokenType.IDENTIFIER);
        keywords.put("ENCODING", TokenType.IDENTIFIER);
        keywords.put("GZIP", TokenType.IDENTIFIER);
        keywords.put("BZIP2", TokenType.IDENTIFIER);
        keywords.put("LZOP", TokenType.IDENTIFIER);
        keywords.put("PARQUET", TokenType.IDENTIFIER);
        keywords.put("ORC", TokenType.IDENTIFIER);
        keywords.put("JSON", TokenType.IDENTIFIER);
        keywords.put("AVRO", TokenType.IDENTIFIER);
        keywords.put("CSV", TokenType.IDENTIFIER);
        keywords.put("ROWCOUNT", TokenType.IDENTIFIER);
        keywords.put("STL", TokenType.IDENTIFIER);
        keywords.put("SVL", TokenType.IDENTIFIER);
        keywords.put("SYS", TokenType.IDENTIFIER);
        keywords.put("SCHEMA", TokenType.IDENTIFIER);
        keywords.put("EXTERNAL", TokenType.IDENTIFIER);
        keywords.put("SPECTRUM", TokenType.IDENTIFIER);
        keywords.put("REDSHIFT_SPECTRUM", TokenType.IDENTIFIER);
        keywords.put("ATHENA", TokenType.IDENTIFIER);

        // Redshift functions
        keywords.put("LISTAGG", TokenType.IDENTIFIER);
        keywords.put("WITHIN", TokenType.IDENTIFIER);
        keywords.put("GROUP", TokenType.IDENTIFIER);
        keywords.put("MEDIAN", TokenType.IDENTIFIER);
        keywords.put("PERCENTILE_CONT", TokenType.IDENTIFIER);
        keywords.put("PERCENTILE_DISC", TokenType.IDENTIFIER);
        keywords.put("STDDEV", TokenType.IDENTIFIER);
        keywords.put("STDDEV_POP", TokenType.IDENTIFIER);
        keywords.put("STDDEV_SAMP", TokenType.IDENTIFIER);
        keywords.put("VARIANCE", TokenType.IDENTIFIER);
        keywords.put("VAR_POP", TokenType.IDENTIFIER);
        keywords.put("VAR_SAMP", TokenType.IDENTIFIER);
        keywords.put("RATIO_TO_REPORT", TokenType.IDENTIFIER);
        keywords.put("FIRST_VALUE", TokenType.IDENTIFIER);
        keywords.put("LAST_VALUE", TokenType.IDENTIFIER);
        keywords.put("LAG", TokenType.IDENTIFIER);
        keywords.put("LEAD", TokenType.IDENTIFIER);
        keywords.put("ROW_NUMBER", TokenType.IDENTIFIER);
        keywords.put("RANK", TokenType.IDENTIFIER);
        keywords.put("DENSE_RANK", TokenType.IDENTIFIER);
        keywords.put("NTILE", TokenType.IDENTIFIER);
        keywords.put("CUME_DIST", TokenType.IDENTIFIER);
        keywords.put("PERCENT_RANK", TokenType.IDENTIFIER);
        keywords.put("COUNT", TokenType.IDENTIFIER);
        keywords.put("SUM", TokenType.IDENTIFIER);
        keywords.put("AVG", TokenType.IDENTIFIER);
        keywords.put("MIN", TokenType.IDENTIFIER);
        keywords.put("MAX", TokenType.IDENTIFIER);
        keywords.put("APPROX_PERCENTILE", TokenType.IDENTIFIER);
        keywords.put("JSON_EXTRACT_PATH_TEXT", TokenType.IDENTIFIER);
        keywords.put("JSON_EXTRACT_PATH", TokenType.IDENTIFIER);
        keywords.put("FLATTEN", TokenType.IDENTIFIER);
        keywords.put("JSON_ARRAY_LENGTH", TokenType.IDENTIFIER);
        keywords.put("JSON_PARSE", TokenType.IDENTIFIER);
        keywords.put("JSON_SERIALIZE", TokenType.IDENTIFIER);
        keywords.put("REGEXP_INSTR", TokenType.IDENTIFIER);
        keywords.put("REGEXP_SUBSTR", TokenType.IDENTIFIER);
        keywords.put("REGEXP_REPLACE", TokenType.IDENTIFIER);
        keywords.put("REGEXP_LIKE", TokenType.IDENTIFIER);
        keywords.put("TRANSLATE", TokenType.IDENTIFIER);
        keywords.put("REPLACE", TokenType.IDENTIFIER);
        keywords.put("SUBSTR", TokenType.IDENTIFIER);
        keywords.put("SUBSTRING", TokenType.IDENTIFIER);
        keywords.put("LENGTH", TokenType.IDENTIFIER);
        keywords.put("LTRIM", TokenType.IDENTIFIER);
        keywords.put("RTRIM", TokenType.IDENTIFIER);
        keywords.put("TRIM", TokenType.IDENTIFIER);
        keywords.put("UPPER", TokenType.IDENTIFIER);
        keywords.put("LOWER", TokenType.IDENTIFIER);
        keywords.put("REVERSE", TokenType.IDENTIFIER);
        keywords.put("CONCAT", TokenType.IDENTIFIER);
        keywords.put("CHR", TokenType.IDENTIFIER);
        keywords.put("ASCII", TokenType.IDENTIFIER);
        keywords.put("INITCAP", TokenType.IDENTIFIER);
        keywords.put("GETBIT", TokenType.IDENTIFIER);
        keywords.put("SETBIT", TokenType.IDENTIFIER);
        keywords.put("BITCOUNT", TokenType.IDENTIFIER);

        // Date/time functions
        keywords.put("CURRENT_DATE", TokenType.IDENTIFIER);
        keywords.put("CURRENT_TIME", TokenType.IDENTIFIER);
        keywords.put("CURRENT_TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("NOW", TokenType.IDENTIFIER);
        keywords.put("GETDATE", TokenType.IDENTIFIER);
        keywords.put("DATEADD", TokenType.IDENTIFIER);
        keywords.put("DATEDIFF", TokenType.IDENTIFIER);
        keywords.put("DATE_TRUNC", TokenType.IDENTIFIER);
        keywords.put("DATE_PART", TokenType.IDENTIFIER);
        keywords.put("EXTRACT", TokenType.IDENTIFIER);
        keywords.put("TO_DATE", TokenType.IDENTIFIER);
        keywords.put("TO_CHAR", TokenType.IDENTIFIER);
        keywords.put("TO_TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("TRUNC", TokenType.IDENTIFIER);
        keywords.put("EPOCH", TokenType.IDENTIFIER);
        keywords.put("LAST_DAY", TokenType.IDENTIFIER);
        keywords.put("NEXT_DAY", TokenType.IDENTIFIER);

        return Trie.build(keywords);
    }
}
