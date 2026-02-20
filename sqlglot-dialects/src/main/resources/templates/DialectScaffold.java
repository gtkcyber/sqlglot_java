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
package com.gtkcyber.sqlglot.dialects.template;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to generate new SQL dialect implementations from templates.
 *
 * This scaffolding tool automates the creation of dialect implementations by:
 * 1. Reading template files
 * 2. Replacing placeholder variables with dialect-specific names
 * 3. Creating new dialect files with proper structure
 *
 * USAGE:
 * <pre>
 * {@code
 * DialectScaffold scaffold = new DialectScaffold();
 * DialectConfig config = new DialectScaffold.DialectConfig("SQLite", "sqlite");
 * scaffold.generateDialect(config, Paths.get("sqlglot-dialects/src/main/java/com/gtkcyber/sqlglot/dialects/sqlite"));
 * }
 * </pre>
 *
 * PLACEHOLDERS:
 * - {DIALECT_NAME}: Display name (e.g., "SQLite")
 * - {DIALECT_NAME_UPPER}: Uppercase name (e.g., "SQLITE")
 * - {DIALECT_NAME_LOWER}: Lowercase name (e.g., "sqlite")
 * - {PACKAGE_PATH}: Package path (e.g., "sqlite")
 * - {DIALECT_CLASS_NAME}: Dialect class (e.g., "SqliteDialect")
 * - {PARSER_CLASS_NAME}: Parser class (e.g., "SqliteParser")
 * - {GENERATOR_CLASS_NAME}: Generator class (e.g., "SqliteGenerator")
 * - {TOKENIZER_CLASS_NAME}: Tokenizer class (e.g., "SqliteTokenizer")
 */
public class DialectScaffold {
    private static final String TEMPLATES_PACKAGE = "com.gtkcyber.sqlglot.dialects.template";

    /**
     * Configuration for a new dialect implementation.
     */
    public static class DialectConfig {
        private final String dialectName;              // e.g., "SQLite"
        private final String dialectNameLower;         // e.g., "sqlite"
        private final String packagePath;              // e.g., "sqlite"
        private final String dialectClassName;         // e.g., "SqliteDialect"
        private final String parserClassName;          // e.g., "SqliteParser"
        private final String generatorClassName;       // e.g., "SqliteGenerator"
        private final String tokenizerClassName;       // e.g., "SqliteTokenizer"

        /**
         * Creates a dialect configuration with standard naming conventions.
         *
         * This constructor automatically generates class names based on the dialect name:
         * - "SQLite" -> "SqliteDialect", "SqliteParser", "SqliteGenerator", "SqliteTokenizer"
         * - "PostgreSQL" -> "PostgreSQLDialect", "PostgreSQLParser", etc.
         *
         * @param dialectName The display name of the dialect (e.g., "SQLite")
         * @param packagePath The package path for the dialect (e.g., "sqlite")
         */
        public DialectConfig(String dialectName, String packagePath) {
            this(
                dialectName,
                packagePath,
                dialectName + "Dialect",
                dialectName + "Parser",
                dialectName + "Generator",
                dialectName + "Tokenizer"
            );
        }

        /**
         * Creates a dialect configuration with explicit class names.
         *
         * @param dialectName The display name of the dialect
         * @param packagePath The package path for the dialect
         * @param dialectClassName The Dialect class name
         * @param parserClassName The Parser class name
         * @param generatorClassName The Generator class name
         * @param tokenizerClassName The Tokenizer class name
         */
        public DialectConfig(
                String dialectName,
                String packagePath,
                String dialectClassName,
                String parserClassName,
                String generatorClassName,
                String tokenizerClassName) {
            this.dialectName = dialectName;
            this.dialectNameLower = dialectName.toLowerCase();
            this.packagePath = packagePath.toLowerCase();
            this.dialectClassName = dialectClassName;
            this.parserClassName = parserClassName;
            this.generatorClassName = generatorClassName;
            this.tokenizerClassName = tokenizerClassName;
        }

        public String getDialectName() {
            return dialectName;
        }

        public String getDialectNameLower() {
            return dialectNameLower;
        }

        public String getDialectNameUpper() {
            return dialectName.toUpperCase();
        }

        public String getPackagePath() {
            return packagePath;
        }

        public String getDialectClassName() {
            return dialectClassName;
        }

        public String getParserClassName() {
            return parserClassName;
        }

        public String getGeneratorClassName() {
            return generatorClassName;
        }

        public String getTokenizerClassName() {
            return tokenizerClassName;
        }
    }

    /**
     * Generates a complete dialect implementation from templates.
     *
     * This method:
     * 1. Creates the target directory
     * 2. Generates all four template files
     * 3. Replaces placeholder variables
     * 4. Writes files to disk
     *
     * @param config The dialect configuration
     * @param targetDir The target directory for the new dialect
     * @throws IOException If file I/O fails
     */
    public void generateDialect(DialectConfig config, Path targetDir) throws IOException {
        // Create target directory
        Files.createDirectories(targetDir);

        // Build replacement map
        Map<String, String> replacements = buildReplacements(config);

        // Generate each file
        generateDialectFile(config, replacements, targetDir);
        generateParserFile(config, replacements, targetDir);
        generateGeneratorFile(config, replacements, targetDir);
        generateTokenizerFile(config, replacements, targetDir);
    }

