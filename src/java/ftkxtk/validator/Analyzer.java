package ftkxtk.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Analyzer {
    private HashMap<Integer, Ast.Statement> lines;
    public Analyzer(Ast.Source src) {
        lines = new HashMap<>();
        for(Ast.Statement stmt : src.getStatements()) {
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
            Environment.LOGICS.get(reasonName).accept(reasonLines);
        });

        System.out.println("All Logic is valid");
    }
}
