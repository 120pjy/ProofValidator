package ftkxtk.validator;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

import static ftkxtk.validator.Token.Type.Line;

public final class Parser {

    private final TokenStream tokens;

    private int line;

    public Parser(List<Token> tokens) {
        this.tokens = new TokenStream(tokens);
    }

    public Ast.Source parseSource() throws ParseException {
        List<Ast.Statement> statements = new ArrayList<>();
        while (tokens.has(0)) {
            Ast.Statement stmt = parseStatement();
            if (stmt == null) throw new ParseException("Can't parse statement", tokens.index);
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
         return parseExpressionStatement();
    }

    public Ast.Statement.Expression parseExpressionStatement() throws ParseException {
        if (!match(Line)) throw new ParseException("No Line number", tokens.index);
        line = Integer.parseInt(tokens.get(-1).getLiteral().substring(1));
        Ast.Expression expr = parseImplicationExpression();
        Ast.Reason reason = parseReason();
        return new Ast.Statement.Expression(line, reason, expr);
    }

    public Ast.Expression parseExpression() throws ParseException {
        return parseImplicationExpression();
    }

    public Ast.Expression parseImplicationExpression() throws ParseException {
        Ast.Expression expr = parseLogicalExpression();
        while (match("\\implies") || match("\\iff") || match("=")) {
            String operator = tokens.get(-1).getLiteral();
            Ast.Expression right = parseLogicalExpression();
            expr = new Ast.Expression.Binary(operator, expr, right);
        }
        return expr;
    }

    public Ast.Expression parseLogicalExpression() throws ParseException {
        Ast.Expression expr = parsePrimaryExpression();
        while (match("\\and") || match("\\or")) {
            String operator = tokens.get(-1).getLiteral();
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
        } else if (match("\\not")) {
            return parseNot();
        } else if (match("(")){
            return parseGroup();
        } else {
            throw new ParseException("Invalid expression.", tokens.index);
        }
    }

    private Ast.Expression.Group parseGroup() throws ParseException {
        Ast.Expression expr = parseExpression();
        if (!match(")")) {
            throw new ParseException(") expected.", tokens.index);
        }
        return new Ast.Expression.Group(expr);
    }

    private Ast.Expression parseNot() throws ParseException {
        return new Ast.Expression.Not(parsePrimaryExpression());
    }

    public Ast.Reason parseReason() throws ParseException {
        if(!match(Token.Type.Reason))
            throw new ParseException("reason indicator (\\t) expected.", tokens.index);
        if(!match(Token.Type.IDENTIFIER))
            throw new ParseException("at least one identifier expected.", tokens.index);
        StringBuilder reason = new StringBuilder(tokens.get(-1).getLiteral());
        while(match(Token.Type.IDENTIFIER)) {
            reason.append(" ").append(tokens.get(-1).getLiteral());
        }
        List<Integer> lines = new ArrayList<>();
        if(match("(")) {
            while (!match(")")) {
                if (!match(Token.Type.Number)) {
                    throw new ParseException(") expected.", tokens.index);
                }
                lines.add(Integer.parseInt(tokens.get(-1).getLiteral()));
                if (!match(",") && !peek(")")) {
                    throw new ParseException(", expected.", tokens.index);
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
