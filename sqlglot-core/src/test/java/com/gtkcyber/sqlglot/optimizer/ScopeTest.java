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
package com.gtkcyber.sqlglot.optimizer;

import com.gtkcyber.sqlglot.expressions.Expression;
import com.gtkcyber.sqlglot.expressions.Nodes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScopeTest {

    @Test
    void testScopeCreation() {
        Expression expr = new Nodes.Select(java.util.List.of());
        Scope scope = new Scope(ScopeType.ROOT, expr, null);

        assertEquals(ScopeType.ROOT, scope.getType());
        assertEquals(expr, scope.getExpression());
        assertNull(scope.getParent());
        assertTrue(scope.getChildren().isEmpty());
    }

    @Test
    void testAddSource() {
        Expression expr = new Nodes.Select(java.util.List.of());
        Scope scope = new Scope(ScopeType.ROOT, expr, null);

        Source source = new Source.TableSource("users", "public.users");
        scope.addSource("users", source);

        assertTrue(scope.getSource("users").isPresent());
        assertEquals(source, scope.getSource("users").get());
    }

    @Test
    void testGetSource() {
        Expression expr = new Nodes.Select(java.util.List.of());
        Scope scope = new Scope(ScopeType.ROOT, expr, null);

        Source source = new Source.TableSource("orders", "public.orders");
        scope.addSource("orders", source);

        assertTrue(scope.getSource("orders").isPresent());
        assertFalse(scope.getSource("nonexistent").isPresent());
    }

    @Test
    void testAddMultipleSources() {
        Expression expr = new Nodes.Select(java.util.List.of());
        Scope scope = new Scope(ScopeType.ROOT, expr, null);

        scope.addSource("t1", new Source.TableSource("t1", "table1"));
        scope.addSource("t2", new Source.TableSource("t2", "table2"));

        assertEquals(2, scope.getSources().size());
    }

    @Test
    void testCTERefCount() {
        Expression expr = new Nodes.Select(java.util.List.of());
        Scope scope = new Scope(ScopeType.ROOT, expr, null);

        assertEquals(0, scope.getCTERefCount("unused_cte"));

        scope.setCTERefCount("used_cte", 2);
        assertEquals(2, scope.getCTERefCount("used_cte"));

        scope.incrementCTERefCount("used_cte");
        assertEquals(3, scope.getCTERefCount("used_cte"));
    }

    @Test
    void testReferencedColumns() {
        Expression expr = new Nodes.Select(java.util.List.of());
        Scope scope = new Scope(ScopeType.ROOT, expr, null);

        scope.addReferencedColumn("id");
        scope.addReferencedColumn("name");

        assertTrue(scope.getReferencedColumns().contains("id"));
        assertTrue(scope.getReferencedColumns().contains("name"));
        assertEquals(2, scope.getReferencedColumns().size());
    }

    @Test
    void testAddChild() {
        Expression parentExpr = new Nodes.Select(java.util.List.of());
        Expression childExpr = new Nodes.Select(java.util.List.of());

        Scope parentScope = new Scope(ScopeType.ROOT, parentExpr, null);
        Scope childScope = new Scope(ScopeType.SUBQUERY, childExpr, parentScope);

        parentScope.addChild(childScope);

        assertEquals(1, parentScope.getChildren().size());
        assertEquals(childScope, parentScope.getChildren().get(0));
        assertEquals(parentScope, childScope.getParent());
    }

    @Test
    void testFindScope() {
        Expression expr = new Nodes.Select(java.util.List.of());
        Scope root = new Scope(ScopeType.ROOT, expr, null);

        Expression subqueryExpr = new Nodes.Select(java.util.List.of());
        Scope subquery = new Scope(ScopeType.SUBQUERY, subqueryExpr, root);
        root.addChild(subquery);

        assertTrue(root.findScope(ScopeType.ROOT).isPresent());
        assertTrue(root.findScope(ScopeType.SUBQUERY).isPresent());
        assertFalse(root.findScope(ScopeType.CTE).isPresent());
    }

    @Test
    void testTableSourceInterface() {
        Source.TableSource source = new Source.TableSource("t1", "schema.table1");
        assertEquals("t1", source.getName());
        assertEquals("schema.table1", source.getQualifiedName());
    }

    @Test
    void testScopeSourceInterface() {
        Expression expr = new Nodes.Select(java.util.List.of());
        Scope innerScope = new Scope(ScopeType.CTE, expr, null);
        Source.ScopeSource source = new Source.ScopeSource("cte1", innerScope);

        assertEquals("cte1", source.getName());
        assertEquals(innerScope, source.getScope());
    }

    @Test
    void testGetAllSources() {
        Expression expr = new Nodes.Select(java.util.List.of());
        Scope scope = new Scope(ScopeType.ROOT, expr, null);

        scope.addSource("t1", new Source.TableSource("t1", "table1"));
        scope.addSource("t2", new Source.TableSource("t2", "table2"));
        scope.addSource("t3", new Source.TableSource("t3", "table3"));

        assertEquals(3, scope.getSources().size());
    }

    @Test
    void testGetCTERefCounts() {
        Expression expr = new Nodes.Select(java.util.List.of());
        Scope scope = new Scope(ScopeType.ROOT, expr, null);

        scope.setCTERefCount("cte1", 2);
        scope.setCTERefCount("cte2", 0);

        var refCounts = scope.getCTERefCounts();
        assertEquals(2, refCounts.get("cte1"));
        assertEquals(0, refCounts.get("cte2"));
    }
}
