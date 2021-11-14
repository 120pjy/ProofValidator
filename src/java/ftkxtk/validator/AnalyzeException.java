package ftkxtk.validator;

public final class AnalyzeException extends RuntimeException {

    private final String position;

    public AnalyzeException(String message, String position) {
        super(message);
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

}
