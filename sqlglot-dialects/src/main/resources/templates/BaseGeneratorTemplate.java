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
package com.gtkcyber.sqlglot.dialects.{PACKAGE_PATH};

import com.gtkcyber.sqlglot.expressions.Expression;
import com.gtkcyber.sqlglot.expressions.Nodes;
import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * {DIALECT_NAME}-specific SQL generator.
 *
 * The generator converts an AST (Abstract Syntax Tree) back to SQL text.
 * It's responsible for dialect-specific formatting, functions, and type syntax.
 *
 * TEMPLATE INSTRUCTIONS:
 * 1. Replace {DIALECT_NAME} with your dialect name
 * 2. Replace {GENERATOR_CLASS_NAME} with your generator class name
 * 3. Override methods to customize code generation
 * 4. Common methods to override:
 *    - formatIdentifier(): How to quote identifiers (backticks, double quotes, etc.)
 *    - transforms(): Map AST node types to custom generation functions
 *    - visitFunction(): Handle dialect-specific functions
 *    - visitCast(): Handle dialect-specific CAST syntax
 *    - visitDataType(): Handle dialect-specific data types
 *
 * FORMATTING PATTERNS:
 * - Use sql(expression) to recursively generate SQL for nested expressions
 * - Use formatIdentifier(name) to quote identifiers with dialect-specific style
 * - Use formatKeyword(keyword) to format keywords consistently
 * - Build strings with StringBuilder for efficiency
 */
public class {GENERATOR_CLASS_NAME} extends Generator {
    /**
     * Creates a new {DIALECT_NAME} generator.
     *
     * @param config Generator configuration options
     */
    public {GENERATOR_CLASS_NAME}(GeneratorConfig config) {
        super(config);
    }

    /**
     * Formats an identifier for {DIALECT_NAME}.
     *
     * This method determines how identifiers (table names, column names, etc.)
     * are quoted in the generated SQL.
     *
     * Examples:
     * - Double quotes: "column_name" (most dialects)
     * - Backticks: `column_name` (MySQL, Drill)
     * - Square brackets: [column_name] (SQL Server)
     * - No quoting: column_name (minimal dialects)
     *
     * @param name The identifier name
     * @return The formatted and quoted identifier
     */
    @Override
    protected String formatIdentifier(String name) {
        // TEMPLATE: Replace with your dialect's identifier quoting style
        // Example for double quotes:
        return "\"" + name.replace("\"", "\"\"") + "\"";

        // Example for backticks (MySQL/Drill style):
        // return "`" + name.replace("`", "``") + "`";

        // Example for square brackets (SQL Server style):
        // return "[" + name.replace("]", "]]") + "]";
    }

    /**
     * Provides dialect-specific AST transformations.
     *
     * This map allows you to replace the default generation logic for specific
     * AST node types with custom generation functions.
     *
     * Example:
     * <pre>
     * {@code
     * Map<Class<? extends Expression>, Function<Expression, String>> map = new HashMap<>();
     * map.put(Nodes.Function.class, expr -> {
     *     Nodes.Function func = (Nodes.Function) expr;
     *     if ("CUSTOM_FUNC".equalsIgnoreCase(func.getName())) {
     *         // Custom generation for CUSTOM_FUNC
     *         return "CUSTOM_GENERATION_" + sql(func.getArgs().get(0));
     *     }
     *     return null; // Fall back to default
     * });
     * return map;
     * }
     * </pre>
     *
     * @return Map of AST node type to generation function
     */
    @Override
    protected Map<Class<? extends Expression>, Function<Expression, String>> transforms() {
        Map<Class<? extends Expression>, Function<Expression, String>> map = new HashMap<>();

        // TEMPLATE: Add dialect-specific transformations here
        // Example: Handle dialect-specific functions
        // map.put(Nodes.Function.class, expr -> {
        //     Nodes.Function func = (Nodes.Function) expr;
        //     if ("{DIALECT_SPECIFIC_FUNC}".equalsIgnoreCase(func.getName())) {
        //         // Custom generation for {DIALECT_SPECIFIC_FUNC}
        //         return "{DIALECT_SPECIFIC_FUNC}" + "(" +
        //                String.join(", ", func.getArgs().stream().map(this::sql).collect(Collectors.toList())) +
        //                ")";
        //     }
        //     return null;
        // });

        return map;
    }

