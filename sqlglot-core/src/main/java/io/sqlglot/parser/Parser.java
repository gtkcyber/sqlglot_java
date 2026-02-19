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
package io.sqlglot.parser;

import io.sqlglot.expressions.*;
import io.sqlglot.tokens.*;

import java.util.*;

/**
 * Recursive descent SQL parser with operator precedence climbing.
 */
public class Parser {
    private final ParserConfig config;
    private List<Token> tokens;
    private int pos;
    private final List<ParseError> errors = new ArrayList<>();

    public Parser() {
        this(ParserConfig.defaultConfig());
    }

    public Parser(ParserConfig config) {
        this.config = Objects.requireNonNull(config);
    }

    /**
     * Parses a list of SQL statements.
     */
    public List<Expression> parse(String sql) {
        Tokenizer tokenizer = new Tokenizer();
        this.tokens = tokenizer.tokenize(sql);
        this.pos = 0;
        this.errors.clear();

        List<Expression> statements = new ArrayList<>();
        while (!isAtEnd() && currentToken().type() != TokenType.EOF) {
            try {
                Optional<Expression> stmt = parseStatement();
                stmt.ifPresent(statements::add);
                skipSemicolon();
            } catch (ParseException e) {
                if (config.errorLevel() == ErrorLevel.IMMEDIATE) {
                    throw e;
                }
                errors.add(new ParseError(e.getMessage(), currentToken().line(),
                    currentToken().col(), sql));
                if (errors.size() >= config.maxErrors()) {
                    break;
                }
                synchronize();
            }
        }

        if (config.errorLevel() == ErrorLevel.RAISE && !errors.isEmpty()) {
            throw new ParseException("Parse errors: " + errors);
        }

        return statements;
    }

    /**
     * Parses a single SQL statement.
     */
    private Optional<Expression> parseStatement() {
        TokenType type = currentToken().type();

        return switch (type) {
            case SELECT -> parseSelect();
            case INSERT -> parseInsert();
            case UPDATE -> parseUpdate();
            case DELETE -> parseDelete();
            case CREATE -> parseCreate();
            case DROP -> parseDrop();
            case ALTER -> parseAlter();
            case WITH -> parseWith();
            default -> {
                error("Unexpected token: " + type);
                yield Optional.empty();
            }
        };
    }

    /**
     * Parses a SELECT statement with optional set operations (UNION, INTERSECT, EXCEPT).
     */
    private Optional<Expression> parseSelect() {
        Expression select = parseBasicSelect().orElse(null);
        if (select == null) {
            return Optional.empty();
        }

        // Handle set operations: UNION, INTERSECT, EXCEPT
        while (!isAtEnd() && (currentToken().type() == TokenType.UNION ||
                              currentToken().type() == TokenType.INTERSECT ||
                              currentToken().type() == TokenType.EXCEPT)) {
            TokenType op = currentToken().type();
            advance();

            boolean all = match(TokenType.ALL);
            Expression right = parseBasicSelect().orElse(null);
            if (right == null) {
                error("Expected SELECT after " + op);
                break;
            }

            select = switch (op) {
                case UNION -> new Nodes.Union((Nodes.Select) select, (Nodes.Select) right, !all);
                case INTERSECT -> new Nodes.Intersect((Nodes.Select) select, (Nodes.Select) right);
                case EXCEPT -> new Nodes.Except((Nodes.Select) select, (Nodes.Select) right);
                default -> select;
            };
        }

        return Optional.of(select);
    }

