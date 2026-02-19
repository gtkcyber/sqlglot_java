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
package io.sqlglot.generator;

import io.sqlglot.expressions.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generates SQL code from an Expression AST.
 * Implements the ExpressionVisitor pattern for type-safe traversal.
 */
public class Generator implements ExpressionVisitor<String> {
    private final GeneratorConfig config;

    public Generator() {
        this(GeneratorConfig.defaultConfig());
    }

    public Generator(GeneratorConfig config) {
        this.config = config;
    }

    /**
     * Generates SQL for an expression.
     */
    public String sql(Expression expr) {
        if (expr == null) {
            return "";
        }

        // Apply dialect-specific transforms
        Map<Class<? extends Expression>, Function<Expression, String>> transforms = transforms();
        Function<Expression, String> transform = transforms.get(expr.getClass());
        if (transform != null) {
            return transform.apply(expr);
        }

        // Default: use visitor
        return expr.accept(this);
    }

    /**
     * Override in subclasses to add dialect-specific transforms.
     */
    protected Map<Class<? extends Expression>, Function<Expression, String>> transforms() {
        return new LinkedHashMap<>();
    }

    /**
     * Formats an identifier (optionally quoted).
     */
    protected String formatIdentifier(String name) {
        if (config.identify()) {
            return "\"" + name.replace("\"", "\"\"") + "\"";
        }
        return name;
    }

    /**
     * Formats a keyword (optionally normalized to uppercase).
     */
    protected String formatKeyword(String keyword) {
        if (config.normalize()) {
            return keyword.toUpperCase();
        }
        return keyword;
    }

    // ============ VISITOR METHODS ============

    @Override
    public String visitLiteral(Nodes.Literal node) {
        if (node.isString()) {
            return "'" + node.getValue().replace("'", "''") + "'";
        }
        return node.getValue();
    }

    @Override
    public String visitNull(Nodes.Null node) {
        return formatKeyword("NULL");
    }

    @Override
    public String visitTrue(Nodes.True node) {
        return formatKeyword("TRUE");
    }

    @Override
    public String visitFalse(Nodes.False node) {
        return formatKeyword("FALSE");
    }

    @Override
    public String visitIdentifier(Nodes.Identifier node) {
        return formatIdentifier(node.getName());
    }

    @Override
    public String visitColumn(Nodes.Column node) {
        String result = sql(node.getName());
        if (node.getTable() != null) {
            result = sql(node.getTable()) + "." + result;
        }
        return result;
    }

    @Override
    public String visitTable(Nodes.Table node) {
        String result = node.getName();
        if (node.getSchema() != null) {
            result = node.getSchema() + "." + result;
        }
        return formatIdentifier(result);
    }

    @Override
    public String visitAlias(Nodes.Alias node) {
        return sql(node.getExpression()) + " " + formatKeyword("AS") + " " + formatIdentifier(node.getAlias());
    }

    @Override
    public String visitStar(Nodes.Star node) {
        return "*";
    }

    @Override
    public String visitAdd(Nodes.Add node) {
        return sql(node.getLeft()) + " + " + sql(node.getRight());
    }

    @Override
    public String visitSub(Nodes.Sub node) {
        return sql(node.getLeft()) + " - " + sql(node.getRight());
    }

    @Override
    public String visitMul(Nodes.Mul node) {
        return sql(node.getLeft()) + " * " + sql(node.getRight());
    }

    @Override
    public String visitDiv(Nodes.Div node) {
        return sql(node.getLeft()) + " / " + sql(node.getRight());
    }

    @Override
    public String visitMod(Nodes.Mod node) {
        return sql(node.getLeft()) + " % " + sql(node.getRight());
    }

    @Override
    public String visitAnd(Nodes.And node) {
        return sql(node.getLeft()) + " " + formatKeyword("AND") + " " + sql(node.getRight());
    }

