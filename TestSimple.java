import io.sqlglot.SqlGlot;
import io.sqlglot.expressions.Expression;
import io.sqlglot.parser.Parser;
import java.util.Optional;

public class TestSimple {
    public static void main(String[] args) {
        System.out.println("Starting test...");
        try {
            Parser parser = new Parser();
            System.out.println("Parser created");
            
            String sql = "SELECT a FROM t";
            System.out.println("Parsing: " + sql);
            var stmts = parser.parse(sql);
            System.out.println("Parse result: " + stmts.size() + " statements");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