    /**
     * Parses a basic SELECT statement (without set operations).
     */
    private Optional<Expression> parseBasicSelect() {
        if (currentToken().type() != TokenType.SELECT) {
            return Optional.empty();
        }

        expect(TokenType.SELECT);

        boolean distinct = match(TokenType.DISTINCT);

        List<Expression> expressions = parseSelectExpressions();
        Nodes.From from = null;
        if (match(TokenType.FROM)) {
            from = new Nodes.From(parseTableReference());
        }

        List<Nodes.Join> joins = new ArrayList<>();
        while (matchJoin()) {
            joins.add(parseJoin());
        }

        Nodes.Where where = null;
        if (match(TokenType.WHERE)) {
            where = new Nodes.Where(parseExpression(0));
        }

        Nodes.GroupBy groupBy = null;
        if (match(TokenType.GROUP)) {
            // Skip BY if present (not always tokenized separately)
            if (currentToken().text().equalsIgnoreCase("BY")) {
                advance();
            }
            List<Expression> groupExpressions = parseGroupByExpressions();
            groupBy = new Nodes.GroupBy(groupExpressions);
        }

        Nodes.Having having = null;
        if (match(TokenType.HAVING)) {
            having = new Nodes.Having(parseExpression(0));
        }

        List<Nodes.OrderBy> orderBy = new ArrayList<>();
        if (match(TokenType.ORDER)) {
            // Skip optional BY keyword
            if (currentToken().text().equalsIgnoreCase("BY")) {
                advance();
            }
            // Parse ORDER BY expressions until LIMIT/OFFSET or end of SELECT
            while (!isAtEnd() && !isOrderByTerminator()) {
                Expression expr = parseExpression(0);
                String direction = "ASC";
                if (match(TokenType.ASC)) {
                    direction = "ASC";
                } else if (match(TokenType.DESC)) {
                    direction = "DESC";
                }
                orderBy.add(new Nodes.OrderBy(expr, direction));
                if (!match(TokenType.COMMA)) {
                    break;
                }
            }
        }

        Nodes.Limit limit = null;
        if (match(TokenType.LIMIT)) {
            limit = new Nodes.Limit(parsePrimary());
        }

        Nodes.Offset offset = null;
        if (match(TokenType.OFFSET)) {
            offset = new Nodes.Offset(parsePrimary());
        }

        return Optional.of(new Nodes.Select(expressions, distinct, from, joins, where, groupBy, having, orderBy, limit, offset));
    }

    /**
     * Parses SELECT expressions (columns).
     */
    private List<Expression> parseSelectExpressions() {
        List<Expression> expressions = new ArrayList<>();

        if (match(TokenType.STAR)) {
            expressions.add(Nodes.Star.INSTANCE);
        } else {
            do {
                Expression expr = parseExpression(0);

                if (match(TokenType.AS)) {
                    String alias = expect(TokenType.IDENTIFIER).text();
                    expr = new Nodes.Alias(expr, alias);
                }

                expressions.add(expr);
            } while (match(TokenType.COMMA));
        }

        return expressions;
    }

    /**
     * Parses a table reference (with optional alias).
     */
    private Expression parseTableReference() {
        Expression expr = parsePrimary();

        if (match(TokenType.AS)) {
            String alias = expect(TokenType.IDENTIFIER).text();
            expr = new Nodes.Alias(expr, alias);
        }

        return expr;
    }

    /**
     * Extracts table name from an expression (Identifier -> Table).
     */
    private Nodes.Table getTableFromExpression(Expression expr) {
        if (expr instanceof Nodes.Table t) {
            return t;
        } else if (expr instanceof Nodes.Identifier id) {
            return new Nodes.Table(id.getName());
        } else {
            error("Expected table name, got: " + expr.getClass().getSimpleName());
            return new Nodes.Table("unknown");
        }
    }

    /**
     * Parses a JOIN clause.
     */
    private Nodes.Join parseJoin() {
        String joinType = currentToken().text();
        advance();

        // JOIN keyword already matched


        Expression table = parseTableReference();
        Expression on = null;

        if (match(TokenType.ON)) {
            on = parseExpression(0);
        }

        return new Nodes.Join(joinType, table, on);
    }

    /**
     * Returns true if current token starts a JOIN clause.
     */
    private boolean matchJoin() {
        TokenType type = currentToken().type();
        return type == TokenType.INNER || type == TokenType.LEFT || type == TokenType.RIGHT ||
               type == TokenType.FULL || type == TokenType.CROSS || type == TokenType.OUTER;
    }

    /**
     * Parses an INSERT statement.
     */
    private Optional<Expression> parseInsert() {
        expect(TokenType.INSERT);
        expect(TokenType.INTO);

        Expression tableExpr = parsePrimary();
        Nodes.Table table = getTableFromExpression(tableExpr);

        List<Expression> columns = new ArrayList<>();
        if (match(TokenType.L_PAREN)) {
            columns = parseExpressionList();
            expect(TokenType.R_PAREN);
        }

        expect(TokenType.VALUES);
        if (match(TokenType.L_PAREN)) {
            List<Expression> values = parseExpressionList();
            expect(TokenType.R_PAREN);
            return Optional.of(new Nodes.Insert(table, columns, new Nodes.Row(values)));
        }

        return Optional.empty();
    }

