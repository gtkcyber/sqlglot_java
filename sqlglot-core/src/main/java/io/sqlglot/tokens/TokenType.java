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
package io.sqlglot.tokens;

import java.util.EnumSet;
import java.util.Set;

/**
 * Enum of SQL token types (400+ types).
 * Each token type belongs to a Category for efficient classification.
 */
public enum TokenType {
    // Punctuation
    L_PAREN(Category.PUNCTUATION, "("),
    R_PAREN(Category.PUNCTUATION, ")"),
    L_BRACKET(Category.PUNCTUATION, "["),
    R_BRACKET(Category.PUNCTUATION, "]"),
    L_BRACE(Category.PUNCTUATION, "{"),
    R_BRACE(Category.PUNCTUATION, "}"),
    COMMA(Category.PUNCTUATION, ","),
    DOT(Category.PUNCTUATION, "."),
    SEMICOLON(Category.PUNCTUATION, ";"),
    STAR(Category.PUNCTUATION, "*"),
    PLACEHOLDER(Category.PUNCTUATION, "?"),
    PARAMETER(Category.PUNCTUATION, "@"),
    COLON(Category.PUNCTUATION, ":"),
    DOUBLE_COLON(Category.PUNCTUATION, "::"),
    ARROW(Category.PUNCTUATION, "=>"),
    DARROW(Category.PUNCTUATION, "->"),
    LDARROW(Category.PUNCTUATION, "<-"),
    HASH(Category.PUNCTUATION, "#"),
    TICK(Category.PUNCTUATION, "`"),
    AT(Category.PUNCTUATION, "@"),

    // Operators
    PLUS(Category.OPERATOR, "+"),
    MINUS(Category.OPERATOR, "-"),
    SLASH(Category.OPERATOR, "/"),
    PERCENT(Category.OPERATOR, "%"),
    AMPERSAND(Category.OPERATOR, "&"),
    PIPE(Category.OPERATOR, "|"),
    CARET(Category.OPERATOR, "^"),
    TILDA(Category.OPERATOR, "~"),
    LSHIFT(Category.OPERATOR, "<<"),
    RSHIFT(Category.OPERATOR, ">>"),
    DPIPE(Category.OPERATOR, "||"),

    // Comparison operators
    EQ(Category.OPERATOR, "="),
    NEQ(Category.OPERATOR, "<>"),
    NEQ2(Category.OPERATOR, "!="),
    LT(Category.OPERATOR, "<"),
    GT(Category.OPERATOR, ">"),
    LTE(Category.OPERATOR, "<="),
    GTE(Category.OPERATOR, ">="),
    SPACESHIP(Category.OPERATOR, "<=>"),

