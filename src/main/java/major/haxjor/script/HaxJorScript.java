package major.haxjor.script;

import com.moandjiezana.toml.Toml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A script that has settings.
 * @author Major
 */
public interface HaxJorScript {

    /**
     * Optional settings accessibility.
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface HaxJorSettings {
        String settingsFile();
    }

    /**
     * The base path for every script.
     */
     Path SCRIPT_BASE_PATH = Paths.get(".", "data", "script_settings");

    /**
     * Init the script
     */
    void initialize();

    /**
     * The action that is executed when the script is triggered.
     */
    void execute();

    /**
     * Is this script enabled? Allows easy toggling for features.
     */
    boolean enabled();

}
