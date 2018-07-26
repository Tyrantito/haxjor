package major.haxjor.script;

import com.moandjiezana.toml.Toml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Optional settings accessibility.
 *
 * @author Major
 */
public abstract class HaxJorScriptSettings {

    /**
     * The base path for every script.
     */
    public static final Path SCRIPT_BASE_PATH = Paths.get(".", "data", "script_settings");

    /**
     * The file name that will be resolved through the {@link #SCRIPT_BASE_PATH} path.
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SettingsFile {
        String file();
    }

    /**
     * The toml instance.
     */
    protected Toml toml;

    /**
     * Build the TOML by the file.
     *
     * @param file
     */
    public final HaxJorScriptSettings build(final String file) {
        toml = new Toml().read(HaxJorScriptSettings.SCRIPT_BASE_PATH.resolve((file)).toFile());
        return this;
    }

}