    // Keywords - DDL
    ALTER(Category.KEYWORD, "ALTER"),
    AS(Category.KEYWORD, "AS"),
    AUTOINCREMENT(Category.KEYWORD, "AUTOINCREMENT"),
    BIGINT(Category.KEYWORD, "BIGINT"),
    BLOB(Category.KEYWORD, "BLOB"),
    BOOLEAN(Category.KEYWORD, "BOOLEAN"),
    BOTH(Category.KEYWORD, "BOTH"),
    CASCADE(Category.KEYWORD, "CASCADE"),
    CASE(Category.KEYWORD, "CASE"),
    CAST(Category.KEYWORD, "CAST"),
    CHECK(Category.KEYWORD, "CHECK"),
    COLLATE(Category.KEYWORD, "COLLATE"),
    COLUMN(Category.KEYWORD, "COLUMN"),
    CONSTRAINT(Category.KEYWORD, "CONSTRAINT"),
    CREATE(Category.KEYWORD, "CREATE"),
    CROSS(Category.KEYWORD, "CROSS"),
    CURRENT_DATE(Category.KEYWORD, "CURRENT_DATE"),
    CURRENT_TIMESTAMP(Category.KEYWORD, "CURRENT_TIMESTAMP"),
    CURRENT_TIME(Category.KEYWORD, "CURRENT_TIME"),
    CURRENT_USER(Category.KEYWORD, "CURRENT_USER"),
    DATABASE(Category.KEYWORD, "DATABASE"),
    DATE(Category.KEYWORD, "DATE"),
    DATETIME(Category.KEYWORD, "DATETIME"),
    DAY(Category.KEYWORD, "DAY"),
    DECIMAL(Category.KEYWORD, "DECIMAL"),
    DEFAULT(Category.KEYWORD, "DEFAULT"),
    DELETE(Category.KEYWORD, "DELETE"),
    DESC(Category.KEYWORD, "DESC"),
    DESCRIBE(Category.KEYWORD, "DESCRIBE"),
    DISTINCT(Category.KEYWORD, "DISTINCT"),
    DISTINCTROW(Category.KEYWORD, "DISTINCTROW"),
    DIV(Category.KEYWORD, "DIV"),
    DOUBLE(Category.KEYWORD, "DOUBLE"),
    DROP(Category.KEYWORD, "DROP"),
    ELSE(Category.KEYWORD, "ELSE"),
    ELSEIF(Category.KEYWORD, "ELSEIF"),
    END(Category.KEYWORD, "END"),
    ESCAPE(Category.KEYWORD, "ESCAPE"),
    EXCEPT(Category.KEYWORD, "EXCEPT"),
    EXISTS(Category.KEYWORD, "EXISTS"),
    FALSE(Category.KEYWORD, "FALSE"),
    FLOAT(Category.KEYWORD, "FLOAT"),
    FOREIGN(Category.KEYWORD, "FOREIGN"),
    FROM(Category.KEYWORD, "FROM"),
    FULL(Category.KEYWORD, "FULL"),
    GENERATED(Category.KEYWORD, "GENERATED"),
    GROUP(Category.KEYWORD, "GROUP"),
    GROUP_CONCAT(Category.KEYWORD, "GROUP_CONCAT"),
    HAVING(Category.KEYWORD, "HAVING"),
    HOUR(Category.KEYWORD, "HOUR"),
    IF(Category.KEYWORD, "IF"),
    ILIKE(Category.KEYWORD, "ILIKE"),
    IN(Category.KEYWORD, "IN"),
    INDEX(Category.KEYWORD, "INDEX"),
    INNER(Category.KEYWORD, "INNER"),
    INSERT(Category.KEYWORD, "INSERT"),
    INTERSECT(Category.KEYWORD, "INTERSECT"),
    INT(Category.KEYWORD, "INT"),
    INTEGER(Category.KEYWORD, "INTEGER"),
    INTO(Category.KEYWORD, "INTO"),
    IS(Category.KEYWORD, "IS"),
    JOIN(Category.KEYWORD, "JOIN"),
    KEY(Category.KEYWORD, "KEY"),
    LEFT(Category.KEYWORD, "LEFT"),
    LIKE(Category.KEYWORD, "LIKE"),
    LIMIT(Category.KEYWORD, "LIMIT"),
    LONGBLOB(Category.KEYWORD, "LONGBLOB"),
    LONGTEXT(Category.KEYWORD, "LONGTEXT"),
    MATCH(Category.KEYWORD, "MATCH"),
    MEDIUMBLOB(Category.KEYWORD, "MEDIUMBLOB"),
    MEDIUMINT(Category.KEYWORD, "MEDIUMINT"),
    MEDIUMTEXT(Category.KEYWORD, "MEDIUMTEXT"),
    MINUTE(Category.KEYWORD, "MINUTE"),
    MOD(Category.KEYWORD, "MOD"),
    MONTH(Category.KEYWORD, "MONTH"),
    NATURAL(Category.KEYWORD, "NATURAL"),
    NOT(Category.KEYWORD, "NOT"),
    NULL(Category.KEYWORD, "NULL"),
    NUMERIC(Category.KEYWORD, "NUMERIC"),
    OFFSET(Category.KEYWORD, "OFFSET"),
    ON(Category.KEYWORD, "ON"),
    OR(Category.KEYWORD, "OR"),
    ORDER(Category.KEYWORD, "ORDER"),
    OUTER(Category.KEYWORD, "OUTER"),
    PARTITION(Category.KEYWORD, "PARTITION"),
    PRECISION(Category.KEYWORD, "PRECISION"),
    PRIMARY(Category.KEYWORD, "PRIMARY"),
    PRIVILEGES(Category.KEYWORD, "PRIVILEGES"),
    READ(Category.KEYWORD, "READ"),
    REFERENCES(Category.KEYWORD, "REFERENCES"),
    REGEXP(Category.KEYWORD, "REGEXP"),
    RENAME(Category.KEYWORD, "RENAME"),
    REPEAT(Category.KEYWORD, "REPEAT"),
    REPLACE(Category.KEYWORD, "REPLACE"),
    RIGHT(Category.KEYWORD, "RIGHT"),
    RLIKE(Category.KEYWORD, "RLIKE"),
    ROW(Category.KEYWORD, "ROW"),
    SCHEMA(Category.KEYWORD, "SCHEMA"),
    SECOND(Category.KEYWORD, "SECOND"),
    SELECT(Category.KEYWORD, "SELECT"),
    SEMI(Category.KEYWORD, "SEMI"),
    SET(Category.KEYWORD, "SET"),
    SHOW(Category.KEYWORD, "SHOW"),
    SMALLINT(Category.KEYWORD, "SMALLINT"),
    SOME(Category.KEYWORD, "SOME"),
    TABLE(Category.KEYWORD, "TABLE"),
    TEMP(Category.KEYWORD, "TEMP"),
    TEMPORARY(Category.KEYWORD, "TEMPORARY"),
    TEXT(Category.KEYWORD, "TEXT"),
    THEN(Category.KEYWORD, "THEN"),
    TIME(Category.KEYWORD, "TIME"),
    TIMESTAMP(Category.KEYWORD, "TIMESTAMP"),
    TINYBLOB(Category.KEYWORD, "TINYBLOB"),
    TINYINT(Category.KEYWORD, "TINYINT"),
    TINYTEXT(Category.KEYWORD, "TINYTEXT"),
    TO(Category.KEYWORD, "TO"),
    TRUE(Category.KEYWORD, "TRUE"),
    UNION(Category.KEYWORD, "UNION"),
    UNIQUE(Category.KEYWORD, "UNIQUE"),
    UNSIGNED(Category.KEYWORD, "UNSIGNED"),
    UPDATE(Category.KEYWORD, "UPDATE"),
    USING(Category.KEYWORD, "USING"),
    VALUES(Category.KEYWORD, "VALUES"),
    VARCHAR(Category.KEYWORD, "VARCHAR"),
    VARCHARACTER(Category.KEYWORD, "VARCHARACTER"),
    VARBINARY(Category.KEYWORD, "VARBINARY"),
    WHEN(Category.KEYWORD, "WHEN"),
    WHERE(Category.KEYWORD, "WHERE"),
    WHILE(Category.KEYWORD, "WHILE"),
    WITH(Category.KEYWORD, "WITH"),
    WRITE(Category.KEYWORD, "WRITE"),
    XOR(Category.KEYWORD, "XOR"),
    YEAR(Category.KEYWORD, "YEAR"),
    ZEROFILL(Category.KEYWORD, "ZEROFILL"),

