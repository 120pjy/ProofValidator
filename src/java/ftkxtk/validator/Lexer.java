package ftkxtk.validator;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("StatementWithEmptyBody")
public final class Lexer {

    private final CharStream chars;

    public Lexer(String input) {
        chars = new CharStream(input);
    }

    public List<Token> lex() {
        List<Token> tokens = new ArrayList<>();
        while(chars.has(0)) {
            if (match("[ \n\r\b\t]")) {
                chars.skip();
            } else {
                tokens.add(lexToken());
            }
        }
        return tokens;
    }

    public Token lexToken() {
        if(match("[A-Za-z_]"))
            return lexIdentifier();
        else if(match("[0-9]")||match("[+\\-]", "[0-9]"))
            return lexNumber();
        return lexOperator();
    }

    public Token lexIdentifier() {
        while(match("[\\w_]")) {}
        return chars.emit(Token.Type.IDENTIFIER);
    }

    public Token lexNumber() {
        boolean dot = false;
        while(match("[\\d]") || match(".", "\\d")) {
            if (dot && chars.get(-1)=='.') {
                return chars.emit(Token.Type.Number);
            }
            if (chars.has(-2) && chars.get(-2)=='.') dot = true;
        }
        return chars.emit(Token.Type.Number);
    }

    public Token lexOperator() {
        if (match("\\\\")) {
            if(!chars.has(0))
                throw new ParseException("\\ cannot be used alone", chars.index);
            while(peek("[^ \b\n\r\t\\\\]")) { chars.advance(); }
        } else {
            if(!match("-",">") && !match("\\|","-") && !match("<","-",">"))
                chars.advance();
        }
        return chars.emit(Token.Type.OPERATOR);
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
