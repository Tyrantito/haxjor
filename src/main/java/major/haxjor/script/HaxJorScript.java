package major.haxjor.script;

/**
 * A script that has settings.
 *
 * @author Major
 */
public interface HaxJorScript {

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
