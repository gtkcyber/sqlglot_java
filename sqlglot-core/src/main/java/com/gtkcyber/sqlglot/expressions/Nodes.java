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
package com.gtkcyber.sqlglot.expressions;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * Container for all SQL expression node types as nested static classes.
 * Each node type extends Expression and implements visitor pattern.
 */
public final class Nodes {
    private Nodes() { /* utility class */ }

    // ============ LITERALS ============

    public static final class Literal extends Expression {
        private final String value;
        private final boolean isString;

        public Literal(String value, boolean isString) {
            this.value = Objects.requireNonNull(value);
            this.isString = isString;
        }

        public String getValue() { return value; }
        public boolean isString() { return isString; }

        @Override
        public Map<String, Object> args() {
            return Map.of("value", value, "isString", isString);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitLiteral(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Literal(
                (String) newArgs.getOrDefault("value", value),
                (boolean) newArgs.getOrDefault("isString", isString)
            );
        }

        @Override
        public String toString() {
            return isString ? "'" + value + "'" : value;
        }
    }

    public static final class Null extends Expression {
        public static final Null INSTANCE = new Null();

        @Override
        public Map<String, Object> args() { return Map.of(); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitNull(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return this;
        }

        @Override
        public String toString() { return "NULL"; }
    }

    public static final class True extends Expression {
        public static final True INSTANCE = new True();

        @Override
        public Map<String, Object> args() { return Map.of(); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitTrue(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return this;
        }

        @Override
        public String toString() { return "TRUE"; }
    }

    public static final class False extends Expression {
        public static final False INSTANCE = new False();

        @Override
        public Map<String, Object> args() { return Map.of(); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitFalse(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return this;
        }

        @Override
        public String toString() { return "FALSE"; }
    }

    // ============ IDENTIFIERS ============

    public static final class Identifier extends Expression {
        private final String name;
        private final boolean quoted;

        public Identifier(String name, boolean quoted) {
            this.name = Objects.requireNonNull(name);
            this.quoted = quoted;
        }

        public Identifier(String name) {
            this(name, false);
        }

        public String getName() { return name; }
        public boolean isQuoted() { return quoted; }

        @Override
        public Map<String, Object> args() {
            return Map.of("name", name, "quoted", quoted);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitIdentifier(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Identifier(
                (String) newArgs.getOrDefault("name", name),
                (boolean) newArgs.getOrDefault("quoted", quoted)
            );
        }

        @Override
        public String toString() {
            return quoted ? "`" + name + "`" : name;
        }
    }

    public static final class Column extends Expression {
        private final Expression table;
        private final Expression name;

        public Column(Expression name) {
            this(null, name);
        }

        public Column(Expression table, Expression name) {
            this.table = table;
            this.name = Objects.requireNonNull(name);
        }

        public Expression getTable() { return table; }
        public Expression getName() { return name; }

        @Override
        public Map<String, Object> args() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("table", table);
            map.put("name", name);
            return map;
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitColumn(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Column(
                (Expression) newArgs.get("table"),
                (Expression) newArgs.get("name")
            );
        }

        @Override
        public String toString() {
            return table != null ? table + "." + name : name.toString();
        }
    }

    public static final class Table extends Expression {
        private final String name;
        private final String schema;

        public Table(String name) {
            this(name, null);
        }

        public Table(String name, String schema) {
            this.name = Objects.requireNonNull(name);
            this.schema = schema;
        }

        public String getName() { return name; }
        public String getSchema() { return schema; }

        @Override
        public Map<String, Object> args() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("name", name);
            map.put("schema", schema);
            return map;
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitTable(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Table(
                (String) newArgs.getOrDefault("name", name),
                (String) newArgs.get("schema")
            );
        }

        @Override
        public String toString() {
            return schema != null ? schema + "." + name : name;
        }
    }

    public static final class Alias extends Expression {
        private final Expression expression;
        private final String alias;

        public Alias(Expression expression, String alias) {
            this.expression = Objects.requireNonNull(expression);
            this.alias = Objects.requireNonNull(alias);
        }

        public Expression getExpression() { return expression; }
        public String getAlias() { return alias; }

        @Override
        public Map<String, Object> args() {
            return Map.of("expression", expression, "alias", alias);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitAlias(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Alias(
                (Expression) newArgs.get("expression"),
                (String) newArgs.get("alias")
            );
        }

        @Override
        public String toString() {
            return expression + " AS " + alias;
        }
    }

    public static final class Star extends Expression {
        public static final Star INSTANCE = new Star();

        @Override
        public Map<String, Object> args() { return Map.of(); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitStar(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return this;
        }

        @Override
        public String toString() { return "*"; }
    }

    // ============ BINARY OPERATORS ============

    public static abstract class Binary extends Expression {
        protected final Expression left;
        protected final Expression right;

        protected Binary(Expression left, Expression right) {
            this.left = Objects.requireNonNull(left);
            this.right = Objects.requireNonNull(right);
        }

        public Expression getLeft() { return left; }
        public Expression getRight() { return right; }

        @Override
        public Map<String, Object> args() {
            return Map.of("left", left, "right", right);
        }

        protected abstract Expression createCopy(Expression left, Expression right);

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return createCopy(
                (Expression) newArgs.get("left"),
                (Expression) newArgs.get("right")
            );
        }
    }

    public static final class Add extends Binary {
        public Add(Expression left, Expression right) { super(left, right); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitAdd(this); }

        @Override
        protected Expression createCopy(Expression left, Expression right) {
            return new Add(left, right);
        }

        @Override
        public String toString() { return left + " + " + right; }
    }

    public static final class Sub extends Binary {
        public Sub(Expression left, Expression right) { super(left, right); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitSub(this); }

        @Override
        protected Expression createCopy(Expression left, Expression right) {
            return new Sub(left, right);
        }

        @Override
        public String toString() { return left + " - " + right; }
    }

