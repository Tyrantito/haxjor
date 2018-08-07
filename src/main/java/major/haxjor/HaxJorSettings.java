package major.haxjor;

import com.moandjiezana.toml.Toml;
import major.haxjor.settings.HJSP;

import java.nio.file.Paths;

/**
 * A static class that contains various settings
 *
 * @author Major
 */
public final class HaxJorSettings {

    /**
     * Some static settings for the system.
     */
    private static final HJSP SETTINGS_PARSER = HJSP.of((Paths.get(".", "settings.haxjor").toString())).parse();
    /**
     * Scripts can not run concurrently, thus, we could either queue submitted scripts and run them sequentially
     * or rather deny them when submitted.
     */
    static final boolean SCRIPT_QUEUEING = SETTINGS_PARSER.getBoolean("config.script_queue");

    /**
     * Should debug messages appear in console? This should only be enabled when required, because of the spam.
     */
    public static final boolean DEBUG_MESSAGES = SETTINGS_PARSER.getBoolean("config.debug_messages");

    /**
     * Uninstanceable class
     */
    private HaxJorSettings() {
        throw new UnsupportedOperationException();
    }
}
