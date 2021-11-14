import ftkxtk.validator.*;

import java.util.List;
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
        String input = """
                (A1) p -> (~ q -> p)
                                
                [modus ponens] ((p -> q) ^ p) |- q
                [hypothetical syllogism] ((p -> q) ^ (q -> r)) |- (p -> r)
                                
                (HS1) ( q -> r) -> ((p -> q) -> (p -> r))
                (DN1) ~ ~ p -> p
                (TR1) (p -> q) -> (~ q -> ~ p)
                                
                #1 p -> (~ q -> p)                                  A1
                #2 (~ q -> p) -> (~ p -> ~ ~ q) 	                TR1
                #3 p -> ( ~ p -> ~ ~ q) 	                        hypothetical syllogism (1, 2)
                #4 ~ ~ q -> q 	                                    DN1
                #5 (~ ~ q -> q) -> ((~ p -> ~ ~ q) -> (~ p -> q)) 	HS1
                #6 (~ p -> ~ ~ q) -> (~ p -> q) 	                modus ponens (5, 4)
                #7 p -> ( ~ p -> q) 	                            hypothetical syllogism (3, 6)
                """;
        try {
            Lexer lexer = new Lexer(input);
            List<Token> tokens = lexer.lex();
            System.out.println(tokens);
            Parser parser = new Parser(tokens);
            Ast.Source ast = parser.parseSource();
            Analyzer analyzer = new Analyzer();
            analyzer.visit(ast);
        } catch(ParseException e) {
            System.out.println("Parse Exception: " + e.getMessage() + " at " + e.getIndex());
        } catch(AnalyzeException e) {
            System.out.println("Analyze Exception: " + e.getMessage() + " at " + e.getPosition());
        }
    }

    private static void printAst(Ast root) {
        printAst(root, 0);
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
        } else if(root instanceof Ast.Statement.Lemma) {
            System.out.println("Lemma Statement - " + ((Ast.Statement.Lemma)root).getName());
            printAst(((Ast.Statement.Lemma)root).getExpression(), indent + 1);
        } else if(root instanceof Ast.Statement.Expression) {
            System.out.println("Expression Statement - " + ((Ast.Statement.Expression)root).getLine());
            printAst(((Ast.Statement.Expression)root).getExpression(), indent + 1);
            printAst(((Ast.Statement.Expression)root).getReason(), indent + 1);
        } else if(root instanceof Ast.Expression.Binary) {
//            System.out.println("Binary(" + ((Ast.Expression.Binary)root).getOperator() + ")");
            System.out.println(((Ast.Expression.Binary)root).getOperator());
            printAst(((Ast.Expression.Binary)root).getLeft(), indent + 1);
            printAst(((Ast.Expression.Binary)root).getRight(), indent + 1);
        } else if(root instanceof Ast.Expression.Literal) {
            System.out.println("Literal(" + ((Ast.Expression.Literal)root).getLiteral() +")");
        } else if(root instanceof Ast.Expression.Not) {
            System.out.println("Not");
            printAst(((Ast.Expression.Not)root).getExpression(), indent + 1);
        }  else if(root instanceof Ast.Expression.Variable) {
//            System.out.println("Var(" + ((Ast.Expression.Variable)root).getName() +", " + ((Ast.Expression.Variable)root).getValue() +")");
            System.out.println("Var(" + ((Ast.Expression.Variable)root).getName() +")");
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
                #2 (\\not q \\implies p) \\implies (\\not p \\implies \\not \\not q) \t Contrapositive (1)
                #3 p \\implies (\\not p \\implies \\not \\not q) \t hypothetical syllogism (1, 2)
                #4 \\not \\not q \\implies q \t double negation
                #5 (\\not \\not q \\implies q) \\implies ((\\not p \\implies \\not \\not q) \\implies (\\not p \\implies q)) \t Hypothetical syllogism
                #6 (\\not p \\implies \\not \\not q) \\implies (\\not p \\implies q) \t modus ponens(4, 5)
                #7 p \\implies (\\not p \\implies q) \t hypothetical syllogism (3, 6)""";
    }

    public static String getSecondExample() {
        return """
                [modus ponens] ((p \\implies q) \\and p) \\infer q
                [hypothetical syllogism] ((p \\implies q) \\and (q \\implies r)) \\infer (p \\implies r)
                (HS) ( p \\implies q) \\implies ((q \\implies r) \\implies (p \\implies r))
                #1 ((p \\implies q) \\implies (q \\implies r)) \\implies (((q \\implies r) \\implies r) \\implies ((p \\implies q) \\implies r))\t HS
                #2 p \\implies q\tgiven
                #3 p\tgiven
                #4 q\tmodusponens (2, 3)
                """;
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