    // Keywords - DML/Functions
    ABS(Category.KEYWORD, "ABS"),
    ARRAY(Category.KEYWORD, "ARRAY"),
    ARRAY_AGG(Category.KEYWORD, "ARRAY_AGG"),
    ARRAY_CONCAT(Category.KEYWORD, "ARRAY_CONCAT"),
    ARRAY_CONTAINS(Category.KEYWORD, "ARRAY_CONTAINS"),
    ARRAY_JOIN(Category.KEYWORD, "ARRAY_JOIN"),
    ARRAY_SIZE(Category.KEYWORD, "ARRAY_SIZE"),
    AVG(Category.KEYWORD, "AVG"),
    CEIL(Category.KEYWORD, "CEIL"),
    COALESCE(Category.KEYWORD, "COALESCE"),
    CONCAT(Category.KEYWORD, "CONCAT"),
    CONCAT_WS(Category.KEYWORD, "CONCAT_WS"),
    COUNT(Category.KEYWORD, "COUNT"),
    CURRENT_USER_ID(Category.KEYWORD, "CURRENT_USER_ID"),
    DATE_ADD(Category.KEYWORD, "DATE_ADD"),
    DATE_FORMAT(Category.KEYWORD, "DATE_FORMAT"),
    DATE_PARSE(Category.KEYWORD, "DATE_PARSE"),
    DATE_SUB(Category.KEYWORD, "DATE_SUB"),
    DATE_TRUNC(Category.KEYWORD, "DATE_TRUNC"),
    DAY_OF_WEEK(Category.KEYWORD, "DAY_OF_WEEK"),
    DAY_OF_YEAR(Category.KEYWORD, "DAY_OF_YEAR"),
    DENSE_RANK(Category.KEYWORD, "DENSE_RANK"),
    EXTRACT(Category.KEYWORD, "EXTRACT"),
    FIRST_VALUE(Category.KEYWORD, "FIRST_VALUE"),
    FLOOR(Category.KEYWORD, "FLOOR"),
    FORMAT(Category.KEYWORD, "FORMAT"),
    FROM_UNIXTIME(Category.KEYWORD, "FROM_UNIXTIME"),
    FROM_ISO8601(Category.KEYWORD, "FROM_ISO8601"),
    GREATEST(Category.KEYWORD, "GREATEST"),
    GSUB(Category.KEYWORD, "GSUB"),
    HEX(Category.KEYWORD, "HEX"),
    HISTOGRAM(Category.KEYWORD, "HISTOGRAM"),
    IF_EXPR(Category.KEYWORD, "IF_EXPR"),
    IFNULL(Category.KEYWORD, "IFNULL"),
    ISNULL(Category.KEYWORD, "ISNULL"),
    JSON_ARRAY(Category.KEYWORD, "JSON_ARRAY"),
    JSON_EXTRACT(Category.KEYWORD, "JSON_EXTRACT"),
    JSON_EXTRACT_SCALAR(Category.KEYWORD, "JSON_EXTRACT_SCALAR"),
    JSON_FORMAT(Category.KEYWORD, "JSON_FORMAT"),
    JSON_PARSE(Category.KEYWORD, "JSON_PARSE"),
    LAST_VALUE(Category.KEYWORD, "LAST_VALUE"),
    LCASE(Category.KEYWORD, "LCASE"),
    LEAST(Category.KEYWORD, "LEAST"),
    LENGTH(Category.KEYWORD, "LENGTH"),
    LIKE_REGEX(Category.KEYWORD, "LIKE_REGEX"),
    LPAD(Category.KEYWORD, "LPAD"),
    LTRIM(Category.KEYWORD, "LTRIM"),
    LOWER(Category.KEYWORD, "LOWER"),
    MAP(Category.KEYWORD, "MAP"),
    MAP_KEYS(Category.KEYWORD, "MAP_KEYS"),
    MAP_VALUES(Category.KEYWORD, "MAP_VALUES"),
    MAX(Category.KEYWORD, "MAX"),
    MD5(Category.KEYWORD, "MD5"),
    MIN(Category.KEYWORD, "MIN"),
    MINUTE_OF_HOUR(Category.KEYWORD, "MINUTE_OF_HOUR"),
    MOD_FUNC(Category.KEYWORD, "MOD_FUNC"),
    MONTH_OF_YEAR(Category.KEYWORD, "MONTH_OF_YEAR"),
    NULLIF(Category.KEYWORD, "NULLIF"),
    PARSE_DATE(Category.KEYWORD, "PARSE_DATE"),
    PARSE_TIMESTAMP(Category.KEYWORD, "PARSE_TIMESTAMP"),
    POW(Category.KEYWORD, "POW"),
    PRINTF(Category.KEYWORD, "PRINTF"),
    RANK(Category.KEYWORD, "RANK"),
    REGEXP_EXTRACT(Category.KEYWORD, "REGEXP_EXTRACT"),
    REGEXP_EXTRACT_ALL(Category.KEYWORD, "REGEXP_EXTRACT_ALL"),
    REGEXP_REPLACE(Category.KEYWORD, "REGEXP_REPLACE"),
    REPEAT_FUNC(Category.KEYWORD, "REPEAT_FUNC"),
    REVERSE(Category.KEYWORD, "REVERSE"),
    ROW_NUMBER(Category.KEYWORD, "ROW_NUMBER"),
    RPAD(Category.KEYWORD, "RPAD"),
    RTRIM(Category.KEYWORD, "RTRIM"),
    SHA1(Category.KEYWORD, "SHA1"),
    SHA256(Category.KEYWORD, "SHA256"),
    SHA512(Category.KEYWORD, "SHA512"),
    SQRT(Category.KEYWORD, "SQRT"),
    STDDEV(Category.KEYWORD, "STDDEV"),
    STDDEV_POP(Category.KEYWORD, "STDDEV_POP"),
    STDDEV_SAMP(Category.KEYWORD, "STDDEV_SAMP"),
    STRPOS(Category.KEYWORD, "STRPOS"),
    STRUCT(Category.KEYWORD, "STRUCT"),
    SUBSTR(Category.KEYWORD, "SUBSTR"),
    SUBSTRING(Category.KEYWORD, "SUBSTRING"),
    SUBSTRING_INDEX(Category.KEYWORD, "SUBSTRING_INDEX"),
    SUM(Category.KEYWORD, "SUM"),
    SYSDATE(Category.KEYWORD, "SYSDATE"),
    TIMESTAMP_ADD(Category.KEYWORD, "TIMESTAMP_ADD"),
    TIMESTAMP_DIFF(Category.KEYWORD, "TIMESTAMP_DIFF"),
    TIMESTAMP_MICROS(Category.KEYWORD, "TIMESTAMP_MICROS"),
    TIMESTAMP_MILLIS(Category.KEYWORD, "TIMESTAMP_MILLIS"),
    TIMESTAMP_SECONDS(Category.KEYWORD, "TIMESTAMP_SECONDS"),
    TIMESTAMP_TRUNC(Category.KEYWORD, "TIMESTAMP_TRUNC"),
    TO_CHAR(Category.KEYWORD, "TO_CHAR"),
    TO_DATE(Category.KEYWORD, "TO_DATE"),
    TO_HEX(Category.KEYWORD, "TO_HEX"),
    TO_JSON(Category.KEYWORD, "TO_JSON"),
    TO_TIMESTAMP(Category.KEYWORD, "TO_TIMESTAMP"),
    TO_TIMESTAMP_NTZ(Category.KEYWORD, "TO_TIMESTAMP_NTZ"),
    TO_UNIX_TIMESTAMP(Category.KEYWORD, "TO_UNIX_TIMESTAMP"),
    TRANSFORM(Category.KEYWORD, "TRANSFORM"),
    TRIM(Category.KEYWORD, "TRIM"),
    TRY_CAST(Category.KEYWORD, "TRY_CAST"),
    TRY_PARSE(Category.KEYWORD, "TRY_PARSE"),
    UNIX_DATE(Category.KEYWORD, "UNIX_DATE"),
    UNIX_MICROS(Category.KEYWORD, "UNIX_MICROS"),
    UNIX_MILLIS(Category.KEYWORD, "UNIX_MILLIS"),
    UNIX_SECONDS(Category.KEYWORD, "UNIX_SECONDS"),
    UNIX_TIMESTAMP(Category.KEYWORD, "UNIX_TIMESTAMP"),
    UPPER(Category.KEYWORD, "UPPER"),
    UCASE(Category.KEYWORD, "UCASE"),
    UNNEST(Category.KEYWORD, "UNNEST"),
    VAR_POP(Category.KEYWORD, "VAR_POP"),
    VAR_SAMP(Category.KEYWORD, "VAR_SAMP"),
    VARIANCE(Category.KEYWORD, "VARIANCE"),
    WEEK(Category.KEYWORD, "WEEK"),
    WEEK_OF_YEAR(Category.KEYWORD, "WEEK_OF_YEAR"),
    WINDOW(Category.KEYWORD, "WINDOW"),
    XPATH(Category.KEYWORD, "XPATH"),
    YEAR_OF_WEEK(Category.KEYWORD, "YEAR_OF_WEEK"),

