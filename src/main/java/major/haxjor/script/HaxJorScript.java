package major.haxjor.script;

import major.haxjor.HaxJorUtility;

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

    /**
     * Pends the thread for a given period in milliseconds.
     * This method must be carefully called since misuse might lead to
     * desynchronization issues, as this method trust that the thread its being called on
     * is the thread it needs to {@link Thread#sleep(long)} for, thus, if its being called
     * directly from an inappropriate thread, deadlocks or sync issues may occur.
     *
     * @param ms the milliseconds to 'delay' for.
     */
    default void pend(int ms) {
        try {
            HaxJorUtility.debug("Pending for: " + ms + "ms");
            synchronized (this) {
                Thread.sleep(ms);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
