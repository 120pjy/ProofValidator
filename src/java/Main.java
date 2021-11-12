import ftkxtk.validator.*;
/*
#1 Let a = b    Modus Ponens (1, 2)
#2 Let b = c    Reason (1, 2)
#3 Let a = c    Reason (3, 4)
#4 p \-> q      Given

#1 LET p = "humans"           given
#2 LET q = live up to 70    given
#

Line(#1) Identifier(Let) Identifier(a) Operator(=) Identifier(b) Reason(\t) Identifier(Nodus) Identifier(Ponens) Operator('(') Number (1) Number(2)
 */

public class Main {
    public static void main(String[] args) {
        String input = getFirstExample();
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer.lex());
        Analyzer analyzer = new Analyzer(parser.parseSource());
        analyzer.analyze();

    }

    private static void printAst(Ast root, int indent) {
        for(int i = 0 ; i < indent; i ++)
            System.out.print(" ");
        System.out.print("ㄴ");
        if(root instanceof Ast.Source) {
            System.out.println("Source");
            for(var ast : ((Ast.Source) root).getStatements()) {
                printAst(ast, indent + 1);
            }
        } else if(root instanceof Ast.Statement.Expression) {
            System.out.println("Expression Statement - " + ((Ast.Statement.Expression)root).getLine());
            printAst(((Ast.Statement.Expression)root).getExpression(), indent + 1);
            printAst(((Ast.Statement.Expression)root).getReason(), indent + 1);
        } else if(root instanceof Ast.Expression.Binary) {
            System.out.println("Binary(" + ((Ast.Expression.Binary)root).getOperator() + ")");
            printAst(((Ast.Expression.Binary)root).getLeft(), indent + 1);
            printAst(((Ast.Expression.Binary)root).getRight(), indent + 1);
        } else if(root instanceof Ast.Expression.Literal) {
            System.out.println("Literal(" + ((Ast.Expression.Literal)root).getLiteral() +")");
        } else if(root instanceof Ast.Expression.Not) {
            System.out.println("Not");
            printAst(((Ast.Expression.Not)root).getExpression(), indent + 1);
        }  else if(root instanceof Ast.Expression.Variable) {
            System.out.println("Variable(" + ((Ast.Expression.Variable)root).getName() +", " + ((Ast.Expression.Variable)root).getValue() +")");
        } else if(root instanceof Ast.Expression.Group) {
            System.out.println("Group");
            printAst(((Ast.Expression.Group)root).getExpression(), indent + 1);
        } else if(root instanceof Ast.Reason) {
            System.out.print("Reason - ");
            System.out.print(((Ast.Reason)root).getReason());

            if(!(((Ast.Reason)root).getLines().isEmpty())) {
                System.out.print("(");
                for (int line : ((Ast.Reason) root).getLines())
                    System.out.print(line + ", ");
                System.out.println(")");
            } else {
                System.out.println();
            }

        } else {
            System.out.println(root.getClass());
            System.out.println(root);
        }
    }

    private static String getFirstExample() {
        /* Reasons are
            1. Given
            2. Contrapositive
            3. Hypothetical syllogism - 1
            4. Hypothetical  syllogism - 2
            5. Double negation
            6. Modus ponens

         */
        return """
                #1 p \\implies (\\not q \\implies p) \t given
                #2 (\\not q \\implies p) \\implies (\\not p \\implies \\not \\not q) \t Contrapositive
                #3 p \\implies (\\not p \\implies \\not \\not q) \t hypothetical syllogism (1, 2)
                #4 \\not \\not q \\implies q \t double negation
                #5 (\\not \\not q \\implies q) \\implies ((\\not p \\implies \\not \\not q) \\implies (\\not p \\implies q)) \t Hypothetical syllogism
                #6 (\\not p \\implies \\not \\not q) \\implies (\\not p \\implies q) \t modus ponens(4, 5)
                #7 p \\implies (\\not p \\implies q) \t hypothetical syllogism (3, 6)""";
    }

    public static String getModusPonens() {
        return """
                #5 (p \\implies q) \t given
                #7 (p \\implies q) \\implies r \t given
                #10 r \t modus ponens(5, 7)
                """;
    }
    public static String getHypotheticalSyllogism() {
        return """
                #5 q \\implies r \t given
                #6 p \\implies q \t given
                #7 p \\implies r \t hypothetical syllogism(5, 6)
                """;
    }
          
    public static String getContraPositive() {
        return """
                #5 p \\implies q \t given
                #10 \\not q \\implies \\not p \t contrapositive(5)
                """;
    }

    public static String getDoubleNegation() {
        return """
                #5 \\not \\not p \\implies p \t double negation
                #10 q \\implies \\not \\not q \t double negation
                """;
    }
}

/*
#1 p \implies (\not q \implies p) \t given
#2 (\not q \implies p) \implies (\not p \implies \not \not q) \t Contrapositive (1)
#3 p \implies (\not p \implies \not \not q) hypothetical syllogism (1, 3)
#4 \not \not q \implies q double negation
#5 (\not \not q \implies q) \implies ((\not p \implies \not \not q) \implies (\not p \implies q)) Hypothetical syllogism (2, 4)
#6 (\not p \implies \not \not q) \implies (\not p \implies q) modus ponens(4, 5)
#7 p \implies (\not p \implies q) hypothetical syllogism (3, 6)
 */