    // Keywords - Window functions
    OVER(Category.KEYWORD, "OVER"),
    PARTITION_BY(Category.KEYWORD, "PARTITION BY"),
    ROWS(Category.KEYWORD, "ROWS"),
    RANGE(Category.KEYWORD, "RANGE"),
    UNBOUNDED(Category.KEYWORD, "UNBOUNDED"),
    PRECEDING(Category.KEYWORD, "PRECEDING"),
    FOLLOWING(Category.KEYWORD, "FOLLOWING"),
    CURRENT_ROW(Category.KEYWORD, "CURRENT ROW"),

    // Keywords - Transaction control
    BEGIN(Category.KEYWORD, "BEGIN"),
    COMMIT(Category.KEYWORD, "COMMIT"),
    ROLLBACK(Category.KEYWORD, "ROLLBACK"),
    SAVEPOINT(Category.KEYWORD, "SAVEPOINT"),
    START(Category.KEYWORD, "START"),
    TRANSACTION(Category.KEYWORD, "TRANSACTION"),

    // Keywords - Access control
    GRANT(Category.KEYWORD, "GRANT"),
    REVOKE(Category.KEYWORD, "REVOKE"),

    // Keywords - Other
    AFTER(Category.KEYWORD, "AFTER"),
    ALL(Category.KEYWORD, "ALL"),
    AND(Category.KEYWORD, "AND"),
    ANY(Category.KEYWORD, "ANY"),
    ASC(Category.KEYWORD, "ASC"),
    AST(Category.KEYWORD, "AST"),
    ASYNCHRONOUS(Category.KEYWORD, "ASYNCHRONOUS"),
    BEFORE(Category.KEYWORD, "BEFORE"),
    BETWEEN(Category.KEYWORD, "BETWEEN"),
    BINARY(Category.KEYWORD, "BINARY"),
    CALL(Category.KEYWORD, "CALL"),
    CLOSE(Category.KEYWORD, "CLOSE"),
    COMMENT(Category.KEYWORD, "COMMENT"),
    COPY(Category.KEYWORD, "COPY"),
    DECLARE(Category.KEYWORD, "DECLARE"),
    DO(Category.KEYWORD, "DO"),
    EACH(Category.KEYWORD, "EACH"),
    EXPLAIN(Category.KEYWORD, "EXPLAIN"),
    FETCH(Category.KEYWORD, "FETCH"),
    FOR(Category.KEYWORD, "FOR"),
    FORCE(Category.KEYWORD, "FORCE"),
    FORMAT_JSON(Category.KEYWORD, "FORMAT_JSON"),
    FUNCTION(Category.KEYWORD, "FUNCTION"),
    GET(Category.KEYWORD, "GET"),
    IGNORE(Category.KEYWORD, "IGNORE"),
    IMMEDIATELY(Category.KEYWORD, "IMMEDIATELY"),
    IMMUTABLE(Category.KEYWORD, "IMMUTABLE"),
    LANGUAGE(Category.KEYWORD, "LANGUAGE"),
    LATERAL(Category.KEYWORD, "LATERAL"),
    LEADING(Category.KEYWORD, "LEADING"),
    LIKE_PATTERN(Category.KEYWORD, "LIKE_PATTERN"),
    LOCATION(Category.KEYWORD, "LOCATION"),
    NEXT(Category.KEYWORD, "NEXT"),
    NO(Category.KEYWORD, "NO"),
    ONLY(Category.KEYWORD, "ONLY"),
    OPEN(Category.KEYWORD, "OPEN"),
    OPTION(Category.KEYWORD, "OPTION"),
    OPTIMIZED(Category.KEYWORD, "OPTIMIZED"),
    PERSISTENT(Category.KEYWORD, "PERSISTENT"),
    PLAN(Category.KEYWORD, "PLAN"),
    PRAGMA(Category.KEYWORD, "PRAGMA"),
    PRIOR(Category.KEYWORD, "PRIOR"),
    PROCEDURE(Category.KEYWORD, "PROCEDURE"),
    PROPERTIES(Category.KEYWORD, "PROPERTIES"),
    PROVE(Category.KEYWORD, "PROVE"),
    QUALIFIED(Category.KEYWORD, "QUALIFIED"),
    QUALIFY(Category.KEYWORD, "QUALIFY"),
    RECURSIVE(Category.KEYWORD, "RECURSIVE"),
    RENAME_TO(Category.KEYWORD, "RENAME TO"),
    RETURNS(Category.KEYWORD, "RETURNS"),
    SAMPLE(Category.KEYWORD, "SAMPLE"),
    SECURITY(Category.KEYWORD, "SECURITY"),
    SEED(Category.KEYWORD, "SEED"),
    SETS(Category.KEYWORD, "SETS"),
    SHELL(Category.KEYWORD, "SHELL"),
    SORT(Category.KEYWORD, "SORT"),
    STABLE(Category.KEYWORD, "STABLE"),
    STATS(Category.KEYWORD, "STATS"),
    STORED(Category.KEYWORD, "STORED"),
    STRICT(Category.KEYWORD, "STRICT"),
    SYNCHRONOUS(Category.KEYWORD, "SYNCHRONOUS"),
    TAG(Category.KEYWORD, "TAG"),
    TRAILING(Category.KEYWORD, "TRAILING"),
    TRIGGER(Category.KEYWORD, "TRIGGER"),
    TUPLE(Category.KEYWORD, "TUPLE"),
    TYPE(Category.KEYWORD, "TYPE"),
    UNARY(Category.KEYWORD, "UNARY"),
    VARIADIC(Category.KEYWORD, "VARIADIC"),
    VERBOSE(Category.KEYWORD, "VERBOSE"),
    VIEW(Category.KEYWORD, "VIEW"),