    /**
     * Generates the Dialect class file.
     *
     * @param config The dialect configuration
     * @param replacements Map of placeholder to replacement value
     * @param targetDir The target directory
     * @throws IOException If file I/O fails
     */
    private void generateDialectFile(DialectConfig config, Map<String, String> replacements, Path targetDir)
            throws IOException {
        String template = readTemplate("BaseDialectTemplate");
        String content = replaceAll(template, replacements);
        Path filePath = targetDir.resolve(config.getDialectClassName() + ".java");
        Files.write(filePath, content.getBytes());
        System.out.println("Generated: " + filePath);
    }

    /**
     * Generates the Parser class file.
     *
     * @param config The dialect configuration
     * @param replacements Map of placeholder to replacement value
     * @param targetDir The target directory
     * @throws IOException If file I/O fails
     */
    private void generateParserFile(DialectConfig config, Map<String, String> replacements, Path targetDir)
            throws IOException {
        String template = readTemplate("BaseParserTemplate");
        String content = replaceAll(template, replacements);
        Path filePath = targetDir.resolve(config.getParserClassName() + ".java");
        Files.write(filePath, content.getBytes());
        System.out.println("Generated: " + filePath);
    }

    /**
     * Generates the Generator class file.
     *
     * @param config The dialect configuration
     * @param replacements Map of placeholder to replacement value
     * @param targetDir The target directory
     * @throws IOException If file I/O fails
     */
    private void generateGeneratorFile(DialectConfig config, Map<String, String> replacements, Path targetDir)
            throws IOException {
        String template = readTemplate("BaseGeneratorTemplate");
        String content = replaceAll(template, replacements);
        Path filePath = targetDir.resolve(config.getGeneratorClassName() + ".java");
        Files.write(filePath, content.getBytes());
        System.out.println("Generated: " + filePath);
    }

    /**
     * Generates the Tokenizer class file.
     *
     * @param config The dialect configuration
     * @param replacements Map of placeholder to replacement value
     * @param targetDir The target directory
     * @throws IOException If file I/O fails
     */
    private void generateTokenizerFile(DialectConfig config, Map<String, String> replacements, Path targetDir)
            throws IOException {
        String template = readTemplate("BaseTokenizerTemplate");
        String content = replaceAll(template, replacements);
        Path filePath = targetDir.resolve(config.getTokenizerClassName() + ".java");
        Files.write(filePath, content.getBytes());
        System.out.println("Generated: " + filePath);
    }

    /**
     * Builds the replacement map for all placeholders.
     *
     * @param config The dialect configuration
     * @return Map of placeholder to replacement value
     */
    private Map<String, String> buildReplacements(DialectConfig config) {
        Map<String, String> replacements = new HashMap<>();
        replacements.put("{DIALECT_NAME}", config.getDialectName());
        replacements.put("{DIALECT_NAME_UPPER}", config.getDialectNameUpper());
        replacements.put("{DIALECT_NAME_LOWER}", config.getDialectNameLower());
        replacements.put("{PACKAGE_PATH}", config.getPackagePath());
        replacements.put("{DIALECT_CLASS_NAME}", config.getDialectClassName());
        replacements.put("{PARSER_CLASS_NAME}", config.getParserClassName());
        replacements.put("{GENERATOR_CLASS_NAME}", config.getGeneratorClassName());
        replacements.put("{TOKENIZER_CLASS_NAME}", config.getTokenizerClassName());
        return replacements;
    }

    /**
     * Reads a template file from the classpath.
     *
     * @param templateName The template name without extension (e.g., "BaseDialectTemplate")
     * @return The template content as a String
     * @throws IOException If file I/O fails
     */
    private String readTemplate(String templateName) throws IOException {
        // Get the template file from the same package
        String templatePath = "/" + TEMPLATES_PACKAGE.replace('.', '/') + "/" + templateName + ".java";
        try {
            byte[] content = this.getClass().getResourceAsStream(templatePath).readAllBytes();
            return new String(content);
        } catch (Exception e) {
            throw new IOException("Failed to read template: " + templateName, e);
        }
    }

    /**
     * Replaces all placeholders in the template with their corresponding values.
     *
     * @param template The template string
     * @param replacements Map of placeholder to replacement value
     * @return The template with placeholders replaced
     */
    private String replaceAll(String template, Map<String, String> replacements) {
        String result = template;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Main method for command-line usage.
     *
     * Usage: java DialectScaffold <dialectName> <packagePath> [outputDir]
     * Example: java DialectScaffold SQLite sqlite /path/to/output
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: DialectScaffold <dialectName> <packagePath> [outputDir]");
            System.out.println("Example: DialectScaffold SQLite sqlite .");
            System.exit(1);
        }

        String dialectName = args[0];
        String packagePath = args[1];
        Path outputDir = args.length > 2 ? Paths.get(args[2]) : Paths.get(".");

        DialectConfig config = new DialectConfig(dialectName, packagePath);
        Path targetDir = outputDir.resolve(packagePath);

        DialectScaffold scaffold = new DialectScaffold();
        scaffold.generateDialect(config, targetDir);

        System.out.println("\nDialect scaffold generated successfully!");
        System.out.println("Location: " + targetDir);
        System.out.println("\nNext steps:");
        System.out.println("1. Review generated files in: " + targetDir);
        System.out.println("2. Update dialect-specific keywords in " + config.getTokenizerClassName() + ".java");
        System.out.println("3. Implement dialect-specific parsing in " + config.getParserClassName() + ".java");
        System.out.println("4. Implement dialect-specific generation in " + config.getGeneratorClassName() + ".java");
        System.out.println("5. Create test cases in: sqlglot-dialects/src/test/java/...");
    }
}
