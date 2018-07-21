package major.haxjor.script.impl;

import major.haxjor.HaxJor;
import major.haxjor.jnative.keyboard.KeyboardJNativeListener;
import major.haxjor.script.HaxJorScript;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Toggle the availability of keyboard-related scripts.
 *
 * @author Major
 */
@HaxJorScript.HaxJorSettings(settingsFile = "toggle_script.toml")
public class ToggleKeyboardHaxJorScript implements HaxJorScript, KeyboardJNativeListener {

    //the toggle state of keyboard scripts.
    public static final AtomicBoolean toggle = new AtomicBoolean(false);


    @Override
    public void initialize() {
        if (HaxJor.debugMessages)
        System.out.println("turned on.");

        //TODO settings could be the activation key for this script.
    }

    @Override
    public void execute() {
        //toggle keyboard scripts
        toggle.set(!toggle.get());
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