    public static final class Mul extends Binary {
        public Mul(Expression left, Expression right) { super(left, right); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitMul(this); }

        @Override
        protected Expression createCopy(Expression left, Expression right) {
            return new Mul(left, right);
        }

        @Override
        public String toString() { return left + " * " + right; }
    }

    public static final class Div extends Binary {
        public Div(Expression left, Expression right) { super(left, right); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitDiv(this); }

        @Override
        protected Expression createCopy(Expression left, Expression right) {
            return new Div(left, right);
        }

        @Override
        public String toString() { return left + " / " + right; }
    }

    public static final class Mod extends Binary {
        public Mod(Expression left, Expression right) { super(left, right); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitMod(this); }

        @Override
        protected Expression createCopy(Expression left, Expression right) {
            return new Mod(left, right);
        }

        @Override
        public String toString() { return left + " % " + right; }
    }

    public static final class And extends Binary {
        public And(Expression left, Expression right) { super(left, right); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitAnd(this); }

        @Override
        protected Expression createCopy(Expression left, Expression right) {
            return new And(left, right);
        }

        @Override
        public String toString() { return left + " AND " + right; }
    }

    public static final class Or extends Binary {
        public Or(Expression left, Expression right) { super(left, right); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitOr(this); }

        @Override
        protected Expression createCopy(Expression left, Expression right) {
            return new Or(left, right);
        }

        @Override
        public String toString() { return left + " OR " + right; }
    }

    public static final class EQ extends Binary {
        public EQ(Expression left, Expression right) { super(left, right); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitEQ(this); }

        @Override
        protected Expression createCopy(Expression left, Expression right) {
            return new EQ(left, right);
        }

        @Override
        public String toString() { return left + " = " + right; }
    }

    public static final class NEQ extends Binary {
        public NEQ(Expression left, Expression right) { super(left, right); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitNEQ(this); }

        @Override
        protected Expression createCopy(Expression left, Expression right) {
            return new NEQ(left, right);
        }

        @Override
        public String toString() { return left + " <> " + right; }
    }

    public static final class LT extends Binary {
        public LT(Expression left, Expression right) { super(left, right); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitLT(this); }

        @Override
        protected Expression createCopy(Expression left, Expression right) {
            return new LT(left, right);
        }

        @Override
        public String toString() { return left + " < " + right; }
    }

    public static final class GT extends Binary {
        public GT(Expression left, Expression right) { super(left, right); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitGT(this); }

        @Override
        protected Expression createCopy(Expression left, Expression right) {
            return new GT(left, right);
        }

        @Override
        public String toString() { return left + " > " + right; }
    }

    public static final class LTE extends Binary {
        public LTE(Expression left, Expression right) { super(left, right); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitLTE(this); }

        @Override
        protected Expression createCopy(Expression left, Expression right) {
            return new LTE(left, right);
        }

        @Override
        public String toString() { return left + " <= " + right; }
    }

    public static final class GTE extends Binary {
        public GTE(Expression left, Expression right) { super(left, right); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitGTE(this); }

        @Override
        protected Expression createCopy(Expression left, Expression right) {
            return new GTE(left, right);
        }

        @Override
        public String toString() { return left + " >= " + right; }
    }

    public static final class Is extends Binary {
        public Is(Expression left, Expression right) { super(left, right); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitIs(this); }

        @Override
        protected Expression createCopy(Expression left, Expression right) {
            return new Is(left, right);
        }

        @Override
        public String toString() { return left + " IS " + right; }
    }

    // ============ UNARY OPERATORS ============

    public static final class Not extends Expression {
        private final Expression expression;

        public Not(Expression expression) {
            this.expression = Objects.requireNonNull(expression);
        }

        public Expression getExpression() { return expression; }

        @Override
        public Map<String, Object> args() {
            return Map.of("expression", expression);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitNot(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Not((Expression) newArgs.get("expression"));
        }

        @Override
        public String toString() { return "NOT " + expression; }
    }

    public static final class Not2 extends Expression {
        private final Expression expression;

        public Not2(Expression expression) {
            this.expression = Objects.requireNonNull(expression);
        }

        public Expression getExpression() { return expression; }

        @Override
        public Map<String, Object> args() {
            return Map.of("expression", expression);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitNot2(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Not2((Expression) newArgs.get("expression"));
        }

        @Override
        public String toString() { return "!" + expression; }
    }

    // ============ COMPARISONS ============

    public static final class IsNull extends Expression {
        private final Expression expression;

        public IsNull(Expression expression) {
            this.expression = Objects.requireNonNull(expression);
        }

        public Expression getExpression() { return expression; }

        @Override
        public Map<String, Object> args() {
            return Map.of("expression", expression);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitIsNull(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new IsNull((Expression) newArgs.get("expression"));
        }

        @Override
        public String toString() { return expression + " IS NULL"; }
    }

    public static final class IsNotNull extends Expression {
        private final Expression expression;

        public IsNotNull(Expression expression) {
            this.expression = Objects.requireNonNull(expression);
        }

        public Expression getExpression() { return expression; }

        @Override
        public Map<String, Object> args() {
            return Map.of("expression", expression);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitIsNotNull(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new IsNotNull((Expression) newArgs.get("expression"));
        }

        @Override
        public String toString() { return expression + " IS NOT NULL"; }
    }

    public static final class Like extends Expression {
        private final Expression left;
        private final Expression pattern;

        public Like(Expression left, Expression pattern) {
            this.left = Objects.requireNonNull(left);
            this.pattern = Objects.requireNonNull(pattern);
        }

        public Expression getLeft() { return left; }
        public Expression getPattern() { return pattern; }

        @Override
        public Map<String, Object> args() {
            return Map.of("left", left, "pattern", pattern);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitLike(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Like(
                (Expression) newArgs.get("left"),
                (Expression) newArgs.get("pattern")
            );
        }

        @Override
        public String toString() { return left + " LIKE " + pattern; }
    }

