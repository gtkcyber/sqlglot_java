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
package com.gtkcyber.sqlglot.dialects.iceberg;

import com.gtkcyber.sqlglot.tokens.Tokenizer;
import com.gtkcyber.sqlglot.tokens.TokenType;
import com.gtkcyber.sqlglot.tokens.Trie;

import java.util.HashMap;
import java.util.Map;

/**
 * Iceberg tokenizer.
 * Supports Iceberg-specific syntax:
 * - Double quote identifiers (primary)
 * - Single quote strings with doubled quote escaping
 * - Snapshot and version keywords
 * - Time travel specifications
 * - Version history and rollback operations
 * - MERGE INTO and partition evolution
 * - Schema and partition transformation keywords
 * - Metadata table operations
 */
public class IcebergTokenizer extends Tokenizer {
    @Override
    protected Map<String, Character> getIdentifierQuotes() {
        Map<String, Character> quotes = new HashMap<>();
        quotes.put("\"", '"');   // Double quotes (primary)
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

        // Snapshot and versioning keywords
        keywords.put("SNAPSHOT", TokenType.IDENTIFIER);
        keywords.put("SNAPSHOTS", TokenType.IDENTIFIER);
        keywords.put("VERSION", TokenType.IDENTIFIER);
        keywords.put("VERSIONS", TokenType.IDENTIFIER);
        keywords.put("FOR", TokenType.IDENTIFIER);
        keywords.put("SYSTEM_TIME", TokenType.IDENTIFIER);
        keywords.put("AS", TokenType.IDENTIFIER);
        keywords.put("OF", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("SNAPSHOT_ID", TokenType.IDENTIFIER);
        keywords.put("BRANCH", TokenType.IDENTIFIER);
        keywords.put("BRANCHES", TokenType.IDENTIFIER);
        keywords.put("TAG", TokenType.IDENTIFIER);
        keywords.put("TAGS", TokenType.IDENTIFIER);

        // History and rollback
        keywords.put("HISTORY", TokenType.IDENTIFIER);
        keywords.put("ROLLBACK", TokenType.IDENTIFIER);
        keywords.put("RESTORE", TokenType.IDENTIFIER);
        keywords.put("EXPIRE", TokenType.IDENTIFIER);
        keywords.put("ORPHAN", TokenType.IDENTIFIER);
        keywords.put("FILES", TokenType.IDENTIFIER);

        // Merge and update operations
        keywords.put("MERGE", TokenType.IDENTIFIER);
        keywords.put("INTO", TokenType.IDENTIFIER);
        keywords.put("USING", TokenType.IDENTIFIER);
        keywords.put("ON", TokenType.IDENTIFIER);
        keywords.put("MATCHED", TokenType.IDENTIFIER);
        keywords.put("NOT", TokenType.IDENTIFIER);
        keywords.put("WHEN", TokenType.IDENTIFIER);
        keywords.put("THEN", TokenType.IDENTIFIER);
        keywords.put("UPDATE", TokenType.IDENTIFIER);
        keywords.put("SET", TokenType.IDENTIFIER);
        keywords.put("DELETE", TokenType.IDENTIFIER);
        keywords.put("INSERT", TokenType.IDENTIFIER);
        keywords.put("VALUES", TokenType.IDENTIFIER);

        // Replace operations
        keywords.put("REPLACE", TokenType.IDENTIFIER);
        keywords.put("OVERWRITE", TokenType.IDENTIFIER);

        // Rewrite and maintenance
        keywords.put("REWRITE_DATA_FILES", TokenType.IDENTIFIER);
        keywords.put("REWRITE_MANIFESTS", TokenType.IDENTIFIER);
        keywords.put("REWRITE_POSITION_DELETE_FILES", TokenType.IDENTIFIER);
        keywords.put("REWRITE", TokenType.IDENTIFIER);
        keywords.put("DATA", TokenType.IDENTIFIER);
        keywords.put("MANIFESTS", TokenType.IDENTIFIER);
        keywords.put("POSITION", TokenType.IDENTIFIER);
        keywords.put("COMPACT", TokenType.IDENTIFIER);
        keywords.put("OPTIMIZE", TokenType.IDENTIFIER);
        keywords.put("WHERE", TokenType.IDENTIFIER);

        // Partition evolution
        keywords.put("PARTITION", TokenType.IDENTIFIER);
        keywords.put("PARTITION_EVOLUTION", TokenType.IDENTIFIER);
        keywords.put("PARTITIONS", TokenType.IDENTIFIER);
        keywords.put("ADD", TokenType.IDENTIFIER);
        keywords.put("DROP", TokenType.IDENTIFIER);
        keywords.put("EVOLVE", TokenType.IDENTIFIER);
        keywords.put("SPEC", TokenType.IDENTIFIER);
        keywords.put("SPECS", TokenType.IDENTIFIER);

        // Schema evolution
        keywords.put("SCHEMA", TokenType.IDENTIFIER);
        keywords.put("COLUMN", TokenType.IDENTIFIER);
        keywords.put("COLUMNS", TokenType.IDENTIFIER);
        keywords.put("ALTER", TokenType.IDENTIFIER);
        keywords.put("RENAME", TokenType.IDENTIFIER);
        keywords.put("UPDATE_TYPE", TokenType.IDENTIFIER);
        keywords.put("MAKE_OPTIONAL", TokenType.IDENTIFIER);
        keywords.put("MAKE_REQUIRED", TokenType.IDENTIFIER);
        keywords.put("DROP_COLUMN", TokenType.IDENTIFIER);
        keywords.put("COMMENT", TokenType.IDENTIFIER);

        // Metadata tables
        keywords.put("METADATA", TokenType.IDENTIFIER);
        keywords.put("MANIFEST_ENTRIES", TokenType.IDENTIFIER);
        keywords.put("ALL_MANIFESTS", TokenType.IDENTIFIER);
        keywords.put("ALL_PARTITIONS", TokenType.IDENTIFIER);
        keywords.put("ALL_FILES", TokenType.IDENTIFIER);
        keywords.put("POSITION_DELETES", TokenType.IDENTIFIER);
        keywords.put("ALL_POSITION_DELETES", TokenType.IDENTIFIER);
        keywords.put("REFERENCES", TokenType.IDENTIFIER);

        // Access control and masking
        keywords.put("MASK", TokenType.IDENTIFIER);
        keywords.put("COLUMN_MASK", TokenType.IDENTIFIER);
        keywords.put("ROW_ACCESS_POLICY", TokenType.IDENTIFIER);
        keywords.put("ROW_FILTER", TokenType.IDENTIFIER);

        // Partitioning transformations
        keywords.put("TRANSFORM", TokenType.IDENTIFIER);
        keywords.put("YEAR", TokenType.IDENTIFIER);
        keywords.put("MONTH", TokenType.IDENTIFIER);
        keywords.put("DAY", TokenType.IDENTIFIER);
        keywords.put("HOUR", TokenType.IDENTIFIER);
        keywords.put("BUCKET", TokenType.IDENTIFIER);
        keywords.put("TRUNCATE", TokenType.IDENTIFIER);

        // Commit and atomicity
        keywords.put("COMMIT", TokenType.IDENTIFIER);
        keywords.put("TRANSACTION", TokenType.IDENTIFIER);
        keywords.put("BEGIN", TokenType.IDENTIFIER);
        keywords.put("END", TokenType.IDENTIFIER);
        keywords.put("SAVEPOINT", TokenType.IDENTIFIER);
        keywords.put("RELEASE", TokenType.IDENTIFIER);

        // Data types
        keywords.put("BOOLEAN", TokenType.IDENTIFIER);
        keywords.put("INT", TokenType.IDENTIFIER);
        keywords.put("LONG", TokenType.IDENTIFIER);
        keywords.put("FLOAT", TokenType.IDENTIFIER);
        keywords.put("DOUBLE", TokenType.IDENTIFIER);
        keywords.put("DECIMAL", TokenType.IDENTIFIER);
        keywords.put("STRING", TokenType.IDENTIFIER);
        keywords.put("BINARY", TokenType.IDENTIFIER);
        keywords.put("DATE", TokenType.IDENTIFIER);
        keywords.put("TIME", TokenType.IDENTIFIER);
        keywords.put("TIMESTAMPTZ", TokenType.IDENTIFIER);
        keywords.put("STRUCT", TokenType.IDENTIFIER);
        keywords.put("ARRAY", TokenType.IDENTIFIER);
        keywords.put("MAP", TokenType.IDENTIFIER);
        keywords.put("FIXED", TokenType.IDENTIFIER);
        keywords.put("UUID", TokenType.IDENTIFIER);

        // Standard aggregate functions
        keywords.put("COUNT", TokenType.IDENTIFIER);
        keywords.put("SUM", TokenType.IDENTIFIER);
        keywords.put("AVG", TokenType.IDENTIFIER);
        keywords.put("MIN", TokenType.IDENTIFIER);
        keywords.put("MAX", TokenType.IDENTIFIER);
        keywords.put("STDDEV", TokenType.IDENTIFIER);
        keywords.put("VARIANCE", TokenType.IDENTIFIER);
        keywords.put("APPROX_COUNT_DISTINCT", TokenType.IDENTIFIER);
        keywords.put("PERCENTILE_APPROX", TokenType.IDENTIFIER);

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
        keywords.put("CURRENT_TIME", TokenType.IDENTIFIER);
        keywords.put("CURRENT_TIMESTAMP", TokenType.IDENTIFIER);
        keywords.put("DATE_FORMAT", TokenType.IDENTIFIER);
        keywords.put("DATE_PARSE", TokenType.IDENTIFIER);
        keywords.put("DATE_ADD", TokenType.IDENTIFIER);
        keywords.put("DATE_SUB", TokenType.IDENTIFIER);
        keywords.put("DATEDIFF", TokenType.IDENTIFIER);
        keywords.put("EXTRACT", TokenType.IDENTIFIER);

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
        keywords.put("ORDER", TokenType.IDENTIFIER);
        keywords.put("BY", TokenType.IDENTIFIER);
        keywords.put("ROWS", TokenType.IDENTIFIER);
        keywords.put("RANGE", TokenType.IDENTIFIER);
        keywords.put("UNBOUNDED", TokenType.IDENTIFIER);
        keywords.put("PRECEDING", TokenType.IDENTIFIER);
        keywords.put("FOLLOWING", TokenType.IDENTIFIER);
        keywords.put("CURRENT", TokenType.IDENTIFIER);
        keywords.put("ROW", TokenType.IDENTIFIER);

        // Cast and type operations
        keywords.put("CAST", TokenType.IDENTIFIER);
        keywords.put("TRY_CAST", TokenType.IDENTIFIER);

        // Other functions
        keywords.put("COALESCE", TokenType.IDENTIFIER);
        keywords.put("NULLIF", TokenType.IDENTIFIER);
        keywords.put("IFNULL", TokenType.IDENTIFIER);
        keywords.put("IF", TokenType.IDENTIFIER);
        keywords.put("CASE", TokenType.IDENTIFIER);
        keywords.put("ELSE", TokenType.IDENTIFIER);

        return Trie.build(keywords);
    }
}