    /**
     * Parses an UPDATE statement.
     */
    private Optional<Expression> parseUpdate() {
        expect(TokenType.UPDATE);
        Expression tableExpr = parsePrimary();
        Nodes.Table table = getTableFromExpression(tableExpr);

        expect(TokenType.SET);
        Map<Expression, Expression> set = new LinkedHashMap<>();
        do {
            Expression col = parseExpression(0);
            expect(TokenType.EQ);
            Expression value = parseExpression(0);
            set.put(col, value);
        } while (match(TokenType.COMMA));

        Expression where = null;
        if (match(TokenType.WHERE)) {
            where = parseExpression(0);
        }

        return Optional.of(new Nodes.Update(table, set, where));
    }

    /**
     * Parses a DELETE statement.
     */
    private Optional<Expression> parseDelete() {
        expect(TokenType.DELETE);
        expect(TokenType.FROM);
        Expression tableExpr = parsePrimary();
        Nodes.Table table = getTableFromExpression(tableExpr);

        Expression where = null;
        if (match(TokenType.WHERE)) {
            where = parseExpression(0);
        }

        return Optional.of(new Nodes.Delete(table, where));
    }

    /**
     * Parses a CREATE statement (stub).
     */
    private Optional<Expression> parseCreate() {
        expect(TokenType.CREATE);
        String type = currentToken().text();
        advance();
        String name = expect(TokenType.IDENTIFIER).text();
        return Optional.of(new Nodes.Create(type, name));
    }

    /**
     * Parses a DROP statement (stub).
     */
    private Optional<Expression> parseDrop() {
        expect(TokenType.DROP);
        String type = currentToken().text();
        advance();
        String name = expect(TokenType.IDENTIFIER).text();
        return Optional.of(new Nodes.Drop(type, name));
    }

    /**
     * Parses an ALTER statement (stub).
     */
    private Optional<Expression> parseAlter() {
        expect(TokenType.ALTER);
        String type = currentToken().text();
        advance();
        String name = expect(TokenType.IDENTIFIER).text();
        return Optional.of(new Nodes.Alter(type, name));
    }

    /**
     * Parses a WITH (CTE) statement.
     */
    private Optional<Expression> parseWith() {
        expect(TokenType.WITH);

        List<Nodes.CTE> ctes = new ArrayList<>();
        do {
            String name = expect(TokenType.IDENTIFIER).text();
            expect(TokenType.AS);
            expect(TokenType.L_PAREN);
            Optional<Expression> select = parseSelect();
            expect(TokenType.R_PAREN);

            if (select.isPresent() && select.get() instanceof Nodes.Select s) {
                ctes.add(new Nodes.CTE(name, s));
            }
        } while (match(TokenType.COMMA));

        Optional<Expression> select = parseSelect();
        if (select.isPresent() && select.get() instanceof Nodes.Select s) {
            return Optional.of(new Nodes.With(ctes, s));
        }

        return Optional.empty();
    }

    /**
     * Parses an expression with operator precedence climbing (iterative, no recursion).
     * Uses explicit stacks instead of recursive calls to avoid stack overflow on deeply nested expressions.
     */
    private Expression parseExpression(int minPrec) {
        // Stacks for operands and operators
        Stack<Expression> values = new Stack<>();
        Stack<Integer> precedences = new Stack<>();
        Stack<TokenType> operators = new Stack<>();

        // Parse first operand
        values.push(parseUnary());

        // Main loop - iterative operator processing
        while (!isAtEnd() && getPrecedence(currentToken()) >= minPrec) {
            int currPrec = getPrecedence(currentToken());
            TokenType currOp = currentToken().type();
            advance();

            // Reduce operators with >= precedence (left-associativity)
            while (!precedences.isEmpty() && precedences.peek() >= currPrec) {
                Expression right = values.pop();
                Expression left = values.pop();
                TokenType op = operators.pop();
                precedences.pop();
                values.push(createBinaryOp(op, left, right));
            }

            // Push next operand
            values.push(parseUnary());

            // Push current operator and its precedence
            operators.push(currOp);
            precedences.push(currPrec);
        }

        // Final reductions - process remaining operators in stack
        while (!precedences.isEmpty()) {
            Expression right = values.pop();
            Expression left = values.pop();
            TokenType op = operators.pop();
            precedences.pop();
            values.push(createBinaryOp(op, left, right));
        }

        return values.pop();
    }