    public static final class In extends Expression {
        private final Expression expression;
        private final List<Expression> values;

        public In(Expression expression, List<Expression> values) {
            this.expression = Objects.requireNonNull(expression);
            this.values = Objects.requireNonNull(values);
        }

        public Expression getExpression() { return expression; }
        public List<Expression> getValues() { return values; }

        @Override
        public Map<String, Object> args() {
            return Map.of("expression", expression, "values", values);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitIn(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new In(
                (Expression) newArgs.get("expression"),
                (List<Expression>) newArgs.get("values")
            );
        }

        @Override
        public String toString() { return expression + " IN (" + values + ")"; }
    }

    public static final class Between extends Expression {
        private final Expression expression;
        private final Expression low;
        private final Expression high;

        public Between(Expression expression, Expression low, Expression high) {
            this.expression = Objects.requireNonNull(expression);
            this.low = Objects.requireNonNull(low);
            this.high = Objects.requireNonNull(high);
        }

        public Expression getExpression() { return expression; }
        public Expression getLow() { return low; }
        public Expression getHigh() { return high; }

        @Override
        public Map<String, Object> args() {
            return Map.of("expression", expression, "low", low, "high", high);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitBetween(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Between(
                (Expression) newArgs.get("expression"),
                (Expression) newArgs.get("low"),
                (Expression) newArgs.get("high")
            );
        }

        @Override
        public String toString() { return expression + " BETWEEN " + low + " AND " + high; }
    }

    public static final class Exists extends Expression {
        private final Expression subquery;

        public Exists(Expression subquery) {
            this.subquery = Objects.requireNonNull(subquery);
        }

        public Expression getSubquery() { return subquery; }

        @Override
        public Map<String, Object> args() {
            return Map.of("subquery", subquery);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitExists(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Exists((Expression) newArgs.get("subquery"));
        }

        @Override
        public String toString() { return "EXISTS (" + subquery + ")"; }
    }

    // ============ FUNCTIONS ============

    public static final class Function extends Expression {
        private final String name;
        private final List<Expression> args;

        public Function(String name, List<Expression> args) {
            this.name = Objects.requireNonNull(name);
            this.args = Objects.requireNonNull(args);
        }

        public String getName() { return name; }
        public List<Expression> getArgs() { return args; }

        @Override
        public Map<String, Object> args() {
            return Map.of("name", name, "args", args);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitFunction(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Function(
                (String) newArgs.get("name"),
                (List<Expression>) newArgs.get("args")
            );
        }

        @Override
        public String toString() { return name + "(" + args + ")"; }
    }

    public static final class Cast extends Expression {
        private final Expression expression;
        private final DataTypeExpr dataType;

        public Cast(Expression expression, DataTypeExpr dataType) {
            this.expression = Objects.requireNonNull(expression);
            this.dataType = Objects.requireNonNull(dataType);
        }

        public Expression getExpression() { return expression; }
        public DataTypeExpr getDataType() { return dataType; }

        @Override
        public Map<String, Object> args() {
            return Map.of("expression", expression, "dataType", dataType);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitCast(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Cast(
                (Expression) newArgs.get("expression"),
                (DataTypeExpr) newArgs.get("dataType")
            );
        }

        @Override
        public String toString() { return "CAST(" + expression + " AS " + dataType + ")"; }
    }

    // ============ CONTROL FLOW ============

    public static final class Case extends Expression {
        private final Expression expr;
        private final List<When> whens;
        private final Expression defaultExpr;

        public Case(Expression expr, List<When> whens, Expression defaultExpr) {
            this.expr = expr;
            this.whens = Objects.requireNonNull(whens);
            this.defaultExpr = defaultExpr;
        }

        public Expression getExpr() { return expr; }
        public List<When> getWhens() { return whens; }
        public Expression getDefaultExpr() { return defaultExpr; }

        @Override
        public Map<String, Object> args() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("expr", expr);
            map.put("whens", whens);
            map.put("defaultExpr", defaultExpr);
            return map;
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitCase(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Case(
                (Expression) newArgs.get("expr"),
                (List<When>) newArgs.get("whens"),
                (Expression) newArgs.get("defaultExpr")
            );
        }

        @Override
        public String toString() { return "CASE " + expr + " " + whens + " END"; }
    }

    public static final class When extends Expression {
        private final Expression condition;
        private final Expression result;

        public When(Expression condition, Expression result) {
            this.condition = Objects.requireNonNull(condition);
            this.result = Objects.requireNonNull(result);
        }

        public Expression getCondition() { return condition; }
        public Expression getResult() { return result; }

        @Override
        public Map<String, Object> args() {
            return Map.of("condition", condition, "result", result);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitWhen(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new When(
                (Expression) newArgs.get("condition"),
                (Expression) newArgs.get("result")
            );
        }

        @Override
        public String toString() { return "WHEN " + condition + " THEN " + result; }
    }

    // ============ DATA TYPES ============

    public static final class DataTypeExpr extends Expression {
        private final String type;
        private final List<Expression> params;

        public DataTypeExpr(String type) {
            this(type, List.of());
        }

        public DataTypeExpr(String type, List<Expression> params) {
            this.type = Objects.requireNonNull(type);
            this.params = Objects.requireNonNull(params);
        }

        public String getType() { return type; }
        public List<Expression> getParams() { return params; }

        @Override
        public Map<String, Object> args() {
            return Map.of("type", type, "params", params);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitDataType(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new DataTypeExpr(
                (String) newArgs.get("type"),
                (List<Expression>) newArgs.get("params")
            );
        }

        @Override
        public String toString() { return params.isEmpty() ? type : type + params; }
    }

    // ============ SELECTION ============

