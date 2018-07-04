package major.haxjor.jnative.keyboard;

import major.haxjor.HaxJor;
import major.haxjor.script.HaxJorScript;
import major.haxjor.script.impl.ToggleKeyboardHaxJorScript;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * How should the program react to native keyboard input.
 *
 * @author Major
 */
public class KeyboardInputListener implements NativeKeyListener {

    //are we currently running any script?
    public static boolean firingEvents;

    //queue of pending scripts.
    private static final Queue<HaxJorScript> SCRIPT_QUEUE = new ArrayDeque<>();

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
        //is not a char.
        if (keyPressed.length() > 1)
            return;

        HaxJorScript script = HaxJor.KEYBOARD_SCRIPTS.get(NativeKeyEvent.getKeyText(e.getKeyCode()).charAt(0));
        System.out.println("Key pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()) + " c: " + NativeKeyEvent.getKeyText(e.getKeyCode()).charAt(0));
        System.out.println(((int) ' ') + " and: " + " " + e.getKeyChar() + " " + e.getKeyCode());
        if (script == null) {
//            System.out.println("No script found for: "+NativeKeyEvent.getKeyText(e.getKeyCode()));
//            System.out.println("Keywords: "+ Arrays.toString(HaxJor.KEYBOARD_SCRIPTS.keySet().toArray()));
            return;
        }
        //run the script
        runScript(script);
    }

    //attempt to run the script, and then handle the queue.
    private static void runScript(HaxJorScript script) {
        if (firingEvents) {
            System.out.println("Queued action instead: " + script.getClass().getSimpleName());
            SCRIPT_QUEUE.add(script);
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
            System.out.println((ToggleKeyboardHaxJorScript.toggle.get() ? "Keyboard scripts are disabled" : "Specific script is disabled") + ".");
            return;
        }

        firingEvents = true;
        System.out.println("Script: " + script.getClass().getSimpleName() + " ");
        script.execute();
        firingEvents = false;

        if (!SCRIPT_QUEUE.isEmpty()) {
            runScript(SCRIPT_QUEUE.poll());
            System.out.println("Running script from queue...");
        }
//        System.out.println("Disabled.");
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
    }

}
