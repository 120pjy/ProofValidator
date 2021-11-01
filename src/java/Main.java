import ftkxtk.validator.Lexer;
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
        Lexer lexer = new Lexer("#1 Let a = b\tModus Ponens\n#2 Let b = c\tReason2\n#3 Then a = c\tReason3\n");
        System.out.println(lexer.lex());
    }
}