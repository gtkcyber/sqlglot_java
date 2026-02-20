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
package com.gtkcyber.sqlglot.dialects.mssql;

import com.gtkcyber.sqlglot.tokens.Tokenizer;
import com.gtkcyber.sqlglot.tokens.TokenType;
import com.gtkcyber.sqlglot.tokens.Trie;

import java.util.HashMap;
import java.util.Map;

/**
 * SQL Server tokenizer.
 * Supports T-SQL syntax:
 * - Square bracket identifiers
 * - Double quote identifiers
 * - Single quote strings with doubled quote escaping
 * - Variable syntax: @variable
 * - SQL Server-specific keywords and functions
 */
public class MssqlTokenizer extends Tokenizer {
    @Override
    protected Map<String, Character> getIdentifierQuotes() {
        Map<String, Character> quotes = new HashMap<>();
        quotes.put("[", ']');    // Square brackets (primary)
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

        // T-SQL specific keywords
        keywords.put("GO", TokenType.IDENTIFIER);
        keywords.put("IDENTITY", TokenType.IDENTIFIER);
        keywords.put("NOLOCK", TokenType.IDENTIFIER);
        keywords.put("NOHOLDLOCK", TokenType.IDENTIFIER);
        keywords.put("READCOMMITTED", TokenType.IDENTIFIER);
        keywords.put("SNAPSHOT", TokenType.IDENTIFIER);
        keywords.put("READUNCOMMITTED", TokenType.IDENTIFIER);
        keywords.put("REPEATABLEREAD", TokenType.IDENTIFIER);
        keywords.put("SERIALIZABLE", TokenType.IDENTIFIER);
        keywords.put("TABLOCK", TokenType.IDENTIFIER);
        keywords.put("UPDLOCK", TokenType.IDENTIFIER);
        keywords.put("XLOCK", TokenType.IDENTIFIER);
        keywords.put("SLOCK", TokenType.IDENTIFIER);
        keywords.put("PAGLOCK", TokenType.IDENTIFIER);
        keywords.put("TABLOCKX", TokenType.IDENTIFIER);
        keywords.put("CLUSTERED", TokenType.IDENTIFIER);
        keywords.put("NONCLUSTERED", TokenType.IDENTIFIER);
        keywords.put("CONSTRAINT", TokenType.IDENTIFIER);
        keywords.put("FOREIGN", TokenType.IDENTIFIER);
        keywords.put("PRIMARY", TokenType.IDENTIFIER);
        keywords.put("KEY", TokenType.IDENTIFIER);
        keywords.put("CHECK", TokenType.IDENTIFIER);
        keywords.put("DEFAULT", TokenType.IDENTIFIER);
        keywords.put("UNIQUE", TokenType.IDENTIFIER);
        keywords.put("INDEX", TokenType.IDENTIFIER);
        keywords.put("TRIGGER", TokenType.IDENTIFIER);
        keywords.put("PROCEDURE", TokenType.IDENTIFIER);
        keywords.put("FUNCTION", TokenType.IDENTIFIER);
        keywords.put("VIEW", TokenType.IDENTIFIER);
        keywords.put("SCHEMA", TokenType.IDENTIFIER);
        keywords.put("DATABASE", TokenType.IDENTIFIER);
        keywords.put("TABLE", TokenType.IDENTIFIER);
        keywords.put("DECLARE", TokenType.IDENTIFIER);
        keywords.put("SET", TokenType.IDENTIFIER);
        keywords.put("BEGIN", TokenType.IDENTIFIER);
        keywords.put("END", TokenType.IDENTIFIER);
        keywords.put("IF", TokenType.IDENTIFIER);
        keywords.put("ELSE", TokenType.IDENTIFIER);
        keywords.put("WHILE", TokenType.IDENTIFIER);
        keywords.put("BREAK", TokenType.IDENTIFIER);
        keywords.put("CONTINUE", TokenType.IDENTIFIER);
        keywords.put("TRY", TokenType.IDENTIFIER);
        keywords.put("CATCH", TokenType.IDENTIFIER);
        keywords.put("THROW", TokenType.IDENTIFIER);
        keywords.put("RAISERROR", TokenType.IDENTIFIER);
        keywords.put("EXEC", TokenType.IDENTIFIER);
        keywords.put("EXECUTE", TokenType.IDENTIFIER);
        keywords.put("PRINT", TokenType.IDENTIFIER);
        keywords.put("USE", TokenType.IDENTIFIER);
        keywords.put("GO", TokenType.IDENTIFIER);
        keywords.put("SWITCH", TokenType.IDENTIFIER);
        keywords.put("CASE", TokenType.IDENTIFIER);
        keywords.put("WHEN", TokenType.IDENTIFIER);
        keywords.put("THEN", TokenType.IDENTIFIER);
        keywords.put("ELSE", TokenType.IDENTIFIER);
        keywords.put("END", TokenType.IDENTIFIER);
        keywords.put("FETCH", TokenType.IDENTIFIER);
        keywords.put("NEXT", TokenType.IDENTIFIER);
        keywords.put("PRIOR", TokenType.IDENTIFIER);
        keywords.put("ABSOLUTE", TokenType.IDENTIFIER);
        keywords.put("RELATIVE", TokenType.IDENTIFIER);
        keywords.put("FROM", TokenType.IDENTIFIER);
        keywords.put("INTO", TokenType.IDENTIFIER);
        keywords.put("CURSOR", TokenType.IDENTIFIER);
        keywords.put("SCROLL", TokenType.IDENTIFIER);
        keywords.put("DYNAMIC", TokenType.IDENTIFIER);
        keywords.put("STATIC", TokenType.IDENTIFIER);
        keywords.put("KEYSET", TokenType.IDENTIFIER);
        keywords.put("FORWARD_ONLY", TokenType.IDENTIFIER);
        keywords.put("FOR", TokenType.IDENTIFIER);
        keywords.put("OPEN", TokenType.IDENTIFIER);
        keywords.put("CLOSE", TokenType.IDENTIFIER);
        keywords.put("DEALLOCATE", TokenType.IDENTIFIER);
        keywords.put("AUTHORIZATION", TokenType.IDENTIFIER);
        keywords.put("PERMISSION", TokenType.IDENTIFIER);
        keywords.put("GRANT", TokenType.IDENTIFIER);
        keywords.put("DENY", TokenType.IDENTIFIER);
        keywords.put("REVOKE", TokenType.IDENTIFIER);
        keywords.put("AS", TokenType.IDENTIFIER);
        keywords.put("WITH", TokenType.IDENTIFIER);
        keywords.put("OPTION", TokenType.IDENTIFIER);
        keywords.put("APPLY", TokenType.IDENTIFIER);
        keywords.put("CROSS", TokenType.IDENTIFIER);
        keywords.put("OUTER", TokenType.IDENTIFIER);
        keywords.put("INNER", TokenType.IDENTIFIER);
        keywords.put("LEFT", TokenType.IDENTIFIER);
        keywords.put("RIGHT", TokenType.IDENTIFIER);
        keywords.put("FULL", TokenType.IDENTIFIER);
        keywords.put("PIVOT", TokenType.IDENTIFIER);
        keywords.put("UNPIVOT", TokenType.IDENTIFIER);
        keywords.put("XML", TokenType.IDENTIFIER);
        keywords.put("JSON", TokenType.IDENTIFIER);
        keywords.put("PATH", TokenType.IDENTIFIER);
        keywords.put("VALUE", TokenType.IDENTIFIER);
        keywords.put("EXPLICIT", TokenType.IDENTIFIER);
        keywords.put("AUTO", TokenType.IDENTIFIER);
        keywords.put("ELEMENTS", TokenType.IDENTIFIER);
        keywords.put("BINARY", TokenType.IDENTIFIER);
        keywords.put("TYPE", TokenType.IDENTIFIER);

        // T-SQL functions
        keywords.put("ISNULL", TokenType.IDENTIFIER);
        keywords.put("COALESCE", TokenType.IDENTIFIER);
        keywords.put("NULLIF", TokenType.IDENTIFIER);
        keywords.put("EOMONTH", TokenType.IDENTIFIER);
        keywords.put("DATEFROMPARTS", TokenType.IDENTIFIER);
        keywords.put("DATETIMEFROMPARTS", TokenType.IDENTIFIER);
        keywords.put("EOMONTH", TokenType.IDENTIFIER);
        keywords.put("GETDATE", TokenType.IDENTIFIER);
        keywords.put("GETUTCDATE", TokenType.IDENTIFIER);
        keywords.put("SYSDATETIME", TokenType.IDENTIFIER);
        keywords.put("SYSUTCDATETIME", TokenType.IDENTIFIER);
        keywords.put("SYSDATETIMEOFFSET", TokenType.IDENTIFIER);
        keywords.put("DATEDIFF", TokenType.IDENTIFIER);
        keywords.put("DATEADD", TokenType.IDENTIFIER);
        keywords.put("DAY", TokenType.IDENTIFIER);
        keywords.put("MONTH", TokenType.IDENTIFIER);
        keywords.put("YEAR", TokenType.IDENTIFIER);
        keywords.put("QUARTER", TokenType.IDENTIFIER);
        keywords.put("WEEK", TokenType.IDENTIFIER);
        keywords.put("HOUR", TokenType.IDENTIFIER);
        keywords.put("MINUTE", TokenType.IDENTIFIER);
        keywords.put("SECOND", TokenType.IDENTIFIER);
        keywords.put("MILLISECOND", TokenType.IDENTIFIER);
        keywords.put("CONVERT", TokenType.IDENTIFIER);
        keywords.put("CAST", TokenType.IDENTIFIER);
        keywords.put("LEN", TokenType.IDENTIFIER);
        keywords.put("SUBSTRING", TokenType.IDENTIFIER);
        keywords.put("SUBSTR", TokenType.IDENTIFIER);
        keywords.put("CHARINDEX", TokenType.IDENTIFIER);
        keywords.put("PATINDEX", TokenType.IDENTIFIER);
        keywords.put("REPLACE", TokenType.IDENTIFIER);
        keywords.put("CONCAT", TokenType.IDENTIFIER);
        keywords.put("LTRIM", TokenType.IDENTIFIER);
        keywords.put("RTRIM", TokenType.IDENTIFIER);
        keywords.put("TRIM", TokenType.IDENTIFIER);
        keywords.put("UPPER", TokenType.IDENTIFIER);
        keywords.put("LOWER", TokenType.IDENTIFIER);
        keywords.put("REVERSE", TokenType.IDENTIFIER);
        keywords.put("FORMAT", TokenType.IDENTIFIER);
        keywords.put("PARSENAME", TokenType.IDENTIFIER);
        keywords.put("QUOTED_IDENTIFIER", TokenType.IDENTIFIER);
        keywords.put("ABS", TokenType.IDENTIFIER);
        keywords.put("ROUND", TokenType.IDENTIFIER);
        keywords.put("CEILING", TokenType.IDENTIFIER);
        keywords.put("FLOOR", TokenType.IDENTIFIER);
        keywords.put("SQUARE", TokenType.IDENTIFIER);
        keywords.put("SQRT", TokenType.IDENTIFIER);
        keywords.put("POWER", TokenType.IDENTIFIER);
        keywords.put("SIGN", TokenType.IDENTIFIER);
        keywords.put("RAND", TokenType.IDENTIFIER);
        keywords.put("PI", TokenType.IDENTIFIER);
        keywords.put("EXP", TokenType.IDENTIFIER);
        keywords.put("LOG", TokenType.IDENTIFIER);
        keywords.put("LOG10", TokenType.IDENTIFIER);
        keywords.put("CHECKSUM", TokenType.IDENTIFIER);
        keywords.put("CHECKSUM_AGG", TokenType.IDENTIFIER);
        keywords.put("CAST", TokenType.IDENTIFIER);
        keywords.put("CONVERT", TokenType.IDENTIFIER);
        keywords.put("TRY_CAST", TokenType.IDENTIFIER);
        keywords.put("TRY_CONVERT", TokenType.IDENTIFIER);
        keywords.put("TRY_PARSE", TokenType.IDENTIFIER);
        keywords.put("PARSE", TokenType.IDENTIFIER);

        return Trie.build(keywords);
    }
}