    // Special tokens
    EOF(Category.SPECIAL, "EOF"),
    UNKNOWN(Category.SPECIAL, "UNKNOWN"),

    // Literals (will be typed at parse time)
    NUMBER(Category.LITERAL, null),
    STRING(Category.LITERAL, null),
    IDENTIFIER(Category.LITERAL, null),
    VAR(Category.LITERAL, null),
    BYTE_STRING(Category.LITERAL, null),
    HEX_STRING(Category.LITERAL, null),
    RAW_STRING(Category.LITERAL, null),
    UNICODE_STRING(Category.LITERAL, null),
    NATIONAL_STRING(Category.LITERAL, null);

    public enum Category {
        PUNCTUATION,
        OPERATOR,
        LITERAL,
        DATA_TYPE,
        KEYWORD,
        SPECIAL
    }

    private final Category category;
    private final String text;

    TokenType(Category category, String text) {
        this.category = category;
        this.text = text;
    }

    public Category getCategory() {
        return category;
    }

    public String getText() {
        return text;
    }

    public boolean isKeyword() {
        return category == Category.KEYWORD;
    }

    public boolean isOperator() {
        return category == Category.OPERATOR;
    }

    public boolean isPunctuation() {
        return category == Category.PUNCTUATION;
    }

    public boolean isLiteral() {
        return category == Category.LITERAL;
    }

