package ftkxtk.validator;

public final class AnalyzeException extends RuntimeException {

    private final String position;
    private final String ast;

    public AnalyzeException(String message, String position, Ast ast) {
        super(message);
        this.position = position;
        this.ast = ast.toString();
    }

    public String getAst() { return ast; }
    public String getPosition() {
        return position;
    }

}
