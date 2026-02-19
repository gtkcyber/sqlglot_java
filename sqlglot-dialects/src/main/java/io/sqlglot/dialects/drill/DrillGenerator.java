package io.sqlglot.dialects.drill;

import io.sqlglot.expressions.Expression;
import io.sqlglot.expressions.Nodes;
import io.sqlglot.generator.Generator;
import io.sqlglot.generator.GeneratorConfig;

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
        // Drill prefers backticks for identifiers
        if (config.identify()) {
            return "`" + name.replace("`", "``") + "`";
        }
        return name;
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
