package major.haxjor.jnative.keyboard;

import major.haxjor.HaxJor;
import major.haxjor.script.HaxJorScript;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.Arrays;

/**
 * How should the program react to native keyboard input.
 *
 * @author Major
 */
public class KeyboardInputListener implements NativeKeyListener {

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {

    }


    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        HaxJorScript script = HaxJor.KEYBOARD_SCRIPTS.get(NativeKeyEvent.getKeyText(e.getKeyCode()).charAt(0));
        if (script == null) {
            System.out.println("No script found for: "+NativeKeyEvent.getKeyText(e.getKeyCode()));
            System.out.println("Keywords: "+ Arrays.toString(HaxJor.KEYBOARD_SCRIPTS.keySet().toArray()));
            return;
        }
        System.out.println("Script: "+script.getClass().getSimpleName()+" ");
        script.execute();
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
    }

}
