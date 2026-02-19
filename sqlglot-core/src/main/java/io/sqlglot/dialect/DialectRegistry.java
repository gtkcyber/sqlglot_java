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
package io.sqlglot.dialect;

import java.util.*;

/**
 * Registry of SQL dialects.
 * Uses ServiceLoader for plugin-based discovery and manual registration.
 */
public class DialectRegistry {
    private static final DialectRegistry INSTANCE = new DialectRegistry();
    private final Map<String, Dialect> dialects = new LinkedHashMap<>();

    private DialectRegistry() {
        // Register built-in dialects
        registerDialect(new AnsiDialect());

        // Load dialects via ServiceLoader
        ServiceLoader.load(Dialect.class).forEach(this::registerDialect);
    }

    /**
     * Gets the singleton instance.
     */
    public static DialectRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Registers a dialect.
     */
    public void registerDialect(Dialect dialect) {
        dialects.put(dialect.getName().toLowerCase(), dialect);
    }

    /**
     * Gets a dialect by name (case-insensitive).
     */
    public Dialect get(String name) {
        Dialect dialect = dialects.get(name.toLowerCase());
        if (dialect == null) {
            throw new IllegalArgumentException("Unknown dialect: " + name);
        }
        return dialect;
    }

    /**
     * Checks if a dialect is registered.
     */
    public boolean contains(String name) {
        return dialects.containsKey(name.toLowerCase());
    }

    /**
     * Gets all registered dialect names.
     */
    public Collection<String> getDialectNames() {
        return new ArrayList<>(dialects.keySet());
    }

    /**
     * Gets all registered dialects.
     */
    public Collection<Dialect> getDialects() {
        return new ArrayList<>(dialects.values());
    }
}
