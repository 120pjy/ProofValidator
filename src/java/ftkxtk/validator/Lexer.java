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
            if (match("[ \r\b\t]")) {
                chars.skip();
            }
            else if (match("[\n]")) {
                chars.addLine();
                chars.skip();
            } else if (match("/","/")) {
                while (match("[^\n]")) {}
                chars.skip();
            }else {
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
        while(match("[\\d]")) {}
        return chars.emit(Token.Type.Number);
    }

    public Token lexOperator() {
        if (match("\\\\")) {
            if(!chars.has(0)) {
                StringBuilder lineString = new StringBuilder();
                chars.index = chars.lineStartIndex;
                while (!match("\n")) {
                    lineString.append(chars.get(0));
                    chars.advance();
                }
                throw new ParseException("\\ cannot be used alone", chars.getLine(), chars.index, lineString.toString());
            }
            while(peek("[^ \b\r\t\\\\]")) { chars.advance(); }
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
        private int line = 1;
        private int lineIndex = 0;
        private int lineStartIndex = 0;

        public CharStream(String input) {
            this.input = input;
        }

        public boolean has(int offset) {
            return index + offset < input.length();
        }

        public char get(int offset) {
            return input.charAt(index + offset);
        }

        public int getLine() {return line;}

        public void addLine() { line ++; lineIndex = 0; lineStartIndex = index;}

        public void advance() {
            index++;
            lineIndex++;
            length++;
        }

        public void skip() {
            length = 0;
        }

        public Token emit(Token.Type type) {
            int start = index - length;
            skip();
            return new Token(type, input.substring(start, index), line, lineIndex);
        }

    }
}