    public static final class Select extends Expression {
        private final List<Expression> expressions;
        private final boolean distinct;
        private final From from;
        private final List<Join> joins;
        private final Where where;
        private final GroupBy groupBy;
        private final Having having;
        private final List<OrderBy> orderBy;
        private final Limit limit;
        private final Offset offset;

        public Select(List<Expression> expressions) {
            this(expressions, false, null, null, null, null, null, null, null, null);
        }

        public Select(List<Expression> expressions, boolean distinct, From from, List<Join> joins, Where where,
                      GroupBy groupBy, Having having, List<OrderBy> orderBy, Limit limit, Offset offset) {
            this.expressions = Objects.requireNonNull(expressions);
            this.distinct = distinct;
            this.from = from;
            this.joins = joins != null ? joins : List.of();
            this.where = where;
            this.groupBy = groupBy;
            this.having = having;
            this.orderBy = orderBy != null ? orderBy : List.of();
            this.limit = limit;
            this.offset = offset;
        }

        public List<Expression> getExpressions() { return expressions; }
        public boolean isDistinct() { return distinct; }
        public From getFrom() { return from; }
        public List<Join> getJoins() { return joins; }
        public Where getWhere() { return where; }
        public GroupBy getGroupBy() { return groupBy; }
        public Having getHaving() { return having; }
        public List<OrderBy> getOrderBy() { return orderBy; }
        public Limit getLimit() { return limit; }
        public Offset getOffset() { return offset; }

        @Override
        public Map<String, Object> args() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("expressions", expressions);
            map.put("distinct", distinct);
            map.put("from", from);
            map.put("joins", joins);
            map.put("where", where);
            map.put("groupBy", groupBy);
            map.put("having", having);
            map.put("orderBy", orderBy);
            map.put("limit", limit);
            map.put("offset", offset);
            return map;
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitSelect(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Select(
                (List<Expression>) newArgs.get("expressions"),
                (boolean) newArgs.getOrDefault("distinct", distinct),
                (From) newArgs.get("from"),
                (List<Join>) newArgs.get("joins"),
                (Where) newArgs.get("where"),
                (GroupBy) newArgs.get("groupBy"),
                (Having) newArgs.get("having"),
                (List<OrderBy>) newArgs.get("orderBy"),
                (Limit) newArgs.get("limit"),
                (Offset) newArgs.get("offset")
            );
        }

        @Override
        public String toString() { return "SELECT " + (distinct ? "DISTINCT " : "") + expressions; }
    }

    public static final class From extends Expression {
        private final Expression table;

        public From(Expression table) {
            this.table = Objects.requireNonNull(table);
        }

        public Expression getTable() { return table; }

        @Override
        public Map<String, Object> args() {
            return Map.of("table", table);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitFrom(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new From((Expression) newArgs.get("table"));
        }

        @Override
        public String toString() { return "FROM " + table; }
    }

    public static final class Join extends Expression {
        private final String type;
        private final Expression table;
        private final Expression on;

        public Join(String type, Expression table, Expression on) {
            this.type = Objects.requireNonNull(type);
            this.table = Objects.requireNonNull(table);
            this.on = on;
        }

        public String getType() { return type; }
        public Expression getTable() { return table; }
        public Expression getOn() { return on; }

        @Override
        public Map<String, Object> args() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", type);
            map.put("table", table);
            map.put("on", on);
            return map;
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitJoin(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Join(
                (String) newArgs.get("type"),
                (Expression) newArgs.get("table"),
                (Expression) newArgs.get("on")
            );
        }

        @Override
        public String toString() { return type + " JOIN " + table + " ON " + on; }
    }

    public static final class Where extends Expression {
        private final Expression condition;

        public Where(Expression condition) {
            this.condition = Objects.requireNonNull(condition);
        }

        public Expression getCondition() { return condition; }

        @Override
        public Map<String, Object> args() {
            return Map.of("condition", condition);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitWhere(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Where((Expression) newArgs.get("condition"));
        }

        @Override
        public String toString() { return "WHERE " + condition; }
    }

    public static final class GroupBy extends Expression {
        private final List<Expression> expressions;

        public GroupBy(List<Expression> expressions) {
            this.expressions = Objects.requireNonNull(expressions);
        }

        public List<Expression> getExpressions() { return expressions; }

        @Override
        public Map<String, Object> args() {
            return Map.of("expressions", expressions);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitGroupBy(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new GroupBy((List<Expression>) newArgs.get("expressions"));
        }

        @Override
        public String toString() { return "GROUP BY " + expressions; }
    }

    public static final class Having extends Expression {
        private final Expression condition;

        public Having(Expression condition) {
            this.condition = Objects.requireNonNull(condition);
        }

        public Expression getCondition() { return condition; }

        @Override
        public Map<String, Object> args() {
            return Map.of("condition", condition);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitHaving(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Having((Expression) newArgs.get("condition"));
        }

        @Override
        public String toString() { return "HAVING " + condition; }
    }

    public static final class OrderBy extends Expression {
        private final Expression expression;
        private final String direction;

        public OrderBy(Expression expression, String direction) {
            this.expression = Objects.requireNonNull(expression);
            this.direction = direction != null ? direction : "ASC";
        }

        public Expression getExpression() { return expression; }
        public String getDirection() { return direction; }

        @Override
        public Map<String, Object> args() {
            return Map.of("expression", expression, "direction", direction);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitOrderBy(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new OrderBy(
                (Expression) newArgs.get("expression"),
                (String) newArgs.get("direction")
            );
        }

        @Override
        public String toString() { return expression + " " + direction; }
    }

    public static final class Order extends Expression {
        private final Expression expression;
        private final String direction;

        public Order(Expression expression, String direction) {
            this.expression = Objects.requireNonNull(expression);
            this.direction = direction != null ? direction : "ASC";
        }

        public Expression getExpression() { return expression; }
        public String getDirection() { return direction; }

