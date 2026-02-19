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
