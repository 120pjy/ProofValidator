package ftkxtk.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Analyzer {
    private HashMap<Integer, Ast.Statement> lines;
    public Analyzer(Ast.Source src) {
        lines = new HashMap<>();
        for(Ast.Statement stmt : src.getStatements()) {
            if(stmt instanceof Ast.Statement.Expression)
                lines.put(stmt.getLine(), removeGroup((Ast.Statement.Expression) stmt));
            else
                lines.put(stmt.getLine(), (stmt));
        }
    }

    private static Ast.Statement.Expression removeGroup(Ast.Statement.Expression stmt) {
        return new Ast.Statement.Expression(stmt.getLine(), stmt.getReason(), removeGroup(stmt.getExpression()));
    }

    private static Ast.Expression removeGroup(Ast.Expression expr) {
        if(expr instanceof Ast.Expression.Group)
            return removeGroup(((Ast.Expression.Group) expr).getExpression());
        else if(expr instanceof Ast.Expression.Binary binary) {
            return new Ast.Expression.Binary(binary.getOperator(), removeGroup(binary.getLeft()), removeGroup(binary.getRight()));
        }
        else if(expr instanceof Ast.Expression.Not)
            return new Ast.Expression.Not(removeGroup(((Ast.Expression.Not) expr).getExpression()));
        else if(expr instanceof Ast.Expression.Variable)
            return expr;
        else if(expr instanceof Ast.Expression.Literal)
            return expr;
        else
            throw new RuntimeException("invalid expr");
    }

    public void analyze() {
        lines.forEach((line, stmt)->{
            System.out.println(stmt);
            Ast.Reason reason = stmt.getReason();
            String reasonName = reason.getReason().toLowerCase();
            List<Integer> reasonArgs = reason.getLines();
            List<Ast.Statement> reasonLines = new ArrayList<>();
            reasonLines.add(stmt);
            for(Integer i : reasonArgs) {
                reasonLines.add(lines.get(i));
            }
            Environment.LOGICS.get(reasonName).accept(reasonLines);
        });

        System.out.println("All Logic is valid");
    }
}
