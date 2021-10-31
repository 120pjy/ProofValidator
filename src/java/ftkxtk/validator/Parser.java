package ftkxtk.validator;

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
        throw new UnsupportedOperationException(); //TODO
    }

    public Ast.Statement.Declaration parseDeclarationStatement() throws ParseException {
        throw new UnsupportedOperationException(); //TODO
    }

    public Ast.Statement.Expression parseExpressionStatement() throws ParseException {
        throw new UnsupportedOperationException(); //TODO
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
        throw new UnsupportedOperationException(); //TODO (in lecture)
    }

    private boolean match(Object... patterns) {
        throw new UnsupportedOperationException(); //TODO (in lecture)
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
