package ftkxtk.validator;

public final class AnalyzeException extends RuntimeException {

    private final String position;
    private final Ast actual;
    private final Ast expected;

    public AnalyzeException(String message, String position, Ast actual) {
        super(message);
        this.position = position;
        this.actual = actual;
        this.expected = null;
    }

    public AnalyzeException(String message, String position, Ast actual, Ast expected) {
        super(message);
        this.position = position;
        this.actual = actual;
        this.expected = expected;
    }

    public String getPosition() {
        return position;
    }
    public String getActual() {
        return actual.toString();
    }
    public String getExpected() {
        if (expected == null)
            return null;
        return expected.toString();
    }

}
