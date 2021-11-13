package ftkxtk.validator;

import java.util.*;

public class Analyzer implements Ast.Visitor<Void> {

    private final HashMap<String, Ast.Statement.Transformation> transformations = new HashMap<>();
    private final HashMap<String, Ast.Statement.Lemma> lemmas = new HashMap<>();
    private final HashMap<Integer, Ast.Statement.Expression> lines = new HashMap<>();

    private List<Ast.Expression> exprTrace;
    private Queue<Ast.Expression> checkQueue;
    private HashMap<String, Ast.Expression> variables;


    public Analyzer() {
    }

    @Override
    public Void visit(Ast.Source ast) {
        ast.getStatements().forEach(this::visit);
        System.out.println("no error has been found on this proof.");
        return null;
    }

    @Override
    public Void visit(Ast.Statement.Transformation ast) {
        exprTrace = new LinkedList<>();
        visit(ast.getExpression());
        ast.setExprStructure(exprTrace);
        exprTrace = new LinkedList<>();
        visit(ast.getInference());
        ast.setInfrStructure(exprTrace);
        transformations.put(ast.getName(), ast);
        return null;
    }

    @Override
    public Void visit(Ast.Statement.Lemma ast) {
        exprTrace = new LinkedList<>();
        visit(ast.getExpression());
        ast.setStructure(exprTrace);
        lemmas.put(ast.getName(), ast);
        return null;
    }

    @Override
    public Void visit(Ast.Statement.Expression ast) {
        lines.put(ast.getLine(), ast);

        if(ast.getReason().getReason().equals("given")) return null;

        if(ast.getReason().getLines().size() > 0)
            checkTransformation(ast);
        else
            checkLemma(ast);
        return null;
    }

    public void checkLemma(Ast.Statement.Expression ast) {
        checkStructure(ast.getExpression(), lemmas.get(ast.getReason().getReason().toLowerCase()).getStructure());
    }

    public void checkTransformation(Ast.Statement.Expression ast) {
        var expression = lines.get(ast.getReason().getLines().get(0)).getExpression();
        for(int i = 1; i < ast.getReason().getLines().size(); i++) {
            expression = new Ast.Expression.Binary("\\and", expression, lines.get(ast.getReason().getLines().get(i)).getExpression());
        }

        var transformation = transformations.get(ast.getReason().getReason().toLowerCase().replace(" ", ""));

        checkStructure(expression, transformation.getExprStructure());
        checkStructure(ast.getExpression(), transformation.getInfrStructure(), this.variables);
    }

    @Override
    public Void visit(Ast.Expression.Literal ast) {

        exprTrace.add(ast);
        return null;
    }

    @Override
    public Void visit(Ast.Expression.Binary ast) {
        exprTrace.add(ast);
        visit(ast.getLeft());
        visit(ast.getRight());
        return null;
    }

    @Override
    public Void visit(Ast.Expression.Not ast) {

        exprTrace.add(ast);
        visit(ast.getExpression());
        return null;
    }

    @Override
    public Void visit(Ast.Expression.Variable ast) {
        exprTrace.add(ast);
        return null;
    }

    @Override
    public Void visit(Ast.Reason ast) {
        return null;
    }

    public void checkStructure(Ast.Expression expr, List<Ast.Expression> referenceStructure, HashMap<String, Ast.Expression> variables) {
        this.variables = variables;
        checkQueue = new LinkedList<>();
        checkQueue.addAll(referenceStructure);
        checkStructure(expr);
    }

    public void checkStructure(Ast.Expression expr, List<Ast.Expression> referenceStructure) {
        variables = new HashMap<>();
        checkQueue = new LinkedList<>();
        checkQueue.addAll(referenceStructure);
        checkStructure(expr);
    }

    public void checkStructure(Ast.Expression expr) {
        var refNode = checkQueue.remove();

        if(refNode instanceof Ast.Expression.Variable) {
            String varName = ((Ast.Expression.Variable)refNode).getName();
            if(variables.containsKey(varName)) {
                if(!variables.get(varName).equals(expr))
                    throw new RuntimeException("variable is inconsistent");
            } else {
                variables.put(varName, expr);
            }
            return;
        }

        if (expr.getClass() != refNode.getClass())
            throw new RuntimeException("Expression types mismatch");

        if (expr instanceof Ast.Expression.Binary) {
            checkStructure(((Ast.Expression.Binary) expr).getLeft());
            checkStructure(((Ast.Expression.Binary) expr).getRight());
        } else if (expr instanceof Ast.Expression.Not){
            checkStructure(((Ast.Expression.Not) expr).getExpression());
        } else {
            throw new RuntimeException("Unknown class " + expr.getClass());
        }

    }
}
