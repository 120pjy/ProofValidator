package ftkxtk.validator;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

public final class Parser {

    private final TokenStream tokens;

    public Parser(List<Token> tokens) {
        this.tokens = new TokenStream(tokens);
    }

    public Ast.Source parseSource() throws ParseException {
        List<Ast.Statement> statements = new ArrayList<>();
        while (tokens.has(0)) {
            Ast.Statement stmt = parseStatement();
            if (stmt == null) throw error("Can't parse statement");
            statements.add(stmt);
        }
        return new Ast.Source(statements);
    }

    /**
     * Parses the {@code statement} rule and delegates to the necessary method.
     * If the next tokens do not start a declaration, if, while, or return
     * statement, then it is an expression/assignment statement.
     */
    public Ast.Statement parseStatement() throws ParseException {
        if(peek("("))
            return parseLemmaStatement();
        else if(peek("["))
            return parseTransformationStatement();
        return parseExpressionStatement();
    }

    public Ast.Statement.Lemma parseLemmaStatement() throws ParseException {
        if (!match("(")) throw error("No (");
        if(!match(Token.Type.IDENTIFIER)) throw error("No name");
        String name = tokens.get(-1).getLiteral();
        if (!match(")")) throw error("No )");
        Ast.Expression expr = parseImplicationExpression();
        return new Ast.Statement.Lemma(name.toLowerCase(), expr);
    }

    public Ast.Statement.Transformation parseTransformationStatement() throws ParseException {
        if (!match("[")) throw error("No [");
        StringBuilder name = new StringBuilder();
        if(!peek(Token.Type.IDENTIFIER))
            throw error("No name");
        while(match(Token.Type.IDENTIFIER)) {
            name.append(tokens.get(-1).getLiteral());
        }
        if (!match("]")) throw error("No ]");
        Ast.Expression expr = parseImplicationExpression();

        if (!match("\\infer") && !match("|-")) throw error("No \\infer or |-");
        Ast.Expression inference = parseImplicationExpression();

        return new Ast.Statement.Transformation(name.toString().toLowerCase(), expr, inference);
    }

    public Ast.Statement.Expression parseExpressionStatement() throws ParseException {
        if (!match("#", Token.Type.Number)) throw error("No Line number");
        int line = Integer.parseInt(tokens.get(-1).getLiteral());
        Ast.Expression expr = parseImplicationExpression();
        if(!match("<")) throw error("< expected");
        Ast.Reason reason = parseReason();
        if(!match(">")) throw error("> expected");
        return new Ast.Statement.Expression(line, reason, expr);
    }

    public Ast.Expression parseExpression() throws ParseException {
        return parseImplicationExpression();
    }

    public Ast.Expression parseImplicationExpression() throws ParseException {
        Ast.Expression expr = parseLogicalExpression();
        while (match("\\implies") || match("->") || match("\\iff") || match("<->") || match("=")) {
            String operator = tokens.get(-1).getLiteral();
            if(operator.equals("->"))
                operator = "\\implies";
            if(operator.equals("<->"))
                operator = "\\iff";
            Ast.Expression right = parseLogicalExpression();
            expr = new Ast.Expression.Binary(operator, expr, right);
        }
        return expr;
    }

    public Ast.Expression parseLogicalExpression() throws ParseException {
        Ast.Expression expr = parsePrimaryExpression();
        while (match("\\and") || match("\\or") || match("^") || match("+")) {
            String operator = tokens.get(-1).getLiteral();
            if(operator.equals("^"))
                operator = "\\and";
            if(operator.equals("+"))
                operator = "\\or";
            Ast.Expression right = parsePrimaryExpression();
            expr = new Ast.Expression.Binary(operator, expr, right);
        }
        return expr;
    }

    public Ast.Expression parsePrimaryExpression() throws ParseException {
        if(match("true") || match("false")) {
            return new Ast.Expression.Literal(Boolean.valueOf(tokens.get(-1).getLiteral()));
        } else if(match(Token.Type.IDENTIFIER)) {
            return new Ast.Expression.Variable(tokens.get(-1).getLiteral());
        } else if (match(Token.Type.Number)) {
            return new Ast.Expression.Literal(new BigDecimal(tokens.get(-1).getLiteral()));
        } else if (match("\\not") || match("~")) {
            return parseNot();
        } else if (match("(")){
            return parseGroup();
        } else {
            throw error("Invalid expression.");
        }
    }

    private Ast.Expression parseGroup() throws ParseException {
        Ast.Expression expr = parseExpression();
        if (!match(")")) {
            throw error(") expected.");
        }
        return expr;
    }

    private Ast.Expression parseNot() throws ParseException {
        return new Ast.Expression.Not(parsePrimaryExpression());
    }

    public Ast.Reason parseReason() throws ParseException {
        if(!match(Token.Type.IDENTIFIER))
            throw error("at least one identifier expected.");
        StringBuilder reason = new StringBuilder(tokens.get(-1).getLiteral());
        while(match(Token.Type.IDENTIFIER)) {
            reason.append(" ").append(tokens.get(-1).getLiteral());
        }
        List<Integer> lines = new ArrayList<>();
        if(match("(")) {
            while (!match(")")) {
                if (!match(Token.Type.Number)) {
                    throw error(") expected.");
                }
                lines.add(Integer.parseInt(tokens.get(-1).getLiteral()));
                if (!match(",") && !peek(")")) {
                    throw error(", expected.");
                }
            }
        }

        return new Ast.Reason(reason.toString(), lines);

    }

    private boolean peek(Object... patterns) {
        for (int i=0; i<patterns.length; i++) {
            if (!tokens.has(i)) {
                return false;
            }
            else if (patterns[i] instanceof Token.Type) {
                if (patterns[i] != tokens.get(i).getType()) {
                    return false;
                }
            }
            else if (patterns[i] instanceof String) {
                if (!patterns[i].equals(tokens.get(i).getLiteral())) {
                    return false;
                }
            }
            else {
                throw new AssertionError("Invalid Pattern Object: "+patterns[i].getClass());
            }
        }
        return true;
    }

    private boolean match(Object... patterns) {
        boolean peek = peek(patterns);

        if (peek)
            for (int i=0; i<patterns.length; i++)
                tokens.advance();

        return peek;
    }

    private ParseException error (String message){
        return new ParseException(message, tokens.has(0) ? tokens.get(0).getLine() : tokens.get(-1).getLine(), tokens.has(0) ? tokens.get(0).getIndex() : tokens.get(-1).getIndex() + tokens.get(-1).getLiteral().length());
    }

    private static final class TokenStream {

        private final List<Token> tokens;
        private int index = 0;

        private TokenStream(List<Token> tokens) {
            this.tokens = tokens;
        }

        public boolean has(int offset) {
            return index + offset < tokens.size();
        }

        public Token get(int offset) {
            return tokens.get(index + offset);
        }

        public void advance() {
            index++;
        }

    }


}
