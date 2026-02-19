package io.sqlglot.dialects.drill;

import io.sqlglot.expressions.Nodes;
import io.sqlglot.parser.Parser;
import io.sqlglot.parser.ParserConfig;

/**
 * Apache Drill-specific parser.
 *
 * Extends the base parser to support Drill-specific syntax:
 * - Workspace.schema.table syntax
 * - FLATTEN function
 * - Complex nested data types
 * - Dynamic SQL
 */
public class DrillParser extends Parser {
    public DrillParser() {
        super(ParserConfig.defaultConfig());
    }

    @Override
    protected Expression parseTableReference() {
        // First, try to parse a workspace path
        // Format: workspace.schema.table or schema.table or table
        Expression expr = parsePrimary();

        // Handle workspace/schema path syntax
        while (peekToken().type() == io.sqlglot.tokens.TokenType.DOT) {
            advance(); // consume DOT
            Expression next = parsePrimary();
            expr = new Nodes.Dot(expr, next);
        }

        // Handle alias
        if (match(io.sqlglot.tokens.TokenType.AS)) {
            String alias = expect(io.sqlglot.tokens.TokenType.IDENTIFIER).text();
            expr = new Nodes.Alias(expr, alias);
        }

        return expr;
    }

    /**
     * Parses FLATTEN function which is Drill-specific.
     * FLATTEN converts an array or map into individual rows.
     */
    protected Expression parseFlattenFunction() {
        // This would be called when FLATTEN is recognized
        // FLATTEN(expression [, depth])
        expect(io.sqlglot.tokens.TokenType.IDENTIFIER); // FLATTEN
        expect(io.sqlglot.tokens.TokenType.L_PAREN);

        Expression expr = parseExpression(0);

        Expression depth = null;
        if (match(io.sqlglot.tokens.TokenType.COMMA)) {
            depth = parseExpression(0);
        }

        expect(io.sqlglot.tokens.TokenType.R_PAREN);

        // Return as a function call for now
        java.util.List<io.sqlglot.expressions.Expression> args = new java.util.ArrayList<>();
        args.add(expr);
        if (depth != null) {
            args.add(depth);
        }

        return new Nodes.Function("FLATTEN", args);
    }

    private io.sqlglot.tokens.Token peekToken() {
        return peek();
    }

    private Expression parseExpression(int i) {
        return super.parseExpression(i);
    }

    private boolean match(io.sqlglot.tokens.TokenType type) {
        return super.match(type);
    }

    private io.sqlglot.tokens.Token expect(io.sqlglot.tokens.TokenType type) {
        return super.expect(type);
    }

    private void advance() {
        super.advance();
    }

    private Expression parsePrimary() {
        return super.parsePrimary();
    }

    private static class Expression extends io.sqlglot.expressions.Expression {
        @Override
        public java.util.Map<String, Object> args() { return java.util.Map.of(); }

        @Override
        public <R> R accept(io.sqlglot.expressions.ExpressionVisitor<R> visitor) { return visitor.visitUnknown(this); }

        @Override
        protected io.sqlglot.expressions.Expression copyWithArgs(java.util.Map<String, Object> newArgs) { return this; }
    }
}
