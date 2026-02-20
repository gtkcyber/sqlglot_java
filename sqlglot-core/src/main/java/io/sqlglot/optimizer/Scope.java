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
package io.sqlglot.optimizer;

import io.sqlglot.expressions.Expression;
import io.sqlglot.expressions.Nodes;

import java.util.*;

/**
 * Represents a query scope for name resolution and reference tracking.
 * Tracks sources (tables/subqueries), columns, parent/child relationships, and reference counts.
 */
public class Scope {
    private final ScopeType type;
    private final Expression expression;
    private final Scope parent;
    private final List<Scope> children;
    private final Map<String, Source> sources;  // alias -> Source
    private final Map<String, Integer> cteReferenceCount;  // CTE name -> count
    private final Set<String> referencedColumnNames;

    /**
     * Creates a new scope.
     *
     * @param type the type of this scope
     * @param expression the expression (SELECT, WITH, etc.) that defines this scope
     * @param parent the parent scope, or null if this is a root scope
     */
    public Scope(ScopeType type, Expression expression, Scope parent) {
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.expression = Objects.requireNonNull(expression, "expression cannot be null");
        this.parent = parent;
        this.children = new ArrayList<>();
        this.sources = new LinkedHashMap<>();
        this.cteReferenceCount = new HashMap<>();
        this.referencedColumnNames = new HashSet<>();
    }

    /**
     * Gets the type of this scope
     */
    public ScopeType getType() {
        return type;
    }

    /**
     * Gets the expression that defines this scope
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Gets the parent scope, or null if this is a root scope
     */
    public Scope getParent() {
        return parent;
    }

    /**
     * Gets all child scopes
     */
    public List<Scope> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Gets all sources (tables/subqueries) in this scope
     */
    public Collection<Source> getSources() {
        return Collections.unmodifiableCollection(sources.values());
    }

    /**
     * Gets a source by name/alias
     */
    public Optional<Source> getSource(String name) {
        return Optional.ofNullable(sources.get(name));
    }

    /**
     * Adds a source to this scope
     */
    public void addSource(String name, Source source) {
        sources.put(name, source);
    }

    /**
     * Gets the reference count for a CTE
     */
    public int getCTERefCount(String cteName) {
        return cteReferenceCount.getOrDefault(cteName, 0);
    }

    /**
     * Increments the reference count for a CTE
     */
    public void incrementCTERefCount(String cteName) {
        cteReferenceCount.put(cteName, getCTERefCount(cteName) + 1);
    }

    /**
     * Sets the reference count for a CTE
     */
    public void setCTERefCount(String cteName, int count) {
        cteReferenceCount.put(cteName, count);
    }

    /**
     * Gets all CTE reference counts
     */
    public Map<String, Integer> getCTERefCounts() {
        return Collections.unmodifiableMap(cteReferenceCount);
    }

    /**
     * Records that a column is referenced in this scope
     */
    public void addReferencedColumn(String columnName) {
        referencedColumnNames.add(columnName);
    }

    /**
     * Gets all referenced column names in this scope
     */
    public Set<String> getReferencedColumns() {
        return Collections.unmodifiableSet(referencedColumnNames);
    }

    /**
     * Adds a child scope
     */
    protected void addChild(Scope child) {
        this.children.add(child);
    }

    /**
     * Finds a scope by type in the scope tree
     */
    public Optional<Scope> findScope(ScopeType type) {
        if (this.type == type) {
            return Optional.of(this);
        }
        for (Scope child : children) {
            Optional<Scope> found = child.findScope(type);
            if (found.isPresent()) {
                return found;
            }
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "Scope{" +
                "type=" + type +
                ", sources=" + sources.keySet() +
                ", children=" + children.size() +
                '}';
    }
}
