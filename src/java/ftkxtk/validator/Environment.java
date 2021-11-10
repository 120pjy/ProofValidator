package ftkxtk.validator;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

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
        registerLogic("double negation", args->{
            if(args.size() != 1)
                throw new IllegalArgumentException("no line needed");
            Ast.Statement currentStatement = args.get(0);
            if (!(currentStatement instanceof Ast.Statement.Expression && ((Ast.Statement.Expression) currentStatement).getExpression() instanceof Ast.Expression.Binary currentExpression))
                throw new RuntimeException("current statement not a binary expression");
            if(!(currentExpression.getOperator().equals("\\implies")) && !currentExpression.getOperator().equals("\\iff"))
                throw new RuntimeException("current expression not implies or iff");
            if(new Ast.Expression.Not(new Ast.Expression.Not(currentExpression.getLeft())).equals(currentExpression.getRight())
                || new Ast.Expression.Not(new Ast.Expression.Not(currentExpression.getRight())).equals(currentExpression.getLeft())) {
                return;
            }
            throw new RuntimeException("No double negation for you");
        });
        registerLogic("contrapositive", args->{
            if(args.size() != 2)
                throw new IllegalArgumentException("only one line needed");
            Ast.Statement currentStatement = args.get(0);
            Ast.Statement refStatement = args.get(1);

            if (!(currentStatement instanceof Ast.Statement.Expression))
                throw new RuntimeException("current statement not an expression");
            if (!(refStatement instanceof Ast.Statement.Expression))
                throw new RuntimeException("reference statement not an expression");
            Ast.Expression currentExpression = ((Ast.Statement.Expression)currentStatement).getExpression();
            Ast.Expression refExpression = ((Ast.Statement.Expression)refStatement).getExpression();
            if(!(currentExpression instanceof Ast.Expression.Binary currentImplyExpression && ((Ast.Expression.Binary)currentExpression).getOperator().equals("\\implies")))
                throw new RuntimeException("current statement not an imply expression");
            if(!(refExpression instanceof Ast.Expression.Binary refImplyExpression && ((Ast.Expression.Binary)refExpression).getOperator().equals("\\implies")))
                throw new RuntimeException("reference statement not an imply expression");

            Ast.Expression negateRefLeft = new Ast.Expression.Not(refImplyExpression.getLeft());
            Ast.Expression negateRefRight = new Ast.Expression.Not(refImplyExpression.getRight());
            if(negateRefLeft.equals(currentImplyExpression.getRight()) && negateRefRight.equals(currentImplyExpression.getLeft()))
                return;
            throw new RuntimeException("no contra-positive for you");
        });
        registerLogic("modus ponens", args->{
            if (args.size() != 3)
                throw new IllegalArgumentException("I think we are fucked up.");
            Ast.Statement currentStatement = args.get(0);

            if (!(currentStatement instanceof Ast.Statement.Expression))
                throw new RuntimeException("Motherfucking Bitch, we fucked up");

            Ast.Expression current = ((Ast.Statement.Expression) currentStatement).getExpression();

            Ast.Statement statement1 = args.get(1);
            Ast.Statement statement2 = args.get(2);

            if (!(statement1 instanceof Ast.Statement.Expression && statement2 instanceof Ast.Statement.Expression))
                throw new RuntimeException("Motherfucking Bitch, we fucked up");

            Ast.Expression expr1 = ((Ast.Statement.Expression)statement1).getExpression();
            Ast.Expression expr2 = ((Ast.Statement.Expression)statement2).getExpression();

            Ast.Expression.Binary left;
            Ast.Expression right;
            if ((expr1 instanceof Ast.Expression.Binary && ((Ast.Expression.Binary)expr1).getOperator().equals("\\implies"))) {
                left = ((Ast.Expression.Binary)expr1);
                right = expr2;
                if (left.getLeft().equals(right) && left.getRight().equals(current))
                    return;
            }
            if ((expr2 instanceof Ast.Expression.Binary && ((Ast.Expression.Binary)expr2).getOperator().equals("\\implies"))) {
                left = ((Ast.Expression.Binary)expr2);
                right = expr1;
                if (left.getLeft().equals(right) && left.getRight().equals(current))
                    return;
            }
            throw new RuntimeException("fuck!");
        });

        registerLogic("given", args->{
           if (args.size() == 1) return;
           throw new RuntimeException("Given does not need a reason");
        });

        registerLogic("hypothetical syllogism", args -> {
            if (args.size() != 3) throw new RuntimeException("Hypothetical syllogism must have 2 reasons");

            for (Ast.Statement stmt: args)
                if (!(stmt instanceof Ast.Statement.Expression && ((Ast.Statement.Expression)stmt).getExpression() instanceof Ast.Expression.Binary && ((Ast.Expression.Binary) ((Ast.Statement.Expression)stmt).getExpression()).getOperator().equals("\\implies"))) throw new RuntimeException("All statements must be an implication expression statement");

            Ast.Expression.Binary current = (Ast.Expression.Binary)((Ast.Statement.Expression)args.get(0)).getExpression();
            Ast.Expression.Binary implies1 = (Ast.Expression.Binary)((Ast.Statement.Expression)args.get(1)).getExpression();
            Ast.Expression.Binary implies2 = (Ast.Expression.Binary)((Ast.Statement.Expression)args.get(2)).getExpression();

            if (implies1.getRight().equals(implies2.getLeft()) && implies1.getLeft().equals(current.getLeft()) && implies2.getRight().equals(current.getRight()))
                return;
            if (implies2.getRight().equals(implies1.getLeft()) && implies2.getLeft().equals(current.getLeft()) && implies1.getRight().equals(current.getRight()))
                return;

            throw new RuntimeException("Your hypothetical syllogism ain't right boy.");


        });


    }
}