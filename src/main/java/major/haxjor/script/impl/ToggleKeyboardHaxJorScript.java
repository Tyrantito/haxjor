package major.haxjor.script.impl;

import major.haxjor.HaxJorUtility;
import major.haxjor.jnative.keyboard.KeyboardJNativeListener;
import major.haxjor.script.HaxJorScript;
import major.haxjor.script.HaxJorScriptSettings;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Toggle the availability of keyboard-related scripts.
 *
 * @author Major
 */
@HaxJorScriptSettings.SettingsFile(file = "toggle_script.toml")
public class ToggleKeyboardHaxJorScript extends HaxJorScriptSettings implements HaxJorScript, KeyboardJNativeListener {

    //the toggle state of keyboard scripts.
    public static final AtomicBoolean toggle = new AtomicBoolean(false);

    @Override
    public void initialize() {
        toggle.set(hjsp.getBoolean("keyboard.toggle"));

        HaxJorUtility.debug(toggle.get() + " is the toggle.");
    }

    @Override
    public void execute() {
        toggle.set(!toggle.get());
        System.out.println("Keyboard scripts are now: " + (toggle.get() ? "enabled" : "disabled"));
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public String indicator() {
        return null;
    }
}