        @Override
        public Map<String, Object> args() {
            return Map.of("expression", expression, "direction", direction);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitOrder(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Order(
                (Expression) newArgs.get("expression"),
                (String) newArgs.get("direction")
            );
        }

        @Override
        public String toString() { return expression + " " + direction; }
    }

    public static final class Limit extends Expression {
        private final Expression expression;

        public Limit(Expression expression) {
            this.expression = Objects.requireNonNull(expression);
        }

        public Expression getExpression() { return expression; }

        @Override
        public Map<String, Object> args() {
            return Map.of("expression", expression);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitLimit(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Limit((Expression) newArgs.get("expression"));
        }

        @Override
        public String toString() { return "LIMIT " + expression; }
    }

    public static final class Offset extends Expression {
        private final Expression expression;

        public Offset(Expression expression) {
            this.expression = Objects.requireNonNull(expression);
        }

        public Expression getExpression() { return expression; }

        @Override
        public Map<String, Object> args() {
            return Map.of("expression", expression);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitOffset(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Offset((Expression) newArgs.get("expression"));
        }

        @Override
        public String toString() { return "OFFSET " + expression; }
    }

    // ============ PLACEHOLDERS ============

    public static final class Parameter extends Expression {
        private final String name;

        public Parameter(String name) {
            this.name = Objects.requireNonNull(name);
        }

        public String getName() { return name; }

        @Override
        public Map<String, Object> args() {
            return Map.of("name", name);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitParameter(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Parameter((String) newArgs.get("name"));
        }

        @Override
        public String toString() { return "@" + name; }
    }

    public static final class Placeholder extends Expression {
        public static final Placeholder INSTANCE = new Placeholder();

        @Override
        public Map<String, Object> args() { return Map.of(); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitPlaceholder(this);
        }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return this;
        }

        @Override
        public String toString() { return "?"; }
    }

    // ============ ADVANCED NODES (Stubs for now) ============

    public static final class Distinct extends Expression {
        private final List<Expression> expressions;

        public Distinct(List<Expression> expressions) {
            this.expressions = Objects.requireNonNull(expressions);
        }

        public List<Expression> getExpressions() { return expressions; }

        @Override
        public Map<String, Object> args() { return Map.of("expressions", expressions); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitDistinct(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Distinct((List<Expression>) newArgs.get("expressions"));
        }

        @Override
        public String toString() { return "DISTINCT " + expressions; }
    }

    public static final class Subquery extends Expression {
        private final Select select;

        public Subquery(Select select) {
            this.select = Objects.requireNonNull(select);
        }

        public Select getSelect() { return select; }

        @Override
        public Map<String, Object> args() { return Map.of("select", select); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitSubquery(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Subquery((Select) newArgs.get("select"));
        }

        @Override
        public String toString() { return "(" + select + ")"; }
    }

    public static final class Values extends Expression {
        private final List<Row> rows;

        public Values(List<Row> rows) {
            this.rows = Objects.requireNonNull(rows);
        }

        public List<Row> getRows() { return rows; }

        @Override
        public Map<String, Object> args() { return Map.of("rows", rows); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitValues(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Values((List<Row>) newArgs.get("rows"));
        }

        @Override
        public String toString() { return "VALUES " + rows; }
    }

    public static final class Row extends Expression {
        private final List<Expression> expressions;

        public Row(List<Expression> expressions) {
            this.expressions = Objects.requireNonNull(expressions);
        }

        public List<Expression> getExpressions() { return expressions; }

        @Override
        public Map<String, Object> args() { return Map.of("expressions", expressions); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitRow(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Row((List<Expression>) newArgs.get("expressions"));
        }

        @Override
        public String toString() { return "(" + expressions + ")"; }
    }

    public static final class Insert extends Expression {
        private final Table table;
        private final List<Expression> columns;
        private final Expression values;

        public Insert(Table table, List<Expression> columns, Expression values) {
            this.table = Objects.requireNonNull(table);
            this.columns = Objects.requireNonNull(columns);
            this.values = Objects.requireNonNull(values);
        }

        public Table getTable() { return table; }
        public List<Expression> getColumns() { return columns; }
        public Expression getValues() { return values; }

        @Override
        public Map<String, Object> args() {
            return Map.of("table", table, "columns", columns, "values", values);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitInsert(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Insert(
                (Table) newArgs.get("table"),
                (List<Expression>) newArgs.get("columns"),
                (Expression) newArgs.get("values")
            );
        }

        @Override
        public String toString() { return "INSERT INTO " + table + columns + " " + values; }
    }

    public static final class Update extends Expression {
        private final Table table;
        private final Map<Expression, Expression> set;
        private final Expression where;

        public Update(Table table, Map<Expression, Expression> set, Expression where) {
            this.table = Objects.requireNonNull(table);
            this.set = Objects.requireNonNull(set);
            this.where = where;
        }

        public Table getTable() { return table; }
        public Map<Expression, Expression> getSet() { return set; }
        public Expression getWhere() { return where; }

        @Override
        public Map<String, Object> args() {
            return Map.of("table", table, "set", set, "where", where);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitUpdate(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Update(
                (Table) newArgs.get("table"),
                (Map<Expression, Expression>) newArgs.get("set"),
                (Expression) newArgs.get("where")
            );
        }

        @Override
        public String toString() { return "UPDATE " + table + " SET " + set; }
    }

    public static final class Delete extends Expression {
        private final Table table;
        private final Expression where;

        public Delete(Table table, Expression where) {
            this.table = Objects.requireNonNull(table);
            this.where = where;
        }

        public Table getTable() { return table; }
        public Expression getWhere() { return where; }

        @Override
        public Map<String, Object> args() {
            return Map.of("table", table, "where", where);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitDelete(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Delete(
                (Table) newArgs.get("table"),
                (Expression) newArgs.get("where")
            );
        }

        @Override
        public String toString() { return "DELETE FROM " + table; }
    }