    public static final Set<TokenType> DATA_TYPES = EnumSet.of(
        INT, INTEGER, BIGINT, SMALLINT, TINYINT,
        FLOAT, DOUBLE, DECIMAL, NUMERIC,
        VARCHAR, TEXT, BLOB, LONGTEXT, LONGBLOB, MEDIUMTEXT, MEDIUMBLOB, TINYTEXT, TINYBLOB,
        DATE, TIME, DATETIME, TIMESTAMP,
        BOOLEAN, VARBINARY, BINARY,
        ARRAY, MAP, STRUCT
    );

    public static final Set<TokenType> COMPARISON_OPERATORS = EnumSet.of(
        EQ, NEQ, NEQ2, LT, GT, LTE, GTE, SPACESHIP, LIKE, ILIKE, BETWEEN, IN, EXISTS, IS
    );

    public static final Set<TokenType> LOGICAL_OPERATORS = EnumSet.of(
        AND, OR, NOT, XOR
    );

    public static final Set<TokenType> ARITHMETIC_OPERATORS = EnumSet.of(
        PLUS, MINUS, SLASH, PERCENT, STAR
    );

    public static final Set<TokenType> BITWISE_OPERATORS = EnumSet.of(
        AMPERSAND, PIPE, CARET, TILDA, LSHIFT, RSHIFT
    );

    public static final Set<TokenType> AGGREGATE_FUNCTIONS = EnumSet.of(
        COUNT, SUM, AVG, MIN, MAX, STDDEV, STDDEV_POP, STDDEV_SAMP, VAR_POP, VAR_SAMP, VARIANCE,
        GROUP_CONCAT, ARRAY_AGG
    );

    public static final Set<TokenType> WINDOW_FUNCTIONS = EnumSet.of(
        ROW_NUMBER, RANK, DENSE_RANK, FIRST_VALUE, LAST_VALUE
    );
}
