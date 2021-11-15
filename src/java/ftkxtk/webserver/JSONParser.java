package ftkxtk.webserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JSONParser {
    private String input;
    private List<Token> tokens;
    private int cursor;
    private int length;

    private class Token {
        private Type type;
        private String value;

        public enum Type {
            IDENTIFIER,
            OPERATOR,
            STRING
        }

        public Token(Type type, String value) {
            this.type = type;
            this.value = value;
        }

        public Type getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Token{" +
                    "type=" + type +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public JSONParser() {

    }

    private void lex(String input) {
        tokens = new ArrayList<>();
        this.input = input;
        this.cursor = 0;
        this.length = 0;

        while(has(0)) {
            if (matchString("[ \b\n\r\t]")) {
                skip();
                continue;
            }
            if (matchString("\"")) {
                while (!matchString("\"")) {
                    advance();
                }
                tokens.add(new Token(Token.Type.STRING, input.substring(cursor - length + 1, cursor - 1).replace("\\n", "\n").replace("\\r", "\r").replace("\\t", "\t").replace("\\b", "\b")));
            } else {
                advance();
                tokens.add(new Token(Token.Type.OPERATOR, input.substring(cursor - length, cursor)));
            }
            skip();
        }

        System.out.println(tokens);
    }

    private JSONObject parseObject() {
        JSONObject obj = new JSONObject();
        if(!match("{")) throw new RuntimeException("{ expected");
        while(!peek("}")) {
            if (!match(Token.Type.STRING)) {
                throw new RuntimeException("name expected");
            }
            if (!match(":")) {
                throw new RuntimeException(": expected");
            }
            if (!match(Token.Type.STRING)) {
                throw new RuntimeException("value expected");
            }

            obj.set(getToken(-3).getValue(), getToken(-1).getValue());
            if (!match(",") && !peek("}")) {
                throw new RuntimeException(", or } expected.");
            }
        }
        if(!match("}")) throw new RuntimeException("} expected");
        return obj;
    }

    private JSONObject parseArray() {
        return new JSONObject();
    }

    private JSONObject parse() {
        this.cursor = 0;
        return parseObject();
    }

    public JSONObject parse(String input) {
        lex(input);
        return parse();
    }

    private void skip() {
        length = 0;
    }

    private void advance() {
        cursor ++;
        length ++;
    }

    public boolean has(int offset) {
        return cursor + offset < input.length();
    }

    public char getChar(int offset) {
        return input.charAt(cursor + offset);
    }

    public Token getToken(int offset) {
        return tokens.get(cursor + offset);
    }

    public boolean peekString(String... patterns) {
        for (int i=0; i<patterns.length; i++) {
            if (!has(i) || !String.valueOf(getChar(i)).matches(patterns[i]))
                return false;
        }
        return true;
    }

    public boolean matchString(String... patterns) {
        boolean peek = peekString(patterns);
        if(peek) {
            for (int i=0; i<patterns.length; i++) {
                advance();
            }
        }
        return peek;
    }

    private boolean peek(Object... patterns) {
        for (int i=0; i<patterns.length; i++) {
            if (!has(i)) {
                return false;
            }
            else if (patterns[i] instanceof Token.Type) {
                System.out.println(getToken(i));
                if (patterns[i] != getToken(i).getType()) {
                    return false;
                }
            }
            else if (patterns[i] instanceof String) {
                if (!patterns[i].equals(getToken(i).getValue())) {
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
                advance();

        return peek;
    }
}
