package ftkxtk.validator;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.List;

public class Environment {
    public static final HashMap<String, Consumer<List<Ast.Statement>>> LOGICS = new HashMap<>();

    // logic(current line, reference line #1, reference line #2, ...)
    public static void registerLogic(String name, Consumer<List<Ast.Statement>> function) {
        if (LOGICS.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate registration of logic " + name + ".");
        }
        LOGICS.put(name, function);
    }

    static {
        LOGICS.put("modus ponens", args->{
        });
    }
}