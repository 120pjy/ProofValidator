package ftkxtk.validator;

import java.util.*;

public class Analyzer implements Ast.Visitor<Void> {

    private final HashMap<String, Ast.Statement.Transformation> transformations = new HashMap<>();
    private final HashMap<String, Ast.Statement.Lemma> lemmas = new HashMap<>();
    private final HashMap<Integer, Ast.Statement.Expression> lines = new HashMap<>();

    private String currentPosition;

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
        currentPosition = "transposition "+ ast.getName();
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
        currentPosition = "lemma " + ast.getName();
        exprTrace = new LinkedList<>();

        if (ast.getExpression() instanceof Ast.Expression.Binary) {
            Ast.Expression.Binary lemmaExpression = (Ast.Expression.Binary)ast.getExpression();
            if (lemmaExpression.getOperator().equals("\\iff")) {
                Ast.Expression left = lemmaExpression.getLeft();
                Ast.Expression right = lemmaExpression.getRight();

                lemmaExpression = new Ast.Expression.Binary("\\implies", left, right);
                visit(lemmaExpression);
                ast.setStructure(exprTrace);
                exprTrace = new LinkedList<>();
                lemmaExpression = new Ast.Expression.Binary("\\implies", right, left);
                visit(lemmaExpression);
                ast.setOtherWayStructure(exprTrace);
                lemmas.put(ast.getName(), ast);
                return null;
            }
        }

        visit(ast.getExpression());
        ast.setStructure(exprTrace);
        lemmas.put(ast.getName(), ast);
        return null;
    }

    @Override
    public Void visit(Ast.Statement.Expression ast) {
        currentPosition = "Argument line #"+ast.getLine();
        lines.put(ast.getLine(), ast);

        if(ast.getReason().getReason().equals("given")) return null;

        if(ast.getReason().getLines().size() > 0)
            checkTransformation(ast);
        else
            checkLemma(ast);
        return null;
    }

    public void checkLemma(Ast.Statement.Expression ast) {
        if(!lemmas.containsKey(ast.getReason().getReason().toLowerCase()))
            throw new AnalyzeException("No such lemma " + ast.getReason().getReason(), currentPosition);
        Ast.Statement.Lemma lemma = lemmas.get(ast.getReason().getReason().toLowerCase());
        if (lemma.getOtherWayStructure() != null) {
            try {
                checkStructure(ast.getExpression(), lemma.getOtherWayStructure());
                return;
            } catch(AnalyzeException e) {}
        }
        try {
            checkStructure(ast.getExpression(), lemma.getStructure());
        }
        catch(AnalyzeException e) {
            throw new AnalyzeException(e.getMessage(), e.getPosition(), ast, lemma);
        }
    }

    public void checkTransformation(Ast.Statement.Expression ast) {
        if(!transformations.containsKey(ast.getReason().getReason().toLowerCase().replace(" ", "")))
            throw new AnalyzeException("No such transformation " + ast.getReason().getReason(), currentPosition);
        List<Integer> reasonLines = ast.getReason().getLines();
        List<List<Integer>> reasonPermutation = permute(reasonLines);
        var transformation = transformations.get(ast.getReason().getReason().toLowerCase().replace(" ", ""));

        if (reasonPermutation.isEmpty())
            throw new AnalyzeException("Missing line # for reason", currentPosition, ast);

        for (int j = 0; j<reasonPermutation.size(); j++) {
            List<Integer> p = reasonPermutation.get(j);
            try {
                Ast.Expression expression = lines.get(p.get(0)).getExpression();
                for (int i=1; i<p.size(); i++) {
                    expression = new Ast.Expression.Binary("\\and", expression, lines.get(p.get(i)).getExpression());
                }
                checkStructure(expression, transformation.getExprStructure());
                checkStructure(ast.getExpression(), transformation.getInfrStructure(), this.variables);
                return;
            }
            catch (AnalyzeException e) {
                if (j == reasonPermutation.size() - 1)
                    throw new AnalyzeException(e.getMessage(), e.getPosition(), ast, transformation);
            }
        }
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
                    throw new AnalyzeException("variable is inconsistent", currentPosition, expr);
            } else {
                variables.put(varName, expr);
            }
            return;
        }

        if (expr.getClass() != refNode.getClass())
            throw new AnalyzeException("Argument structure different from reasoning", currentPosition, expr);

        if (expr instanceof Ast.Expression.Binary) {
            checkStructure(((Ast.Expression.Binary) expr).getLeft());
            checkStructure(((Ast.Expression.Binary) expr).getRight());
        } else if (expr instanceof Ast.Expression.Not){
            checkStructure(((Ast.Expression.Not) expr).getExpression());
        } else {
            throw new AnalyzeException("Unknown class " + expr.getClass(), currentPosition, expr);
        }

    }

    private static <T> List<List<T>> permute(List<T> stmts) {
        List<List<T>> result = new ArrayList<List<T>>();

        //start from an empty list
        result.add(new ArrayList<T>());

        for (int i = 0; i < stmts.size(); i++) {
            //list of list in current iteration of the array num
            ArrayList<ArrayList<T>> current = new ArrayList<ArrayList<T>>();

            for (List<T> l : result) {
                // # of locations to insert is largest index + 1
                for (int j = 0; j < l.size()+1; j++) {
                    // + add num[i] to different locations
                    l.add(j, stmts.get(i));

                    ArrayList<T> temp = new ArrayList<T>(l);
                    current.add(temp);

                    // - remove num[i] add
                    l.remove(j);
                }
            }

            result = new ArrayList<List<T>>(current);
        }

        return result;
    }
}
