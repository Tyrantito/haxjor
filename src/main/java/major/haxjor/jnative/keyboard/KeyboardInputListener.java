package major.haxjor.jnative.keyboard;

import major.haxjor.HaxJor;
import major.haxjor.script.HaxJorScript;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import static major.haxjor.HaxJorUtility.debug;

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
        final String keyPressed = NativeKeyEvent.getKeyText(e.getKeyCode());
//        if (keyPressed.toLowerCase().startsWith("Unknown") || keyPressed.equalsIgnoreCase("Period")
//                || keyPressed.equalsIgnoreCase("Backspace") || keyPressed.equalsIgnoreCase("Caps Lock")
//                || keyPressed.equalsIgnoreCase("Shift") || keyPressed.equalsIgnoreCase("Ctrl") ||
//                keyPressed.equalsIgnoreCase("Alt") || keyPressed.equalsIgnoreCase("Enter")
//                || keyPressed.equalsIgnoreCase("Quote") || keyPressed.equalsIgnoreCase("Context Menu")) {
//            return;
//        }
        //is not a char. FIXME this a cheaphax to allow a single-char
//        if (keyPressed.length() > 1)
//            return;

//        HaxJorScript script = HaxJor.KEYBOARD_SCRIPTS.get(NativeKeyEvent.getKeyText(e.getKeyCode()).charAt(0));
        HaxJorScript script = Keyboard.KEYBOARD_SCRIPTS.get(NativeKeyEvent.getKeyText(e.getKeyCode()));

        System.out.println("Key pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()) + " c: " + NativeKeyEvent.getKeyText(e.getKeyCode()).charAt(0) + "\n"
                + ((int) ' ') + " and: " + " " + e.getKeyChar() + " " + e.getKeyCode());
        if (script == null) {
//            System.out.println("No script found for: "+NativeKeyEvent.getKeyText(e.getKeyCode()));
//            System.out.println("Keywords: "+ Arrays.toString(HaxJor.KEYBOARD_SCRIPTS.keySet().toArray()));
            return;
        }
        //run the script
        HaxJor.runScript(script);
    }


    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
    }

}
