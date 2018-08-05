package major.haxjor.settings.exception;

public final class HJSPFigureException extends RuntimeException {
    public HJSPFigureException(String e) {
        this(e, -1);
    }

    public HJSPFigureException(String e, int line) {
        super(e + (line == -1 ? " Exception at unknown line" : " Exception at line: " + line));
    }

    public HJSPFigureException(String e, Throwable throwable) {
        this(e, -1, throwable);
    }

    public HJSPFigureException(String e, int line, Throwable throwable) {
        super(e + (line == -1 ? " Exception at unknown line" : " " +
                " Exception at line: " + line), throwable);
    }
}