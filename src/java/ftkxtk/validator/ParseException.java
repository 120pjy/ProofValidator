package ftkxtk.validator;

public final class ParseException extends RuntimeException {
    private final int line;
    private final int index;

    private final String lineString;

    public ParseException(String message, int line, int index) {
        super(message);
        this.line = line;
        this.index = index;
        lineString = null;
    }
    public ParseException(String message, int line, int index, String lineString) {
        super(message);
        this.line = line;
        this.index = index;
        this.lineString = lineString;
    }

    public int getLine() { return line; }

    public int getIndex() {
        return index;
    }

    public String getLineString() {
        return lineString;
    }

}
