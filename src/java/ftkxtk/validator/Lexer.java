package ftkxtk.validator;

import java.util.List;

public final class Lexer {

    private final CharStream chars;

    public Lexer(String input) {
        chars = new CharStream(input);
    }

    public List<Token> lex() {
        throw new UnsupportedOperationException(); //TODO
    }

    public Token lexToken() {
        throw new UnsupportedOperationException(); //TODO
    }

    public Token lexIdentifier() {
        throw new UnsupportedOperationException(); //TODO
    }

    public Token lexNumber() {
        throw new UnsupportedOperationException(); //TODO
    }

    public Token lexOperator() {
        throw new UnsupportedOperationException(); //TODO
    }

    public boolean peek(String... patterns) {
        for (int i=0; i<patterns.length; i++) {
            if (!chars.has(i) || !String.valueOf(chars.get(i)).matches(patterns[i]))
                return false;
        }
        return true;
    }

    public boolean match(String... patterns) {
        boolean peek = peek(patterns);
        for (String str: patterns) {
        }
        if(peek) {
            for (int i=0; i<patterns.length; i++) {
                chars.advance();
            }
        }
        return peek;
    }
    
    public static final class CharStream {

        private final String input;
        private int index = 0;
        private int length = 0;

        public CharStream(String input) {
            this.input = input;
        }

        public boolean has(int offset) {
            return index + offset < input.length();
        }

        public char get(int offset) {
            return input.charAt(index + offset);
        }

        public void advance() {
            index++;
            length++;
        }

        public void skip() {
            length = 0;
        }

        public Token emit(Token.Type type) {
            int start = index - length;
            skip();
            return new Token(type, input.substring(start, index), start);
        }

    }

}
