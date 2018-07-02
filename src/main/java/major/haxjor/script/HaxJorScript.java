package major.haxjor.script;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A script that has settings.
 * @author Major
 */
public interface HaxJorScript {

    /**
     * The base path for every script.
     */
    Path SCRIPT_BASE_PATH = Paths.get(".", "data", "script_settings");

    /**
     * The settings path for this script.
     */
    Path settings();

    /**
     * Init the script
     */
    void initialize();

    /**
     * The action that is executed when the script is triggered.
     */
    boolean execute();

    /**
     * Is this script enabled? Allows easy toggling for features.
     */
    boolean enabled();
}