    /**
     * Parses a unary expression (iterative for unary operators to avoid deep recursion).
     */
    private Expression parseUnary() {
        // Collect unary operators in order (iterative instead of recursive)
        Stack<TokenType> unaryOps = new Stack<>();
        while (!isAtEnd()) {
            TokenType type = currentToken().type();
            if (type == TokenType.NOT) {
                unaryOps.push(TokenType.NOT);
                advance();
            } else if (type == TokenType.MINUS) {
                unaryOps.push(TokenType.MINUS);
                advance();
            } else {
                break;
            }
        }

        // Parse primary expression
        Expression expr = parsePrimary();

        // Apply unary operators in reverse order (they were collected in forward order)
        while (!unaryOps.isEmpty()) {
            TokenType op = unaryOps.pop();
            if (op == TokenType.NOT) {
                expr = new Nodes.Not(expr);
            } else if (op == TokenType.MINUS) {
                expr = new Nodes.Sub(new Nodes.Literal("0", false), expr);
            }
        }

        // Postfix operators (iterative to avoid recursion)
        while (true) {
            if (match(TokenType.IS)) {
                if (match(TokenType.NULL)) {
                    expr = new Nodes.IsNull(expr);
                } else if (match(TokenType.NOT)) {
                    expect(TokenType.NULL);
                    expr = new Nodes.IsNotNull(expr);
                }
            } else if (match(TokenType.LIKE)) {
                // Use high precedence to avoid consuming operators in LIKE pattern
                Expression pattern = parseExpression(7);
                expr = new Nodes.Like(expr, pattern);
            } else if (match(TokenType.IN)) {
                expect(TokenType.L_PAREN);
                List<Expression> values = parseExpressionList();
                expect(TokenType.R_PAREN);
                expr = new Nodes.In(expr, values);
            } else if (match(TokenType.BETWEEN)) {
                // Use precedence 3 to exclude AND (precedence 2) from being parsed as operator
                Expression low = parseExpression(3);
                expect(TokenType.AND);
                Expression high = parseExpression(3);
                expr = new Nodes.Between(expr, low, high);
            } else if (match(TokenType.DOT)) {
                Expression right = parsePrimary();
                expr = new Nodes.Dot(expr, right);
            } else if (match(TokenType.L_BRACKET)) {
                List<Expression> offset = parseExpressionList();
                expect(TokenType.R_BRACKET);
                expr = new Nodes.Bracket(expr, offset);
            } else {
                break;
            }
        }

        return expr;
    }

