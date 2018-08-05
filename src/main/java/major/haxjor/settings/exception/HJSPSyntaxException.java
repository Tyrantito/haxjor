package major.haxjor.settings.exception;

public final class HJSPSyntaxException extends RuntimeException {
    public HJSPSyntaxException(String e) {
        this(e, -1);
    }

    public HJSPSyntaxException(String e, int line) {
        super(e + (line == -1 ? " Exception at unknown line" : " Exception at line: " + line));
    }

    public HJSPSyntaxException(String e, Throwable throwable) {
        this(e, -1, throwable);
    }

    public HJSPSyntaxException(String e, int line, Throwable throwable) {
        super(e + (line == -1 ? " Exception at unknown line" : " Exception at line: " + line), throwable);
    }
}