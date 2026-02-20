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
package com.gtkcyber.sqlglot.dialects.oracle;

import com.gtkcyber.sqlglot.tokens.Tokenizer;
import com.gtkcyber.sqlglot.tokens.TokenType;
import com.gtkcyber.sqlglot.tokens.Trie;

import java.util.HashMap;
import java.util.Map;

/**
 * Oracle tokenizer.
 * Supports Oracle-specific syntax:
 * - Double quote identifiers
 * - Single quote strings with doubled quote escaping
 * - Oracle-specific keywords and functions
 * - PL/SQL keywords
 */
public class OracleTokenizer extends Tokenizer {
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

        // Oracle-specific keywords
        keywords.put("CONNECT", TokenType.IDENTIFIER);
        keywords.put("BY", TokenType.IDENTIFIER);
        keywords.put("PRIOR", TokenType.IDENTIFIER);
        keywords.put("NOCYCLE", TokenType.IDENTIFIER);
        keywords.put("START", TokenType.IDENTIFIER);
        keywords.put("WITH", TokenType.IDENTIFIER);
        keywords.put("ROWNUM", TokenType.IDENTIFIER);
        keywords.put("ROWID", TokenType.IDENTIFIER);
        keywords.put("SEQUENCE", TokenType.IDENTIFIER);
        keywords.put("INCREMENT", TokenType.IDENTIFIER);
        keywords.put("BY", TokenType.IDENTIFIER);
        keywords.put("START", TokenType.IDENTIFIER);
        keywords.put("WITH", TokenType.IDENTIFIER);
        keywords.put("MAXVALUE", TokenType.IDENTIFIER);
        keywords.put("MINVALUE", TokenType.IDENTIFIER);
        keywords.put("CYCLE", TokenType.IDENTIFIER);
        keywords.put("NOCYCLE", TokenType.IDENTIFIER);
        keywords.put("CACHE", TokenType.IDENTIFIER);
        keywords.put("NOCACHE", TokenType.IDENTIFIER);
        keywords.put("ORDER", TokenType.IDENTIFIER);
        keywords.put("NOORDER", TokenType.IDENTIFIER);
        keywords.put("CLUSTER", TokenType.IDENTIFIER);
        keywords.put("PARTITION", TokenType.IDENTIFIER);
        keywords.put("SUBPARTITION", TokenType.IDENTIFIER);
        keywords.put("PCTFREE", TokenType.IDENTIFIER);
        keywords.put("PCTUSED", TokenType.IDENTIFIER);
        keywords.put("INITRANS", TokenType.IDENTIFIER);
        keywords.put("MAXTRANS", TokenType.IDENTIFIER);
        keywords.put("TABLESPACE", TokenType.IDENTIFIER);
        keywords.put("STORAGE", TokenType.IDENTIFIER);
        keywords.put("LOGGING", TokenType.IDENTIFIER);
        keywords.put("NOLOGGING", TokenType.IDENTIFIER);
        keywords.put("COMPRESS", TokenType.IDENTIFIER);
        keywords.put("NOCOMPRESS", TokenType.IDENTIFIER);
        keywords.put("PARALLEL", TokenType.IDENTIFIER);
        keywords.put("NOPARALLEL", TokenType.IDENTIFIER);
        keywords.put("RECOVER", TokenType.IDENTIFIER);
        keywords.put("NORECOVERY", TokenType.IDENTIFIER);
        keywords.put("CACHE", TokenType.IDENTIFIER);
        keywords.put("NOCACHE", TokenType.IDENTIFIER);
        keywords.put("BUFFER_POOL", TokenType.IDENTIFIER);
        keywords.put("FLASH_CACHE", TokenType.IDENTIFIER);
        keywords.put("CELL_FLASH_CACHE", TokenType.IDENTIFIER);
        keywords.put("KEEP", TokenType.IDENTIFIER);
        keywords.put("RECYCLE", TokenType.IDENTIFIER);
        keywords.put("DEFAULT", TokenType.IDENTIFIER);

        // PL/SQL keywords
        keywords.put("DECLARE", TokenType.IDENTIFIER);
        keywords.put("BEGIN", TokenType.IDENTIFIER);
        keywords.put("END", TokenType.IDENTIFIER);
        keywords.put("EXCEPTION", TokenType.IDENTIFIER);
        keywords.put("PROCEDURE", TokenType.IDENTIFIER);
        keywords.put("FUNCTION", TokenType.IDENTIFIER);
        keywords.put("PACKAGE", TokenType.IDENTIFIER);
        keywords.put("TYPE", TokenType.IDENTIFIER);
        keywords.put("RECORD", TokenType.IDENTIFIER);
        keywords.put("TABLE", TokenType.IDENTIFIER);
        keywords.put("IS", TokenType.IDENTIFIER);
        keywords.put("AS", TokenType.IDENTIFIER);
        keywords.put("IF", TokenType.IDENTIFIER);
        keywords.put("THEN", TokenType.IDENTIFIER);
        keywords.put("ELSE", TokenType.IDENTIFIER);
        keywords.put("ELSIF", TokenType.IDENTIFIER);
        keywords.put("LOOP", TokenType.IDENTIFIER);
        keywords.put("WHILE", TokenType.IDENTIFIER);
        keywords.put("FOR", TokenType.IDENTIFIER);
        keywords.put("IN", TokenType.IDENTIFIER);
        keywords.put("EXIT", TokenType.IDENTIFIER);
        keywords.put("CONTINUE", TokenType.IDENTIFIER);
        keywords.put("RETURN", TokenType.IDENTIFIER);
        keywords.put("PRAGMA", TokenType.IDENTIFIER);
        keywords.put("RAISE", TokenType.IDENTIFIER);
        keywords.put("WHEN", TokenType.IDENTIFIER);
        keywords.put("OTHERS", TokenType.IDENTIFIER);
        keywords.put("CURSOR", TokenType.IDENTIFIER);
        keywords.put("FETCH", TokenType.IDENTIFIER);
        keywords.put("OPEN", TokenType.IDENTIFIER);
        keywords.put("CLOSE", TokenType.IDENTIFIER);