    @Override
    public String visitOr(Nodes.Or node) {
        return sql(node.getLeft()) + " " + formatKeyword("OR") + " " + sql(node.getRight());
    }

    @Override
    public String visitNot(Nodes.Not node) {
        return formatKeyword("NOT") + " " + sql(node.getExpression());
    }

    @Override
    public String visitNot2(Nodes.Not2 node) {
        return "! " + sql(node.getExpression());
    }

    @Override
    public String visitEQ(Nodes.EQ node) {
        return sql(node.getLeft()) + " = " + sql(node.getRight());
    }

    @Override
    public String visitNEQ(Nodes.NEQ node) {
        return sql(node.getLeft()) + " <> " + sql(node.getRight());
    }

    @Override
    public String visitLT(Nodes.LT node) {
        return sql(node.getLeft()) + " < " + sql(node.getRight());
    }

    @Override
    public String visitGT(Nodes.GT node) {
        return sql(node.getLeft()) + " > " + sql(node.getRight());
    }

    @Override
    public String visitLTE(Nodes.LTE node) {
        return sql(node.getLeft()) + " <= " + sql(node.getRight());
    }

    @Override
    public String visitGTE(Nodes.GTE node) {
        return sql(node.getLeft()) + " >= " + sql(node.getRight());
    }

    @Override
    public String visitIs(Nodes.Is node) {
        return sql(node.getLeft()) + " " + formatKeyword("IS") + " " + sql(node.getRight());
    }

    @Override
    public String visitIsNull(Nodes.IsNull node) {
        return sql(node.getExpression()) + " " + formatKeyword("IS NULL");
    }

    @Override
    public String visitIsNotNull(Nodes.IsNotNull node) {
        return sql(node.getExpression()) + " " + formatKeyword("IS NOT NULL");
    }

    @Override
    public String visitLike(Nodes.Like node) {
        return sql(node.getLeft()) + " " + formatKeyword("LIKE") + " " + sql(node.getPattern());
    }

    @Override
    public String visitIn(Nodes.In node) {
        String values = node.getValues().stream()
            .map(this::sql)
            .collect(Collectors.joining(", "));
        return sql(node.getExpression()) + " " + formatKeyword("IN") + " (" + values + ")";
    }

    @Override
    public String visitBetween(Nodes.Between node) {
        return sql(node.getExpression()) + " " + formatKeyword("BETWEEN") + " " +
               sql(node.getLow()) + " " + formatKeyword("AND") + " " + sql(node.getHigh());
    }

    @Override
    public String visitExists(Nodes.Exists node) {
        return formatKeyword("EXISTS") + " (" + sql(node.getSubquery()) + ")";
    }

    @Override
    public String visitCase(Nodes.Case node) {
        StringBuilder sb = new StringBuilder(formatKeyword("CASE"));
        if (node.getExpr() != null) {
            sb.append(" ").append(sql(node.getExpr()));
        }
        for (Nodes.When when : node.getWhens()) {
            sb.append(" ").append(sql(when));
        }
        if (node.getDefaultExpr() != null) {
            sb.append(" ").append(formatKeyword("ELSE")).append(" ").append(sql(node.getDefaultExpr()));
        }
        sb.append(" ").append(formatKeyword("END"));
        return sb.toString();
    }

    @Override
    public String visitWhen(Nodes.When node) {
        return formatKeyword("WHEN") + " " + sql(node.getCondition()) + " " +
               formatKeyword("THEN") + " " + sql(node.getResult());
    }

    @Override
    public String visitCast(Nodes.Cast node) {
        return formatKeyword("CAST") + "(" + sql(node.getExpression()) + " " +
               formatKeyword("AS") + " " + sql(node.getDataType()) + ")";
    }

    @Override
    public String visitFunction(Nodes.Function node) {
        String args = node.getArgs().stream()
            .map(this::sql)
            .collect(Collectors.joining(", "));
        return node.getName().toUpperCase() + "(" + args + ")";
    }