    public static final class Create extends Expression {
        private final String type;
        private final String name;

        public Create(String type, String name) {
            this.type = Objects.requireNonNull(type);
            this.name = Objects.requireNonNull(name);
        }

        public String getType() { return type; }
        public String getName() { return name; }

        @Override
        public Map<String, Object> args() { return Map.of("type", type, "name", name); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitCreate(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Create(
                (String) newArgs.get("type"),
                (String) newArgs.get("name")
            );
        }

        @Override
        public String toString() { return "CREATE " + type + " " + name; }
    }

    public static final class Drop extends Expression {
        private final String type;
        private final String name;

        public Drop(String type, String name) {
            this.type = Objects.requireNonNull(type);
            this.name = Objects.requireNonNull(name);
        }

        public String getType() { return type; }
        public String getName() { return name; }

        @Override
        public Map<String, Object> args() { return Map.of("type", type, "name", name); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitDrop(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Drop(
                (String) newArgs.get("type"),
                (String) newArgs.get("name")
            );
        }

        @Override
        public String toString() { return "DROP " + type + " " + name; }
    }

    public static final class Alter extends Expression {
        private final String type;
        private final String name;

        public Alter(String type, String name) {
            this.type = Objects.requireNonNull(type);
            this.name = Objects.requireNonNull(name);
        }

        public String getType() { return type; }
        public String getName() { return name; }

        @Override
        public Map<String, Object> args() { return Map.of("type", type, "name", name); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitAlter(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Alter(
                (String) newArgs.get("type"),
                (String) newArgs.get("name")
            );
        }

        @Override
        public String toString() { return "ALTER " + type + " " + name; }
    }

    public static final class With extends Expression {
        private final List<CTE> ctes;
        private final Select select;

        public With(List<CTE> ctes, Select select) {
            this.ctes = Objects.requireNonNull(ctes);
            this.select = Objects.requireNonNull(select);
        }

        public List<CTE> getCtes() { return ctes; }
        public Select getSelect() { return select; }

        @Override
        public Map<String, Object> args() { return Map.of("ctes", ctes, "select", select); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitWith(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new With(
                (List<CTE>) newArgs.get("ctes"),
                (Select) newArgs.get("select")
            );
        }

        @Override
        public String toString() { return "WITH " + ctes + " " + select; }
    }

    public static final class CTE extends Expression {
        private final String name;
        private final Select select;

        public CTE(String name, Select select) {
            this.name = Objects.requireNonNull(name);
            this.select = Objects.requireNonNull(select);
        }

        public String getName() { return name; }
        public Select getSelect() { return select; }

        @Override
        public Map<String, Object> args() { return Map.of("name", name, "select", select); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitCTE(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new CTE(
                (String) newArgs.get("name"),
                (Select) newArgs.get("select")
            );
        }

        @Override
        public String toString() { return name + " AS (" + select + ")"; }
    }

    public static final class Window extends Expression {
        private final Expression expression;
        private final PartitionBy partitionBy;
        private final OrderBy orderBy;

        public Window(Expression expression, PartitionBy partitionBy, OrderBy orderBy) {
            this.expression = Objects.requireNonNull(expression);
            this.partitionBy = partitionBy;
            this.orderBy = orderBy;
        }

        public Expression getExpression() { return expression; }
        public PartitionBy getPartitionBy() { return partitionBy; }
        public OrderBy getOrderBy() { return orderBy; }

        @Override
        public Map<String, Object> args() {
            return Map.of("expression", expression, "partitionBy", partitionBy, "orderBy", orderBy);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitWindow(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Window(
                (Expression) newArgs.get("expression"),
                (PartitionBy) newArgs.get("partitionBy"),
                (OrderBy) newArgs.get("orderBy")
            );
        }

        @Override
        public String toString() { return expression + " OVER (...)"; }
    }

    public static final class PartitionBy extends Expression {
        private final List<Expression> expressions;

        public PartitionBy(List<Expression> expressions) {
            this.expressions = Objects.requireNonNull(expressions);
        }

        public List<Expression> getExpressions() { return expressions; }

        @Override
        public Map<String, Object> args() { return Map.of("expressions", expressions); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitPartitionBy(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new PartitionBy((List<Expression>) newArgs.get("expressions"));
        }

        @Override
        public String toString() { return "PARTITION BY " + expressions; }
    }

    public static final class Bracket extends Expression {
        private final Expression expression;
        private final List<Expression> offset;

        public Bracket(Expression expression, List<Expression> offset) {
            this.expression = Objects.requireNonNull(expression);
            this.offset = Objects.requireNonNull(offset);
        }

        public Expression getExpression() { return expression; }
        public List<Expression> getOffset() { return offset; }

        @Override
        public Map<String, Object> args() { return Map.of("expression", expression, "offset", offset); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitBracket(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Bracket(
                (Expression) newArgs.get("expression"),
                (List<Expression>) newArgs.get("offset")
            );
        }

        @Override
        public String toString() { return expression + "[" + offset + "]"; }
    }

    public static final class Dot extends Expression {
        private final Expression left;
        private final Expression right;

        public Dot(Expression left, Expression right) {
            this.left = Objects.requireNonNull(left);
            this.right = Objects.requireNonNull(right);
        }

        public Expression getLeft() { return left; }
        public Expression getRight() { return right; }

        @Override
        public Map<String, Object> args() { return Map.of("left", left, "right", right); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitDot(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Dot(
                (Expression) newArgs.get("left"),
                (Expression) newArgs.get("right")
            );
        }

        @Override
        public String toString() { return left + "." + right; }
    }

    public static final class Paren extends Expression {
        private final Expression expression;

        public Paren(Expression expression) {
            this.expression = Objects.requireNonNull(expression);
        }

