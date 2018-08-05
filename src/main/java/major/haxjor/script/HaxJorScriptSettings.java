package major.haxjor.script;

import major.haxjor.settings.HJSP;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional settings implementation for a script..
 *
 * @author Major
 */
public abstract class HaxJorScriptSettings {

    /**
     * The file name that will be resolved through the {@link HJSP#SCRIPT_BASE_PATH} path.
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SettingsFile {
        String file();
    }

    /**
     * The toml instance.
     */
    protected HJSP hjsp;

    /**
     * Build the TOML by the file.
     *
     * @param file the file name of the setting file.
     */
//    public final void build(final String file) {
//        if (!file.endsWith(".toml")) {
//            throw new IllegalArgumentException("File must be .toml");
//        }
//        toml = new Toml().read(HaxJorScriptSettings.SCRIPT_BASE_PATH.resolve(file).toFile());
//    }

}