    @Override
    public String visitDataType(Nodes.DataTypeExpr node) {
        if (node.getParams().isEmpty()) {
            return node.getType().toUpperCase();
        }
        String params = node.getParams().stream()
            .map(this::sql)
            .collect(Collectors.joining(", "));
        return node.getType().toUpperCase() + "(" + params + ")";
    }

    @Override
    public String visitSelect(Nodes.Select node) {
        StringBuilder sb = new StringBuilder();

        sb.append(formatKeyword("SELECT")).append(" ");
        if (node.isDistinct()) {
            sb.append(formatKeyword("DISTINCT")).append(" ");
        }

        String expressions = node.getExpressions().stream()
            .map(this::sql)
            .collect(Collectors.joining(", "));
        sb.append(expressions);

        if (node.getFrom() != null) {
            sb.append(" ").append(sql(node.getFrom()));
        }

        for (Nodes.Join join : node.getJoins()) {
            sb.append(" ").append(sql(join));
        }

        if (node.getWhere() != null) {
            sb.append(" ").append(sql(node.getWhere()));
        }

        if (node.getGroupBy() != null) {
            sb.append(" ").append(sql(node.getGroupBy()));
        }

        if (node.getHaving() != null) {
            sb.append(" ").append(sql(node.getHaving()));
        }

        if (!node.getOrderBy().isEmpty()) {
            String orderBy = node.getOrderBy().stream()
                .map(this::sql)
                .collect(Collectors.joining(", "));
            sb.append(" ").append(formatKeyword("ORDER BY")).append(" ").append(orderBy);
        }

        if (node.getLimit() != null) {
            sb.append(" ").append(sql(node.getLimit()));
        }

        if (node.getOffset() != null) {
            sb.append(" ").append(sql(node.getOffset()));
        }

        return sb.toString();
    }

    @Override
    public String visitDistinct(Nodes.Distinct node) {
        String expressions = node.getExpressions().stream()
            .map(this::sql)
            .collect(Collectors.joining(", "));
        return formatKeyword("DISTINCT") + " " + expressions;
    }

    @Override
    public String visitFrom(Nodes.From node) {
        return formatKeyword("FROM") + " " + sql(node.getTable());
    }

    @Override
    public String visitJoin(Nodes.Join node) {
        StringBuilder sb = new StringBuilder();
        sb.append(node.getType().toUpperCase()).append(" ").append(formatKeyword("JOIN")).append(" ");
        sb.append(sql(node.getTable()));
        if (node.getOn() != null) {
            sb.append(" ").append(formatKeyword("ON")).append(" ").append(sql(node.getOn()));
        }
        return sb.toString();
    }

    @Override
    public String visitWhere(Nodes.Where node) {
        return formatKeyword("WHERE") + " " + sql(node.getCondition());
    }

    @Override
    public String visitGroupBy(Nodes.GroupBy node) {
        String expressions = node.getExpressions().stream()
            .map(this::sql)
            .collect(Collectors.joining(", "));
        return formatKeyword("GROUP BY") + " " + expressions;
    }

    @Override
    public String visitHaving(Nodes.Having node) {
        return formatKeyword("HAVING") + " " + sql(node.getCondition());
    }

    @Override
    public String visitOrderBy(Nodes.OrderBy node) {
        return sql(node.getExpression()) + " " + node.getDirection().toUpperCase();
    }

    @Override
    public String visitOrder(Nodes.Order node) {
        return sql(node.getExpression()) + " " + node.getDirection().toUpperCase();
    }

    @Override
    public String visitLimit(Nodes.Limit node) {
        return formatKeyword("LIMIT") + " " + sql(node.getExpression());
    }

    @Override
    public String visitOffset(Nodes.Offset node) {
        return formatKeyword("OFFSET") + " " + sql(node.getExpression());
    }

    @Override
    public String visitParameter(Nodes.Parameter node) {
        return "@" + node.getName();
    }