    /**
     * Parses a primary expression (literals, identifiers, functions, parenthesized expressions).
     */
    private Expression parsePrimary() {
        Token current = currentToken();

        // Literals
        if (current.type() == TokenType.NUMBER) {
            advance();
            return new Nodes.Literal(current.text(), false);
        }

        if (current.type() == TokenType.STRING) {
            advance();
            return new Nodes.Literal(current.text(), true);
        }

        if (match(TokenType.NULL)) {
            return Nodes.Null.INSTANCE;
        }

        if (match(TokenType.TRUE)) {
            return Nodes.True.INSTANCE;
        }

        if (match(TokenType.FALSE)) {
            return Nodes.False.INSTANCE;
        }

        if (match(TokenType.STAR)) {
            return Nodes.Star.INSTANCE;
        }

        // Parenthesized expression or subquery
        if (match(TokenType.L_PAREN)) {
            if (peek().type() == TokenType.SELECT) {
                Optional<Expression> select = parseSelect();
                expect(TokenType.R_PAREN);
                if (select.isPresent() && select.get() instanceof Nodes.Select s) {
                    return new Nodes.Subquery(s);
                }
                return Nodes.Null.INSTANCE;
            } else {
                Expression expr = parseExpression(0);
                expect(TokenType.R_PAREN);
                return new Nodes.Paren(expr);
            }
        }

        // Cast
        if (match(TokenType.CAST)) {
            expect(TokenType.L_PAREN);
            Expression expr = parseExpression(0);
            expect(TokenType.AS);
            Nodes.DataTypeExpr type = parseDataType();
            expect(TokenType.R_PAREN);
            return new Nodes.Cast(expr, type);
        }

        // Case expression
        if (match(TokenType.CASE)) {
            Expression expr = null;
            if (currentToken().type() != TokenType.WHEN) {
                expr = parseExpression(0);
            }

            List<Nodes.When> whens = new ArrayList<>();
            while (match(TokenType.WHEN)) {
                Expression condition = parseExpression(0);
                expect(TokenType.THEN);
                Expression result = parseExpression(0);
                whens.add(new Nodes.When(condition, result));
            }

            Expression defaultExpr = null;
            if (match(TokenType.ELSE)) {
                defaultExpr = parseExpression(0);
            }

            expect(TokenType.END);
            return new Nodes.Case(expr, whens, defaultExpr);
        }

        // Identifier or function (allow keywords as function names)
        if (current.type() == TokenType.IDENTIFIER || current.type().isKeyword()) {
            String name = current.text();
            advance();

            if (match(TokenType.L_PAREN)) {
                // Function call (keywords like COUNT, SUM can be function names)
                List<Expression> args = parseFunctionArguments();
                expect(TokenType.R_PAREN);
                return new Nodes.Function(name, args);
            }

            // For keywords, treat as identifier (e.g., COUNT without parens)
            return new Nodes.Identifier(name);
        }

        error("Unexpected token: " + current.type());
        return Nodes.Null.INSTANCE;
    }

    /**
     * Parses a data type (INT, VARCHAR, ARRAY<INT>, etc).
     */
    private Nodes.DataTypeExpr parseDataType() {
        // Accept both IDENTIFIER (custom types) and KEYWORD (built-in types like INT, VARCHAR)
        Token typeToken = currentToken();
        String type;

        if (typeToken.type() == TokenType.IDENTIFIER) {
            type = expect(TokenType.IDENTIFIER).text();
        } else if (typeToken.type().isKeyword()) {
            // Handle keywords used as type names (INT, VARCHAR, BIGINT, etc.)
            type = typeToken.text();
            advance();
        } else {
            error("Expected data type but got " + typeToken.type());
            type = "UNKNOWN";
        }

        List<Expression> params = new ArrayList<>();

        if (match(TokenType.L_PAREN)) {
            params = parseFunctionArguments();
            expect(TokenType.R_PAREN);
        }

        return new Nodes.DataTypeExpr(type, params);
    }

    /**
     * Parses a comma-separated list of expressions.
     */
    private List<Expression> parseExpressionList() {
        List<Expression> expressions = new ArrayList<>();

        if (currentToken().type() != TokenType.R_PAREN) {
            do {
                expressions.add(parseExpression(0));
            } while (match(TokenType.COMMA));
        }

        return expressions;
    }

    /**
     * Parses function arguments with explicit R_PAREN termination.
     * Handles special cases like COUNT(*) and prevents infinite recursion.
     */
    private List<Expression> parseFunctionArguments() {
        List<Expression> args = new ArrayList<>();

        // Empty argument list (e.g., COUNT())
        if (currentToken().type() == TokenType.R_PAREN) {
            return args;
        }

        // Handle * (e.g., COUNT(*))
        if (match(TokenType.STAR)) {
            args.add(new Nodes.Identifier("*"));
            return args;
        }

        // Parse comma-separated arguments, stopping at R_PAREN
        do {
            // Safety check: if we see R_PAREN, stop parsing
            if (currentToken().type() == TokenType.R_PAREN) {
                break;
            }
            args.add(parseExpression(0));
        } while (match(TokenType.COMMA) && currentToken().type() != TokenType.R_PAREN);

        return args;
    }

