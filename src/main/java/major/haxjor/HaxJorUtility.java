package major.haxjor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Some static utility functions
 *
 * @author Major
 */
public final class HaxJorUtility {

    private HaxJorUtility() {
        throw new UnsupportedOperationException();
    }

    /**
     * A shortened method for a simple expression, whether to send a debug message or not.
     * TODO this could be expanded by having debug levels
     *
     * @param message the message to display
     */
    public static void debug(String message) {
        debug(() -> System.out.println(message));
    }

    public static void debug(Runnable runnable) {
        if (HaxJorSettings.DEBUG_MESSAGES) {
            runnable.run();
        }
    }

    /**
     * A method for calculating elapsed milliseconds by nanoseconds, converted using {@link TimeUnit}.
     *
     * @param startNano the nanoseconds on the start.
     * @return elapsed milliseconds since startNano
     */
    static long elapsedMs(long startNano) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNano);
    }

    //imported: NativeHookDemo.class
    static final class LogFormatter extends Formatter {

        LogFormatter() {
        }

        public String format(LogRecord var1) {
            StringBuilder var2 = new StringBuilder();
            var2.append(new Date(var1.getMillis())).append(" ").append(var1.getLevel().getLocalizedName()).append(":\t").append(this.formatMessage(var1));
            if (var1.getThrown() != null) {
                try {
                    StringWriter var3 = new StringWriter();
                    PrintWriter var4 = new PrintWriter(var3);
                    var1.getThrown().printStackTrace(var4);
                    var4.close();
                    var2.append(var3.toString());
                    var3.close();
                } catch (Exception ignored/*var5*/) {
                }
            }

            return var2.toString();
        }
    }
}
