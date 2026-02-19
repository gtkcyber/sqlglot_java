package io.sqlglot.generator;

/**
 * Configuration for SQL code generation.
 */
public record GeneratorConfig(
    boolean pretty,        // Format with newlines and indentation
    boolean identify,      // Identify all identifiers (add quotes)
    boolean normalize,     // Normalize keywords to uppercase
    int indentLevel        // Current indentation level
) {
    /**
     * Creates a default config with pretty printing and normalized keywords.
     */
    public static GeneratorConfig defaultConfig() {
        return new GeneratorConfig(true, false, true, 0);
    }

    /**
     * Creates a compact config without pretty printing.
     */
    public static GeneratorConfig compact() {
        return new GeneratorConfig(false, false, true, 0);
    }

    /**
     * Creates a config with increased indentation.
     */
    public GeneratorConfig indent() {
        return new GeneratorConfig(pretty, identify, normalize, indentLevel + 1);
    }

    /**
     * Creates a config with decreased indentation.
     */
    public GeneratorConfig dedent() {
        return new GeneratorConfig(pretty, identify, normalize, Math.max(0, indentLevel - 1));
    }

    /**
     * Returns the indentation string.
     */
    public String getIndent() {
        return "  ".repeat(indentLevel);
    }

    /**
     * Returns a newline if pretty printing is enabled, empty string otherwise.
     */
    public String newline() {
        return pretty ? "\n" : " ";
    }
}