    /**
     * Parses GROUP BY expressions, stopping at HAVING, WHERE, ORDER BY, LIMIT, OFFSET, or EOF.
     */
    private List<Expression> parseGroupByExpressions() {
        List<Expression> expressions = new ArrayList<>();

        while (!isAtEnd() && !isGroupByTerminator()) {
            expressions.add(parseExpression(0));

            if (!match(TokenType.COMMA)) {
                break;
            }
        }

        return expressions;
    }

    /**
     * Returns true if current token terminates a GROUP BY clause.
     */
    private boolean isGroupByTerminator() {
        TokenType type = currentToken().type();
        return type == TokenType.HAVING || type == TokenType.ORDER ||
               type == TokenType.LIMIT || type == TokenType.OFFSET ||
               type == TokenType.UNION || type == TokenType.INTERSECT ||
               type == TokenType.EXCEPT;
    }

    /**
     * Returns true if current token terminates an ORDER BY clause.
     */
    private boolean isOrderByTerminator() {
        TokenType type = currentToken().type();
        return type == TokenType.LIMIT || type == TokenType.OFFSET ||
               type == TokenType.UNION || type == TokenType.INTERSECT ||
               type == TokenType.EXCEPT;
    }

    /**
     * Gets the precedence of an operator.
     */
    private int getPrecedence(Token token) {
        return switch (token.type()) {
            case OR, XOR -> 1;
            case AND -> 2;
            case NOT -> 3;
            case EQ, NEQ, NEQ2, LT, GT, LTE, GTE, LIKE, ILIKE, IN, BETWEEN, IS -> 4;
            case PLUS, MINUS -> 5;
            case STAR, SLASH, PERCENT, MOD -> 6;
            default -> -1;
        };
    }

    /**
     * Creates a binary operation.
     */
    private Expression createBinaryOp(TokenType op, Expression left, Expression right) {
        return switch (op) {
            case PLUS -> new Nodes.Add(left, right);
            case MINUS -> new Nodes.Sub(left, right);
            case STAR -> new Nodes.Mul(left, right);
            case SLASH -> new Nodes.Div(left, right);
            case PERCENT, MOD -> new Nodes.Mod(left, right);
            case AND -> new Nodes.And(left, right);
            case OR, XOR -> new Nodes.Or(left, right);
            case EQ -> new Nodes.EQ(left, right);
            case NEQ, NEQ2 -> new Nodes.NEQ(left, right);
            case LT -> new Nodes.LT(left, right);
            case GT -> new Nodes.GT(left, right);
            case LTE -> new Nodes.LTE(left, right);
            case GTE -> new Nodes.GTE(left, right);
            default -> new Nodes.Or(left, right); // fallback
        };
    }

    // ============ HELPER METHODS ============

    private boolean isAtEnd() {
        return pos >= tokens.size() || (pos < tokens.size() && tokens.get(pos).type() == TokenType.EOF);
    }

    private Token currentToken() {
        if (pos >= tokens.size()) {
            return new Token(TokenType.EOF, "", 1, 1, pos, pos, List.of());
        }
        return tokens.get(pos);
    }

    private Token peek() {
        return pos + 1 >= tokens.size() ? new Token(TokenType.EOF, "", 1, 1, pos + 1, pos + 1, List.of()) : tokens.get(pos + 1);
    }

    private Token advance() {
        Token current = currentToken();
        if (!isAtEnd()) pos++;
        return current;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (currentToken().type() == type) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token expect(TokenType type) {
        if (currentToken().type() != type) {
            error("Expected " + type + " but got " + currentToken().type());
        }
        return advance();
    }

    private void skipSemicolon() {
        match(TokenType.SEMICOLON);
    }

    private void error(String message) {
        if (config.errorLevel() == ErrorLevel.IMMEDIATE) {
            throw new ParseException(message);
        }
        errors.add(new ParseError(message, currentToken().line(), currentToken().col(), ""));
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (currentToken().type() == TokenType.SEMICOLON) {
                advance();
                return;
            }

            switch (currentToken().type()) {
                case SELECT, INSERT, UPDATE, DELETE, CREATE, DROP -> {
                    return;
                }
            }

            advance();
        }
    }

    /**
     * Parse exception for immediate error handling.
     */
    public static class ParseException extends RuntimeException {
        public ParseException(String message) {
            super(message);
        }

        public ParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public List<ParseError> getErrors() {
        return errors;
    }
}