        // Oracle-specific functions
        keywords.put("SYSDATE", TokenType.IDENTIFIER);
        keywords.put("SYSTIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("CURRENT_DATE", TokenType.IDENTIFIER);
        keywords.put("CURRENT_TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("TRUNC", TokenType.IDENTIFIER);
        keywords.put("ROUND", TokenType.IDENTIFIER);
        keywords.put("TO_DATE", TokenType.IDENTIFIER);
        keywords.put("TO_CHAR", TokenType.IDENTIFIER);
        keywords.put("TO_NUMBER", TokenType.IDENTIFIER);
        keywords.put("TO_TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("CAST", TokenType.IDENTIFIER);
        keywords.put("NVL", TokenType.IDENTIFIER);
        keywords.put("NVL2", TokenType.IDENTIFIER);
        keywords.put("NULLIF", TokenType.IDENTIFIER);
        keywords.put("COALESCE", TokenType.IDENTIFIER);
        keywords.put("DECODE", TokenType.IDENTIFIER);
        keywords.put("CASE", TokenType.IDENTIFIER);
        keywords.put("WHEN", TokenType.IDENTIFIER);
        keywords.put("THEN", TokenType.IDENTIFIER);
        keywords.put("ELSE", TokenType.IDENTIFIER);
        keywords.put("END", TokenType.IDENTIFIER);
        keywords.put("SUBSTR", TokenType.IDENTIFIER);
        keywords.put("SUBSTRING", TokenType.IDENTIFIER);
        keywords.put("INSTR", TokenType.IDENTIFIER);
        keywords.put("LENGTH", TokenType.IDENTIFIER);
        keywords.put("CONCAT", TokenType.IDENTIFIER);
        keywords.put("LPAD", TokenType.IDENTIFIER);
        keywords.put("RPAD", TokenType.IDENTIFIER);
        keywords.put("LTRIM", TokenType.IDENTIFIER);
        keywords.put("RTRIM", TokenType.IDENTIFIER);
        keywords.put("TRIM", TokenType.IDENTIFIER);
        keywords.put("UPPER", TokenType.IDENTIFIER);
        keywords.put("LOWER", TokenType.IDENTIFIER);
        keywords.put("INITCAP", TokenType.IDENTIFIER);
        keywords.put("REPLACE", TokenType.IDENTIFIER);
        keywords.put("TRANSLATE", TokenType.IDENTIFIER);
        keywords.put("REGEXP_LIKE", TokenType.IDENTIFIER);
        keywords.put("REGEXP_INSTR", TokenType.IDENTIFIER);
        keywords.put("REGEXP_SUBSTR", TokenType.IDENTIFIER);
        keywords.put("REGEXP_REPLACE", TokenType.IDENTIFIER);
        keywords.put("SOUNDEX", TokenType.IDENTIFIER);
        keywords.put("METAPHONE", TokenType.IDENTIFIER);
        keywords.put("ABS", TokenType.IDENTIFIER);
        keywords.put("POWER", TokenType.IDENTIFIER);
        keywords.put("SQRT", TokenType.IDENTIFIER);
        keywords.put("SIGN", TokenType.IDENTIFIER);
        keywords.put("CEIL", TokenType.IDENTIFIER);
        keywords.put("FLOOR", TokenType.IDENTIFIER);
        keywords.put("MOD", TokenType.IDENTIFIER);
        keywords.put("REMAINDER", TokenType.IDENTIFIER);
        keywords.put("SIN", TokenType.IDENTIFIER);
        keywords.put("COS", TokenType.IDENTIFIER);
        keywords.put("TAN", TokenType.IDENTIFIER);
        keywords.put("ASIN", TokenType.IDENTIFIER);
        keywords.put("ACOS", TokenType.IDENTIFIER);
        keywords.put("ATAN", TokenType.IDENTIFIER);
        keywords.put("EXP", TokenType.IDENTIFIER);
        keywords.put("LN", TokenType.IDENTIFIER);
        keywords.put("LOG", TokenType.IDENTIFIER);
        keywords.put("BITAND", TokenType.IDENTIFIER);
        keywords.put("APPROX_RANK", TokenType.IDENTIFIER);
        keywords.put("PERCENT_RANK", TokenType.IDENTIFIER);
        keywords.put("PERCENTILE_CONT", TokenType.IDENTIFIER);
        keywords.put("PERCENTILE_DISC", TokenType.IDENTIFIER);

        return Trie.build(keywords);
    }
}
