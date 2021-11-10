package ftkxtk.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Analyzer {
    private HashMap<Integer, Ast.Statement> lines;

    private static Ast.Expression removeGroupExpression(Ast.Expression expr) {
        if (expr instanceof Ast.Expression.Not) {
            return new Ast.Expression.Not(removeGroupExpression(((Ast.Expression.Not)expr).getExpression()));
        }
        else if (expr instanceof Ast.Expression.Binary) {
            Ast.Expression.Binary binary = (Ast.Expression.Binary) expr;
            Ast.Expression left = removeGroupExpression(binary.getLeft());
            Ast.Expression right = removeGroupExpression(binary.getRight());
            return new Ast.Expression.Binary(binary.getOperator(), left, right);
        }
        if (expr instanceof Ast.Expression.Group)
            return ((Ast.Expression.Group)expr).getExpression();
        return expr;
    }

    private static Ast.Statement removeGroupStatement(Ast.Statement stmt) {
        if (stmt instanceof Ast.Statement.Expression) {
            Ast.Expression expr = ((Ast.Statement.Expression)stmt).getExpression();
            expr = removeGroupExpression(expr);
            return new Ast.Statement.Expression(stmt.getLine(), stmt.getReason(), expr);
        }
        else return stmt;
    }

    public Analyzer(Ast.Source src) {
        lines = new HashMap<>();
        for(Ast.Statement stmt : src.getStatements()) {
            stmt = removeGroupStatement(stmt);
            lines.put(stmt.getLine(), stmt);
        }
    }
    public void analyze() {
        lines.forEach((line, stmt)->{
            Ast.Reason reason = stmt.getReason();
            String reasonName = reason.getReason().toLowerCase();
            List<Integer> reasonArgs = reason.getLines();
            List<Ast.Statement> reasonLines = new ArrayList<>();
            reasonLines.add(stmt);
            for(Integer i : reasonArgs) {
                reasonLines.add(lines.get(i));
            }
            System.out.println(line);
            Environment.LOGICS.get(reasonName).accept(reasonLines);
        });

        System.out.println("All Logic is valid");
    }
}
