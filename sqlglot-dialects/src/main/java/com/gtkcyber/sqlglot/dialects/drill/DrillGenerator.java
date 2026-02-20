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
package com.gtkcyber.sqlglot.dialects.drill;

import com.gtkcyber.sqlglot.expressions.Expression;
import com.gtkcyber.sqlglot.expressions.Nodes;
import com.gtkcyber.sqlglot.generator.Generator;
import com.gtkcyber.sqlglot.generator.GeneratorConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Apache Drill-specific SQL generator.
 *
 * Generates SQL optimized for Apache Drill:
 * - Uses backtick identifiers
 * - Workspace.schema.table syntax
 * - Drill-specific functions (FLATTEN, etc.)
 * - Nested type support
 */
public class DrillGenerator extends Generator {
    public DrillGenerator(GeneratorConfig config) {
        super(config);
    }

    @Override
    protected String formatIdentifier(String name) {
        // Drill always uses backticks for identifiers
        return "`" + name.replace("`", "``") + "`";
    }

    @Override
    protected Map<Class<? extends Expression>, Function<Expression, String>> transforms() {
        Map<Class<? extends Expression>, Function<Expression, String>> map = new HashMap<>();

        // Drill-specific transformations can be added here
        // For example, handling workspace paths specially
        map.put(Nodes.WorkspacePath.class, expr -> {
            Nodes.WorkspacePath ws = (Nodes.WorkspacePath) expr;
            return "`" + String.join("`.`", ws.getPath()) + "`";
        });

        return map;
    }

    /**
     * Generates FLATTEN function with Drill-specific syntax.
     */
    @Override
    public String visitFunction(Nodes.Function node) {
        if ("FLATTEN".equalsIgnoreCase(node.getName())) {
            StringBuilder sb = new StringBuilder("FLATTEN(");
            if (!node.getArgs().isEmpty()) {
                sb.append(sql(node.getArgs().get(0)));
                if (node.getArgs().size() > 1) {
                    sb.append(", ").append(sql(node.getArgs().get(1)));
                }
            }
            sb.append(")");
            return sb.toString();
        }

        // Fall back to standard function generation
        return super.visitFunction(node);
    }

    /**
     * Generates CAST statements with Drill's extended syntax for complex types.
     */
    @Override
    public String visitCast(Nodes.Cast node) {
        String expr = sql(node.getExpression());
        String type = sql(node.getDataType());

        // Drill supports CAST(... AS type) with complex types like ARRAY<INT>, MAP<STRING, INT>
        return formatKeyword("CAST") + "(" + expr + " " + formatKeyword("AS") + " " + type + ")";
    }

    @Override
    public String visitTable(Nodes.Table node) {
        // Drill supports workspace.schema.table syntax
        StringBuilder sb = new StringBuilder();

        if (node.getSchema() != null) {
            // Schema is in format "workspace.schema" or just "schema"
            sb.append("`").append(node.getSchema()).append("`.");
        }

        sb.append("`").append(node.getName()).append("`");
        return sb.toString();
    }

    @Override
    public String visitWorkspacePath(Nodes.WorkspacePath node) {
        return "`" + String.join("`.`", node.getPath()) + "`";
    }

    @Override
    public String visitDot(Nodes.Dot node) {
        // For workspace paths, keep as backtick-quoted path
        return sql(node.getLeft()) + "." + sql(node.getRight());
    }
}
