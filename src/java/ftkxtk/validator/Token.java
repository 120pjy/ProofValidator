package ftkxtk.validator;

public final class Token {

    public enum Type {
        IDENTIFIER,
        OPERATOR,
        Number,
    }

    private final Type type;
    private final String literal;
    private final int line;
    private final int index;

    public Token(Type type, String literal, int line, int index) {
        this.type = type;
        this.literal = literal;
        this.line = line;
        this.index = index;
    }

    public Type getType() {
        return type;
    }

    public String getLiteral() {
        return literal;
    }

    public int getLine() {
        return line;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Token
                && type == ((Token) obj).type
                && literal.equals(((Token) obj).literal)
                && line == ((Token) obj).line
                && index == ((Token) obj).index;
    }

    @Override
    public String toString() {
        return type + "=" + literal + "@line: " + line + " index: " + index;
    }

}
