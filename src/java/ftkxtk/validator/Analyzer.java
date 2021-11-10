package ftkxtk.validator;

import java.util.HashMap;
import java.util.function.Consumer;

public class Analyzer {
    private HashMap<Integer, Ast.Statement> lines;
    public Analyzer(Ast.Source src) {
        lines = new HashMap<>();
        for(Ast.Statement stmt : src.getStatements()) {
            lines.put(stmt.getLine(), stmt);
        }
    }
    public void analyze() {

    }
}
