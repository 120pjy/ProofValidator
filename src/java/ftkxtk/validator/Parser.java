package ftkxtk.validator;

import java.util.ArrayList;
import java.util.List;

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

    public Ast.Expression parseImplicationExpression() throws ParseException {
        throw new UnsupportedOperationException(); //TODO
    }

    public Ast.Expression parseLogicalExpression() throws ParseException {
        throw new UnsupportedOperationException(); //TODO
    }

    public Ast.Expression parsePrimaryExpression() throws ParseException {
        throw new UnsupportedOperationException(); //TODO
    }

    public Ast.Reason parseReason() throws ParseException {
        throw new UnsupportedOperationException(); //TODO
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
