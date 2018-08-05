package major.haxjor.settings.exception;

public  final class HJSPNonInitializedFieldException extends RuntimeException {
    public HJSPNonInitializedFieldException(String e) {
        this(e, -1);
    }

    public HJSPNonInitializedFieldException(String e, int line) {
        super(e + (line == -1 ? " Exception at unknown line" : " Exception at line: " + line));
    }

    public HJSPNonInitializedFieldException(String e, Throwable throwable) {
        this(e, -1, throwable);
    }

    public HJSPNonInitializedFieldException(String e, int line, Throwable throwable) {
        super(e + (line == -1 ? " Exception at unknown line" : " Exception at line: " + line), throwable);
    }
}


