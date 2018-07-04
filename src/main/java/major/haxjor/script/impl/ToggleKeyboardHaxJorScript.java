package major.haxjor.script.impl;

import major.haxjor.jnative.keyboard.KeyboardJNativeListener;
import major.haxjor.script.HaxJorScript;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Toggle the availability of keyboard-related scripts.
 * @author Major
 */
public class ToggleKeyboardHaxJorScript implements HaxJorScript, KeyboardJNativeListener {

    //the toggle state of keyboard scripts.
    public static final AtomicBoolean toggle = new AtomicBoolean(true);
    @Override
    public Path settings() {
        return null;
    }

    @Override
    public void initialize() {
        System.out.println("turned on.");

        //TODO settings could be the activation key for this script.
    }

    @Override
    public boolean execute() {
        //toggle keyboard scripts
        toggle.set(!toggle.get());
        return true;
    }

    //this is always enabled, because its a toggle and when its disabled it has to still work so it can be enabled.
    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public char indicator() {
        return 0;
    }
}