        public Expression getExpression() { return expression; }

        @Override
        public Map<String, Object> args() { return Map.of("expression", expression); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitParen(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Paren((Expression) newArgs.get("expression"));
        }

        @Override
        public String toString() { return "(" + expression + ")"; }
    }

    public static final class Array extends Expression {
        private final List<Expression> expressions;

        public Array(List<Expression> expressions) {
            this.expressions = Objects.requireNonNull(expressions);
        }

        public List<Expression> getExpressions() { return expressions; }

        @Override
        public Map<String, Object> args() { return Map.of("expressions", expressions); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitArray(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Array((List<Expression>) newArgs.get("expressions"));
        }

        @Override
        public String toString() { return "[" + expressions + "]"; }
    }

    public static final class MapLiteral extends Expression {
        private final java.util.Map<Expression, Expression> mapValue;

        public MapLiteral(java.util.Map<Expression, Expression> mapValue) {
            this.mapValue = Objects.requireNonNull(mapValue);
        }

        public java.util.Map<Expression, Expression> getMapValue() { return mapValue; }

        @Override
        public java.util.Map<String, Object> args() { return java.util.Map.of("mapValue", mapValue); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitMap(this); }

        @Override
        protected Expression copyWithArgs(java.util.Map<String, Object> newArgs) {
            return new MapLiteral((java.util.Map<Expression, Expression>) newArgs.get("mapValue"));
        }

        @Override
        public String toString() { return "{" + mapValue + "}"; }
    }

    public static final class Struct extends Expression {
        private final java.util.Map<String, Expression> fields;

        public Struct(java.util.Map<String, Expression> fields) {
            this.fields = Objects.requireNonNull(fields);
        }

        public java.util.Map<String, Expression> getFields() { return fields; }

        @Override
        public Map<String, Object> args() { return java.util.Map.of("fields", fields); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitStruct(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Struct((java.util.Map<String, Expression>) newArgs.get("fields"));
        }

        @Override
        public String toString() { return "STRUCT<" + fields + ">"; }
    }

    public static final class JSON extends Expression {
        private final String json;

        public JSON(String json) {
            this.json = Objects.requireNonNull(json);
        }

        public String getJson() { return json; }

        @Override
        public Map<String, Object> args() { return java.util.Map.of("json", json); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitJSON(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new JSON((String) newArgs.get("json"));
        }

        @Override
        public String toString() { return json; }
    }

    public static final class TypedExpr extends Expression {
        private final Expression expression;
        private final DataTypeExpr type;

        public TypedExpr(Expression expression, DataTypeExpr type) {
            this.expression = Objects.requireNonNull(expression);
            this.type = Objects.requireNonNull(type);
        }

        public Expression getExpression() { return expression; }
        public DataTypeExpr getType() { return type; }

        @Override
        public Map<String, Object> args() { return java.util.Map.of("expression", expression, "type", type); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitTypedExpr(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new TypedExpr(
                (Expression) newArgs.get("expression"),
                (DataTypeExpr) newArgs.get("type")
            );
        }

        @Override
        public String toString() { return expression + "::" + type; }
    }

    public static final class Unnest extends Expression {
        private final Expression expression;

        public Unnest(Expression expression) {
            this.expression = Objects.requireNonNull(expression);
        }

        public Expression getExpression() { return expression; }

        @Override
        public Map<String, Object> args() { return java.util.Map.of("expression", expression); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitUnnest(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Unnest((Expression) newArgs.get("expression"));
        }

        @Override
        public String toString() { return "UNNEST(" + expression + ")"; }
    }

    public static final class Lateral extends Expression {
        private final Expression expression;

        public Lateral(Expression expression) {
            this.expression = Objects.requireNonNull(expression);
        }

        public Expression getExpression() { return expression; }

        @Override
        public Map<String, Object> args() { return java.util.Map.of("expression", expression); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitLateral(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Lateral((Expression) newArgs.get("expression"));
        }

        @Override
        public String toString() { return "LATERAL " + expression; }
    }

    public static final class Unique extends Expression {
        private final Expression expression;

        public Unique(Expression expression) {
            this.expression = Objects.requireNonNull(expression);
        }

        public Expression getExpression() { return expression; }

        @Override
        public Map<String, Object> args() { return java.util.Map.of("expression", expression); }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitUnique(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Unique((Expression) newArgs.get("expression"));
        }

        @Override
        public String toString() { return "UNIQUE(" + expression + ")"; }
    }

    // ============ SET OPERATIONS ============

    public static final class Union extends Expression {
        private final Expression left;
        private final Expression right;
        private final boolean distinct;

        public Union(Expression left, Expression right) {
            this(left, right, true);
        }

        public Union(Expression left, Expression right, boolean distinct) {
            this.left = Objects.requireNonNull(left);
            this.right = Objects.requireNonNull(right);
            this.distinct = distinct;
        }

        public Expression getLeft() { return left; }
        public Expression getRight() { return right; }
        public boolean isDistinct() { return distinct; }

        @Override
        public java.util.Map<String, Object> args() {
            return java.util.Map.of("left", left, "right", right, "distinct", distinct);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitUnion(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Union(
                (Expression) newArgs.get("left"),
                (Expression) newArgs.get("right"),
                (boolean) newArgs.getOrDefault("distinct", true)
            );
        }

        @Override
        public String toString() {
            return left + (distinct ? " UNION " : " UNION ALL ") + right;
        }
    }

    public static final class Intersect extends Expression {
        private final Expression left;
        private final Expression right;

        public Intersect(Expression left, Expression right) {
            this.left = Objects.requireNonNull(left);
            this.right = Objects.requireNonNull(right);
        }

        public Expression getLeft() { return left; }
        public Expression getRight() { return right; }