    @Override
    public String visitPlaceholder(Nodes.Placeholder node) {
        return "?";
    }

    @Override
    public String visitSubquery(Nodes.Subquery node) {
        return "(" + sql(node.getSelect()) + ")";
    }

    @Override
    public String visitValues(Nodes.Values node) {
        String rows = node.getRows().stream()
            .map(this::sql)
            .collect(Collectors.joining(", "));
        return formatKeyword("VALUES") + " " + rows;
    }

    @Override
    public String visitRow(Nodes.Row node) {
        String expressions = node.getExpressions().stream()
            .map(this::sql)
            .collect(Collectors.joining(", "));
        return "(" + expressions + ")";
    }

    @Override
    public String visitInsert(Nodes.Insert node) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatKeyword("INSERT INTO")).append(" ").append(sql(node.getTable()));
        if (!node.getColumns().isEmpty()) {
            String cols = node.getColumns().stream().map(this::sql).collect(Collectors.joining(", "));
            sb.append(" (").append(cols).append(")");
        }
        sb.append(" ").append(sql(node.getValues()));
        return sb.toString();
    }

    @Override
    public String visitUpdate(Nodes.Update node) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatKeyword("UPDATE")).append(" ").append(sql(node.getTable()));
        sb.append(" ").append(formatKeyword("SET")).append(" ");
        String setClause = node.getSet().entrySet().stream()
            .map(e -> sql(e.getKey()) + " = " + sql(e.getValue()))
            .collect(Collectors.joining(", "));
        sb.append(setClause);
        if (node.getWhere() != null) {
            sb.append(" ").append(sql(node.getWhere()));
        }
        return sb.toString();
    }

    @Override
    public String visitDelete(Nodes.Delete node) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatKeyword("DELETE FROM")).append(" ").append(sql(node.getTable()));
        if (node.getWhere() != null) {
            sb.append(" ").append(sql(node.getWhere()));
        }
        return sb.toString();
    }

    @Override
    public String visitCreate(Nodes.Create node) {
        return formatKeyword("CREATE") + " " + node.getType().toUpperCase() + " " + node.getName();
    }

    @Override
    public String visitDrop(Nodes.Drop node) {
        return formatKeyword("DROP") + " " + node.getType().toUpperCase() + " " + node.getName();
    }

    @Override
    public String visitAlter(Nodes.Alter node) {
        return formatKeyword("ALTER") + " " + node.getType().toUpperCase() + " " + node.getName();
    }

    @Override
    public String visitWith(Nodes.With node) {
        String ctes = node.getCtes().stream()
            .map(this::sql)
            .collect(Collectors.joining(", "));
        return formatKeyword("WITH") + " " + ctes + " " + sql(node.getSelect());
    }

    @Override
    public String visitCTE(Nodes.CTE node) {
        return node.getName() + " " + formatKeyword("AS") + " (" + sql(node.getSelect()) + ")";
    }

    @Override
    public String visitWindow(Nodes.Window node) {
        StringBuilder sb = new StringBuilder(sql(node.getExpression()));
        sb.append(" ").append(formatKeyword("OVER")).append(" (");
        if (node.getPartitionBy() != null) {
            sb.append(sql(node.getPartitionBy()));
        }
        if (node.getOrderBy() != null) {
            sb.append(" ").append(sql(node.getOrderBy()));
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String visitPartitionBy(Nodes.PartitionBy node) {
        String expressions = node.getExpressions().stream()
            .map(this::sql)
            .collect(Collectors.joining(", "));
        return formatKeyword("PARTITION BY") + " " + expressions;
    }

    @Override
    public String visitBracket(Nodes.Bracket node) {
        String offsets = node.getOffset().stream()
            .map(this::sql)
            .collect(Collectors.joining(", "));
        return sql(node.getExpression()) + "[" + offsets + "]";
    }

    @Override
    public String visitDot(Nodes.Dot node) {
        return sql(node.getLeft()) + "." + sql(node.getRight());
    }

    @Override
    public String visitParen(Nodes.Paren node) {
        return "(" + sql(node.getExpression()) + ")";
    }

    @Override
    public String visitArray(Nodes.Array node) {
        String expressions = node.getExpressions().stream()
            .map(this::sql)
            .collect(Collectors.joining(", "));
        return "[" + expressions + "]";
    }

    @Override
    public String visitMap(Nodes.MapLiteral node) {
        String map = node.getMapValue().entrySet().stream()
            .map(e -> sql(e.getKey()) + ": " + sql(e.getValue()))
            .collect(Collectors.joining(", "));
        return "{" + map + "}";
    }

    @Override
    public String visitStruct(Nodes.Struct node) {
        String fields = node.getFields().entrySet().stream()
            .map(e -> e.getKey() + ": " + sql(e.getValue()))
            .collect(Collectors.joining(", "));
        return "STRUCT<" + fields + ">";
    }

    @Override
    public String visitJSON(Nodes.JSON node) {
        return node.getJson();
    }

    @Override
    public String visitTypedExpr(Nodes.TypedExpr node) {
        return sql(node.getExpression()) + "::" + sql(node.getType());
    }

    @Override
    public String visitUnnest(Nodes.Unnest node) {
        return formatKeyword("UNNEST") + "(" + sql(node.getExpression()) + ")";
    }

    @Override
    public String visitLateral(Nodes.Lateral node) {
        return formatKeyword("LATERAL") + " " + sql(node.getExpression());
    }

    @Override
    public String visitUnique(Nodes.Unique node) {
        return formatKeyword("UNIQUE") + "(" + sql(node.getExpression()) + ")";
    }

    @Override
    public String visitUnion(Nodes.Union node) {
        return sql(node.getLeft()) + " " + formatKeyword("UNION") +
               (node.isDistinct() ? "" : " " + formatKeyword("ALL")) + " " + sql(node.getRight());
    }

    @Override
    public String visitIntersect(Nodes.Intersect node) {
        return sql(node.getLeft()) + " " + formatKeyword("INTERSECT") + " " + sql(node.getRight());
    }

    @Override
    public String visitExcept(Nodes.Except node) {
        return sql(node.getLeft()) + " " + formatKeyword("EXCEPT") + " " + sql(node.getRight());
    }

    @Override
    public String visitPrimaryKey(Nodes.PrimaryKey node) {
        String cols = node.getColumns().stream()
            .map(this::sql)
            .collect(Collectors.joining(", "));
        return formatKeyword("PRIMARY KEY") + " (" + cols + ")";
    }

    @Override
    public String visitForeignKey(Nodes.ForeignKey node) {
        String cols = node.getColumns().stream()
            .map(this::sql)
            .collect(Collectors.joining(", "));
        return formatKeyword("FOREIGN KEY") + " (" + cols + ") " + formatKeyword("REFERENCES") + " " + node.getRefTable();
    }

    @Override
    public String visitExplain(Nodes.Explain node) {
        return formatKeyword("EXPLAIN") + " " + sql(node.getExpression());
    }

    @Override
    public String visitComment(Nodes.Comment node) {
        return "-- " + node.getText();
    }

    @Override
    public String visitHint(Nodes.Hint node) {
        return "/*+ " + node.getHint() + " */";
    }

    @Override
    public String visitColumnDef(Nodes.ColumnDef node) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatIdentifier(node.getName())).append(" ").append(sql(node.getType()));
        if (!node.isNullable()) {
            sb.append(" ").append(formatKeyword("NOT NULL"));
        }
        if (node.getDefaultValue() != null) {
            sb.append(" ").append(formatKeyword("DEFAULT")).append(" ").append(sql(node.getDefaultValue()));
        }
        return sb.toString();
    }

    @Override
    public String visitWorkspacePath(Nodes.WorkspacePath node) {
        return "`" + String.join("`.`", node.getPath()) + "`";
    }
}
