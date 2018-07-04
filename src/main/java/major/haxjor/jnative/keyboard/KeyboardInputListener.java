package major.haxjor.jnative.keyboard;

import major.haxjor.HaxJor;
import major.haxjor.script.HaxJorScript;
import major.haxjor.script.impl.ToggleKeyboardHaxJorScript;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.Arrays;

/**
 * How should the program react to native keyboard input.
 *
 * @author Major
 */
public class KeyboardInputListener implements NativeKeyListener {

    //are we currently running any script?
    public static boolean firingEvents;

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (firingEvents) {
            return;
        }
        HaxJorScript script = HaxJor.KEYBOARD_SCRIPTS.get(NativeKeyEvent.getKeyText(e.getKeyCode()).charAt(0));
        if (script == null) {
//            System.out.println("No script found for: "+NativeKeyEvent.getKeyText(e.getKeyCode()));
//            System.out.println("Keywords: "+ Arrays.toString(HaxJor.KEYBOARD_SCRIPTS.keySet().toArray()));
            return;
        }
        //execute this specific script regardless of its availability.
        if (script instanceof ToggleKeyboardHaxJorScript) {
            firingEvents = true;
            script.execute();
            firingEvents = false;
            return;
        }
        //if either keyboard scripts are disabled or the specific script is disabled.
        if (!ToggleKeyboardHaxJorScript.toggle.get() || !script.enabled()) {
            System.out.println((ToggleKeyboardHaxJorScript.toggle.get() ? "Keyboard scripts are disabled" : "Specific script is disabled")+".");
            return;
        }

        firingEvents = true;
        System.out.println("Script: "+script.getClass().getSimpleName()+" ");
        script.execute();
        firingEvents = false;
        System.out.println("Disabled.");
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
    }

}
