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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.function.UnaryOperator;

/**
 * Abstract base class for all SQL expression AST nodes.
 * Provides generic traversal (walk, find, transform) and visitor pattern support.
 */
public abstract class Expression {

    /**
     * Returns a map of field names to their current values.
     * Used for generic tree traversal and manipulation.
     */
    public abstract Map<String, Object> args();

    /**
     * Accepts a visitor and returns a value of type R.
     */
    public abstract <R> R accept(ExpressionVisitor<R> visitor);

    /**
     * Returns a breadth-first stream of all expressions in this subtree (including self).
     */
    public Stream<Expression> walk() {
        return walk(WalkMode.BFS);
    }

    /**
     * Returns a stream of all expressions in this subtree according to the specified mode.
     */
    public Stream<Expression> walk(WalkMode mode) {
        return mode.walk(this);
    }

    /**
     * Finds the first expression of the given type in this subtree.
     */
    public <T extends Expression> Optional<T> find(Class<T> type) {
        return walk()
            .filter(type::isInstance)
            .map(type::cast)
            .findFirst();
    }

    /**
     * Finds all expressions of the given type in this subtree.
     */
    public <T extends Expression> Stream<T> findAll(Class<T> type) {
        return walk()
            .filter(type::isInstance)
            .map(type::cast);
    }

    /**
     * Transforms this expression tree by applying a function to each node.
     * Returns a new tree with the transformations applied (post-order traversal).
     */
    public Expression transform(UnaryOperator<Expression> fn) {
        Map<String, Object> currentArgs = args();
        Map<String, Object> newArgs = new LinkedHashMap<>();

        // Transform children first (post-order)
        for (Map.Entry<String, Object> entry : currentArgs.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Expression expr) {
                newArgs.put(key, expr.transform(fn));
            } else if (value instanceof List<?> list) {
                Object transformedList = list.stream()
                    .map(item -> {
                        if (item instanceof Expression expr) {
                            return expr.transform(fn);
                        }
                        return item;
                    })
                    .toList();
                newArgs.put(key, transformedList);
            } else {
                newArgs.put(key, value);
            }
        }

        // Create a copy of this expression with transformed children
        Expression transformed = copyWithArgs(newArgs);

        // Apply the function to the transformed node
        return fn.apply(transformed);
    }

    /**
     * Creates a copy of this expression with the given args.
     * Must be implemented by each concrete class.
     */
    protected abstract Expression copyWithArgs(Map<String, Object> newArgs);

    /**
     * Returns a string representation of this expression.
     */
    @Override
    public abstract String toString();

    /**
     * Traversal mode for walk() operations.
     */
    public enum WalkMode {
        BFS {
            @Override
            public Stream<Expression> walk(Expression root) {
                return new BfsIterator(root).stream();
            }
        },
        DFS {
            @Override
            public Stream<Expression> walk(Expression root) {
                return new DfsIterator(root).stream();
            }
        };

        public abstract Stream<Expression> walk(Expression root);
    }

    /**
     * BFS tree iterator (level-order traversal).
     */
    private static class BfsIterator {
        private final java.util.Queue<Expression> queue = new java.util.LinkedList<>();

        BfsIterator(Expression root) {
            queue.offer(root);
        }

        Stream<Expression> stream() {
            return Stream.generate(() -> {
                if (queue.isEmpty()) return null;
                Expression expr = queue.poll();
                Map<String, Object> args = expr.args();
                for (Object value : args.values()) {
                    if (value instanceof Expression child) {
                        queue.offer(child);
                    } else if (value instanceof List<?> list) {
                        list.stream()
                            .filter(Expression.class::isInstance)
                            .map(Expression.class::cast)
                            .forEach(queue::offer);
                    }
                }
                return expr;
            }).takeWhile(e -> e != null);
        }
    }

    /**
     * DFS tree iterator (pre-order traversal).
     */
    private static class DfsIterator {
        private final java.util.Deque<Expression> stack = new java.util.ArrayDeque<>();

        DfsIterator(Expression root) {
            stack.push(root);
        }

        Stream<Expression> stream() {
            return Stream.generate(() -> {
                if (stack.isEmpty()) return null;
                Expression expr = stack.pop();
                Map<String, Object> args = expr.args();
                // Push in reverse order for pre-order traversal
                args.values().stream().collect(java.util.stream.Collectors.toList()).stream()
                    .sorted((a, b) -> -1)  // reverse order
                    .forEach(value -> {
                        if (value instanceof Expression child) {
                            stack.push(child);
                        } else if (value instanceof List<?> list) {
                            list.stream()
                                .filter(Expression.class::isInstance)
                                .map(Expression.class::cast)
                                .collect(java.util.stream.Collectors.toCollection(() -> new java.util.LinkedList<>()))
                                .forEach(stack::push);
                        }
                    });
                return expr;
            }).takeWhile(e -> e != null);
        }
    }
}
