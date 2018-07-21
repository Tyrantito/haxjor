package major.haxjor.script.impl.chat;

import major.haxjor.jnative.keyboard.KeyboardJNativeListener;
import major.haxjor.script.HaxJorScript;

/**
 * Script that automatically writes text
 * @author Major
 */
public class ChatHaxJorScript implements HaxJorScript, KeyboardJNativeListener {

    @Override
    public char indicator() {
        return 0;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void execute() {

    }

    @Override
    public boolean enabled() {
        return false;
    }
}