    /**
     * Generates SQL for function calls.
     *
     * Override this to handle dialect-specific functions with special syntax.
     *
     * Example:
     * <pre>
     * {@code
     * public String visitFunction(Nodes.Function node) {
     *     if ("SPECIAL_FUNC".equalsIgnoreCase(node.getName())) {
     *         // Special handling for SPECIAL_FUNC
     *         return "SPECIAL_FUNC_SYNTAX(" + sql(node.getArgs().get(0)) + ")";
     *     }
     *     return super.visitFunction(node);
     * }
     * }
     * </pre>
     *
     * @param node The function expression node
     * @return Generated SQL for the function
     */
    // @Override
    // public String visitFunction(Nodes.Function node) {
    //     // TEMPLATE: Add dialect-specific function generation here
    //     if ("{DIALECT_SPECIFIC_FUNC}".equalsIgnoreCase(node.getName())) {
    //         // Custom generation for {DIALECT_SPECIFIC_FUNC}
    //         StringBuilder sb = new StringBuilder();
    //         sb.append(formatKeyword("{DIALECT_SPECIFIC_FUNC}")).append("(");
    //         // Add function-specific argument formatting
    //         sb.append(")");
    //         return sb.toString();
    //     }
    //     return super.visitFunction(node);
    // }

    /**
     * Generates SQL for CAST expressions.
     *
     * Override this to handle dialect-specific CAST syntax.
     *
     * Example:
     * <pre>
     * {@code
     * public String visitCast(Nodes.Cast node) {
     *     String expr = sql(node.getExpression());
     *     String type = sql(node.getDataType());
     *     return formatKeyword("CAST") + "(" + expr + " " + formatKeyword("AS") + " " + type + ")";
     * }
     * }
     * </pre>
     *
     * @param node The CAST expression node
     * @return Generated SQL for the CAST
     */
    // @Override
    // public String visitCast(Nodes.Cast node) {
    //     // TEMPLATE: Add dialect-specific CAST generation here
    //     String expr = sql(node.getExpression());
    //     String type = sql(node.getDataType());
    //     return formatKeyword("CAST") + "(" + expr + " " + formatKeyword("AS") + " " + type + ")";
    // }

    /**
     * Generates SQL for data type expressions.
     *
     * Override this to handle dialect-specific data type syntax.
     *
     * Example:
     * <pre>
     * {@code
     * public String visitDataType(Nodes.DataType node) {
     *     if ("CUSTOM_TYPE".equalsIgnoreCase(node.getName())) {
     *         return "CUSTOM_TYPE_REPRESENTATION";
     *     }
     *     return super.visitDataType(node);
     * }
     * }
     * </pre>
     *
     * @param node The data type node
     * @return Generated SQL for the data type
     */
    // @Override
    // public String visitDataType(Nodes.DataType node) {
    //     // TEMPLATE: Add dialect-specific data type generation here
    //     return super.visitDataType(node);
    // }

    /**
     * Generates SQL for table references.
     *
     * Override this to handle dialect-specific table path syntax.
     *
     * @param node The table node
     * @return Generated SQL for the table
     */
    // @Override
    // public String visitTable(Nodes.Table node) {
    //     // TEMPLATE: Add dialect-specific table reference generation here
    //     return super.visitTable(node);
    // }

    /**
     * Generates SQL for column references.
     *
     * @param node The column node
     * @return Generated SQL for the column
     */
    // @Override
    // public String visitColumn(Nodes.Column node) {
    //     // TEMPLATE: Add dialect-specific column reference generation here
    //     return super.visitColumn(node);
    // }
}