        @Override
        public java.util.Map<String, Object> args() {
            return java.util.Map.of("left", left, "right", right);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitIntersect(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Intersect(
                (Expression) newArgs.get("left"),
                (Expression) newArgs.get("right")
            );
        }

        @Override
        public String toString() { return left + " INTERSECT " + right; }
    }

    public static final class Except extends Expression {
        private final Expression left;
        private final Expression right;

        public Except(Expression left, Expression right) {
            this.left = Objects.requireNonNull(left);
            this.right = Objects.requireNonNull(right);
        }

        public Expression getLeft() { return left; }
        public Expression getRight() { return right; }

        @Override
        public java.util.Map<String, Object> args() {
            return java.util.Map.of("left", left, "right", right);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitExcept(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Except(
                (Expression) newArgs.get("left"),
                (Expression) newArgs.get("right")
            );
        }

        @Override
        public String toString() { return left + " EXCEPT " + right; }
    }

    // ============ TABLE CONSTRAINTS ============

    public static final class PrimaryKey extends Expression {
        private final List<Expression> columns;

        public PrimaryKey(List<Expression> columns) {
            this.columns = Objects.requireNonNull(columns);
        }

        public List<Expression> getColumns() { return columns; }

        @Override
        public java.util.Map<String, Object> args() {
            return java.util.Map.of("columns", columns);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitPrimaryKey(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new PrimaryKey((List<Expression>) newArgs.get("columns"));
        }

        @Override
        public String toString() {
            return "PRIMARY KEY (" + columns + ")";
        }
    }

    public static final class ForeignKey extends Expression {
        private final List<Expression> columns;
        private final String refTable;
        private final List<String> refColumns;

        public ForeignKey(List<Expression> columns, String refTable, List<String> refColumns) {
            this.columns = Objects.requireNonNull(columns);
            this.refTable = Objects.requireNonNull(refTable);
            this.refColumns = Objects.requireNonNull(refColumns);
        }

        public List<Expression> getColumns() { return columns; }
        public String getRefTable() { return refTable; }
        public List<String> getRefColumns() { return refColumns; }

        @Override
        public java.util.Map<String, Object> args() {
            return java.util.Map.of("columns", columns, "refTable", refTable, "refColumns", refColumns);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitForeignKey(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new ForeignKey(
                (List<Expression>) newArgs.get("columns"),
                (String) newArgs.get("refTable"),
                (List<String>) newArgs.get("refColumns")
            );
        }

        @Override
        public String toString() {
            return "FOREIGN KEY (" + columns + ") REFERENCES " + refTable;
        }
    }

    // ============ UTILITY STATEMENTS ============

    public static final class Explain extends Expression {
        private final Expression expression;

        public Explain(Expression expression) {
            this.expression = Objects.requireNonNull(expression);
        }

        public Expression getExpression() { return expression; }

        @Override
        public java.util.Map<String, Object> args() {
            return java.util.Map.of("expression", expression);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitExplain(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Explain((Expression) newArgs.get("expression"));
        }

        @Override
        public String toString() { return "EXPLAIN " + expression; }
    }

    public static final class Comment extends Expression {
        private final String text;

        public Comment(String text) {
            this.text = Objects.requireNonNull(text);
        }

        public String getText() { return text; }

        @Override
        public java.util.Map<String, Object> args() {
            return java.util.Map.of("text", text);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitComment(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Comment((String) newArgs.get("text"));
        }

        @Override
        public String toString() { return "-- " + text; }
    }

    public static final class Hint extends Expression {
        private final String hint;

        public Hint(String hint) {
            this.hint = Objects.requireNonNull(hint);
        }

        public String getHint() { return hint; }

        @Override
        public java.util.Map<String, Object> args() {
            return java.util.Map.of("hint", hint);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitHint(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new Hint((String) newArgs.get("hint"));
        }

        @Override
        public String toString() { return "/*+ " + hint + " */"; }
    }

    // ============ COLUMN DEFINITION ============

    public static final class ColumnDef extends Expression {
        private final String name;
        private final DataTypeExpr type;
        private final boolean nullable;
        private final Expression defaultValue;

        public ColumnDef(String name, DataTypeExpr type) {
            this(name, type, true, null);
        }

        public ColumnDef(String name, DataTypeExpr type, boolean nullable, Expression defaultValue) {
            this.name = Objects.requireNonNull(name);
            this.type = Objects.requireNonNull(type);
            this.nullable = nullable;
            this.defaultValue = defaultValue;
        }

        public String getName() { return name; }
        public DataTypeExpr getType() { return type; }
        public boolean isNullable() { return nullable; }
        public Expression getDefaultValue() { return defaultValue; }

        @Override
        public java.util.Map<String, Object> args() {
            java.util.Map<String, Object> map = new LinkedHashMap<>();
            map.put("name", name);
            map.put("type", type);
            map.put("nullable", nullable);
            map.put("defaultValue", defaultValue);
            return map;
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitColumnDef(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new ColumnDef(
                (String) newArgs.get("name"),
                (DataTypeExpr) newArgs.get("type"),
                (boolean) newArgs.getOrDefault("nullable", true),
                (Expression) newArgs.get("defaultValue")
            );
        }

        @Override
        public String toString() {
            return name + " " + type;
        }
    }

    // ============ Apache DRILL SPECIFIC ============

    public static final class WorkspacePath extends Expression {
        private final List<String> path;

        public WorkspacePath(List<String> path) {
            this.path = Objects.requireNonNull(path);
        }

        public List<String> getPath() { return path; }

        @Override
        public java.util.Map<String, Object> args() {
            return java.util.Map.of("path", path);
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) { return visitor.visitWorkspacePath(this); }

        @Override
        protected Expression copyWithArgs(Map<String, Object> newArgs) {
            return new WorkspacePath((List<String>) newArgs.get("path"));
        }

        @Override
        public String toString() {
            return "`" + String.join("`.`", path) + "`";
        }
    }
}
