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
package io.sqlglot.expressions;

/**
 * Visitor interface for SQL expression AST traversal.
 * One method per node type for compile-time exhaustiveness checking.
 * Type parameter R is the return type for visited nodes.
 */
public interface ExpressionVisitor<R> {
    // Literals
    R visitLiteral(Nodes.Literal node);
    R visitNull(Nodes.Null node);
    R visitTrue(Nodes.True node);
    R visitFalse(Nodes.False node);

    // Identifiers and references
    R visitIdentifier(Nodes.Identifier node);
    R visitColumn(Nodes.Column node);
    R visitTable(Nodes.Table node);
    R visitAlias(Nodes.Alias node);
    R visitStar(Nodes.Star node);
    R visitParameter(Nodes.Parameter node);
    R visitPlaceholder(Nodes.Placeholder node);

    // Selection and filtering
    R visitSelect(Nodes.Select node);
    R visitDistinct(Nodes.Distinct node);
    R visitFrom(Nodes.From node);
    R visitJoin(Nodes.Join node);
    R visitWhere(Nodes.Where node);
    R visitGroupBy(Nodes.GroupBy node);
    R visitHaving(Nodes.Having node);
    R visitOrderBy(Nodes.OrderBy node);
    R visitOrder(Nodes.Order node);
    R visitLimit(Nodes.Limit node);
    R visitOffset(Nodes.Offset node);

    // Binary operators (arithmetic)
    R visitAdd(Nodes.Add node);
    R visitSub(Nodes.Sub node);
    R visitMul(Nodes.Mul node);
    R visitDiv(Nodes.Div node);
    R visitMod(Nodes.Mod node);

    // Binary operators (logical/comparison)
    R visitAnd(Nodes.And node);
    R visitOr(Nodes.Or node);
    R visitNot(Nodes.Not node);
    R visitNot2(Nodes.Not2 node);
    R visitEQ(Nodes.EQ node);
    R visitNEQ(Nodes.NEQ node);
    R visitLT(Nodes.LT node);
    R visitGT(Nodes.GT node);
    R visitLTE(Nodes.LTE node);
    R visitGTE(Nodes.GTE node);

    // Comparison predicates
    R visitIs(Nodes.Is node);
    R visitIsNull(Nodes.IsNull node);
    R visitIsNotNull(Nodes.IsNotNull node);
    R visitLike(Nodes.Like node);
    R visitIn(Nodes.In node);
    R visitBetween(Nodes.Between node);
    R visitExists(Nodes.Exists node);

    // Control flow
    R visitCase(Nodes.Case node);
    R visitWhen(Nodes.When node);

    // Functions and casts
    R visitCast(Nodes.Cast node);
    R visitFunction(Nodes.Function node);
    R visitDataType(Nodes.DataTypeExpr node);

    // Subqueries and set operations
    R visitSubquery(Nodes.Subquery node);
    R visitValues(Nodes.Values node);
    R visitRow(Nodes.Row node);
    R visitUnnest(Nodes.Unnest node);
    R visitLateral(Nodes.Lateral node);

    // DML
    R visitInsert(Nodes.Insert node);
    R visitUpdate(Nodes.Update node);
    R visitDelete(Nodes.Delete node);

    // DDL
    R visitCreate(Nodes.Create node);
    R visitDrop(Nodes.Drop node);
    R visitAlter(Nodes.Alter node);

    // CTEs and window functions
    R visitWith(Nodes.With node);
    R visitCTE(Nodes.CTE node);
    R visitWindow(Nodes.Window node);
    R visitPartitionBy(Nodes.PartitionBy node);

    // Complex types and operators
    R visitBracket(Nodes.Bracket node);
    R visitDot(Nodes.Dot node);
    R visitParen(Nodes.Paren node);
    R visitArray(Nodes.Array node);
    R visitMap(Nodes.MapLiteral node);
    R visitStruct(Nodes.Struct node);
    R visitJSON(Nodes.JSON node);
    R visitTypedExpr(Nodes.TypedExpr node);
    R visitUnique(Nodes.Unique node);

    // Set operations
    R visitUnion(Nodes.Union node);
    R visitIntersect(Nodes.Intersect node);
    R visitExcept(Nodes.Except node);

    // Table constraints
    R visitPrimaryKey(Nodes.PrimaryKey node);
    R visitForeignKey(Nodes.ForeignKey node);

    // Utility statements
    R visitExplain(Nodes.Explain node);
    R visitComment(Nodes.Comment node);
    R visitHint(Nodes.Hint node);

    // Column definition
    R visitColumnDef(Nodes.ColumnDef node);

    // Apache Drill specific
    R visitWorkspacePath(Nodes.WorkspacePath node);

    // Catch-all for unknown expression types
    default R visitUnknown(Expression expr) {
        throw new UnsupportedOperationException("Unknown expression type: " + expr.getClass().getSimpleName());
    }
}
