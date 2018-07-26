package major.haxjor.jnative.keyboard;

import major.haxjor.jnative.JNativeListener;

/**
 * A keyboard listener that is activated when the {@link #indicator()} char is pressed.
 */
public interface KeyboardJNativeListener extends JNativeListener {

    /**
     * The button that indicates that a script should be executed.
     *
     * @return the name of the keystroke.
     */
    String indicator();
}
